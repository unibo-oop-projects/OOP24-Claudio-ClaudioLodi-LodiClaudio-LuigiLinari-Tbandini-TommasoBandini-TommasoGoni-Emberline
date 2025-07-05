package dev.emberline.core.sounds;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.config.ConfigLoader;
import dev.emberline.core.event.EventDispatcher;
import dev.emberline.core.event.EventHandler;
import dev.emberline.core.sounds.event.RefreshSfxVolumeEvent;
import dev.emberline.core.sounds.event.SetMusicVolumeEvent;
import dev.emberline.core.sounds.event.SfxSoundEvent;
import dev.emberline.core.sounds.event.SfxSoundEvent.SoundType;
import dev.emberline.core.sounds.event.ToggleMusicMuteEvent;
import dev.emberline.core.sounds.event.ToggleSfxMuteEvent;
import dev.emberline.preferences.PreferenceKey;
import dev.emberline.preferences.PreferencesManager;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.EnumMap;
import java.util.EventListener;
import java.util.Map;
import java.util.Objects;

/**
 * Handles game audio playback, including background music and sound effects.
 * Integrates with the event system to respond to volume and mute changes.
 */
public final class AudioController implements EventListener {
    private static final String METADATA_PATH = "/audio/audioController.json";
    private Media musicMedia;
    private final MediaPlayer musicPlayer;
    private static final Metadata METADATA = ConfigLoader.loadConfig(METADATA_PATH, Metadata.class);
    private final Map<SoundType, Media> cachedSfxMedia = new EnumMap<>(SoundType.class);

    private record Metadata(
        @JsonProperty String MUSIC_PATH,
        @JsonProperty Map<SoundType, String> SFX_PATHS,
        @JsonProperty double STEP_VOLUME_AMOUNT
    ) {
    }

    /**
     * Constructs an AudioController instance.
     * This constructor initializes the audio controller, loads the soundtrack,
     * and sets up the media player for the soundtrack.
     */
    public AudioController() {
        registerEvents();

        loadSoundtrack();
        musicPlayer = new MediaPlayer(musicMedia);
        initializeSoundtrack();
    }

    private void registerEvents() {
        EventDispatcher.getInstance().registerListener(this);
    }

    /**
     * Starts the soundtrack if it is not already playing.
     * This method is called when the game starts.
     */
    public void startSoundtrack() {
        if (!musicPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            musicPlayer.play();
        }
    }

    /**
     * Requests a sound effect to be played.
     * This method dispatches an event that will be handled by the audio controller.
     * @param src the source of the event, typically the object that requested the sound
     * @param soundType the type of sound effect to play
     */
    public static void requestSfxSound(final Object src, final SoundType soundType) {
        EventDispatcher.getInstance().dispatchEvent(new SfxSoundEvent(src, soundType));
    }

    /**
     * Requests to set the music volume.
     * This method dispatches an event that will be handled by the audio controller.
     * @param src the source of the event, typically the object that requested the volume change
     * @param newVolume the new volume level for the music
     */
    public static void requestSetMusicVolume(final Object src, final double newVolume) {
        EventDispatcher.getInstance().dispatchEvent(new SetMusicVolumeEvent(src, newVolume));
    }

    /**
     * Requests to toggle the music mute state.
     * This method dispatches an event that will be handled by the audio controller.
     * @param src the source of the event, typically the object that requested the mute toggle
     * @param newMuteState the new mute state for the music
     */
    public static void requestToggleMusicMute(final Object src, final boolean newMuteState) {
        EventDispatcher.getInstance().dispatchEvent(new ToggleMusicMuteEvent(src, newMuteState));
    }

    /**
     * Requests to set the sound effects volume.
     * This method dispatches an event that will be handled by the audio controller.
     * @param src the source of the event, typically the object that requested the volume change
     */
    public static void requestSetSfxVolume(final Object src) {
        EventDispatcher.getInstance().dispatchEvent(new RefreshSfxVolumeEvent(src));
    }

    /**
     * Requests to toggle the sound effects mute state.
     * This method dispatches an event that will be handled by the audio controller.
     * @param src the source of the event, typically the object that requested the mute toggle
     */
    public static void requestToggleSfxMute(final Object src) {
        EventDispatcher.getInstance().dispatchEvent(new ToggleSfxMuteEvent(src));
    }

    private void initializeSoundtrack() {
        musicPlayer.setStartTime(Duration.ZERO);
        musicPlayer.setStopTime(musicMedia.getDuration());
        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        musicPlayer.setVolume(PreferencesManager.getDoublePreference(PreferenceKey.MUSIC_VOLUME));
        musicPlayer.setMute(PreferencesManager.getBooleanPreference(PreferenceKey.MUSIC_MUTE));
    }

    private void loadSoundtrack() {
        final URL fileURL = Objects.requireNonNull(AudioController.class.getResource(METADATA.MUSIC_PATH));
        musicMedia = new Media(fileURL.toExternalForm());
    }

    private Media getCachedSfxMedia(final SfxSoundEvent event) {
        final SoundType type = event.getSoundType();

        return cachedSfxMedia.computeIfAbsent(type, t -> {
            final String soundPath = METADATA.SFX_PATHS.get(t);
            if (soundPath == null) {
                throw new IllegalArgumentException("Sound type " + t + " not found in METADATA.");
            }
            final URL fileURL = Objects.requireNonNull(getClass().getResource(soundPath));
            return new Media(fileURL.toExternalForm());
        });
    }

    private void playSfx(final SfxSoundEvent event) {
        final Media sfxMedia = getCachedSfxMedia(event);
        final MediaPlayer sfxPlayer = new MediaPlayer(sfxMedia);
        sfxPlayer.setVolume(PreferencesManager.getDoublePreference(PreferenceKey.SFX_VOLUME));
        sfxPlayer.setMute(PreferencesManager.getBooleanPreference(PreferenceKey.SFX_MUTE));
        sfxPlayer.play();
    }

    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleSFXSoundEvent(final SfxSoundEvent event) {
        Platform.runLater(() -> {
            playSfx(event);
        });
    }

    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleSetMusicVolumeEvent(final SetMusicVolumeEvent event) {
        Platform.runLater(() -> {
            if (!musicPlayer.isMute()) {
                musicPlayer.setVolume(event.getVolume());
            }
        });
    }

    /**
     * Since JavaFx MediaPlayer.setMute() does not take the volume change while muted into account,
     * we need to set the volume again after toggling mute, getting the new volume from the preferences.
     * @param event the {@link ToggleMusicMuteEvent} with the attached mute state
     */
    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleToggleMusicMuteEvent(final ToggleMusicMuteEvent event) {
        Platform.runLater(() -> {
            final double musicVolume = PreferencesManager.getDoublePreference(PreferenceKey.MUSIC_VOLUME);
            musicPlayer.setMute(event.isMuted());
            musicPlayer.setVolume(musicVolume);
        });
    }

    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleSetSfxVolumeEvent(final SetMusicVolumeEvent event) {
        Platform.runLater(() -> {
            cachedSfxMedia.clear(); // Clear cache to reload SFX with new volume
        });
    }

    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleToggleSfxMuteEvent(final ToggleMusicMuteEvent event) {
        Platform.runLater(() -> {
            cachedSfxMedia.clear(); // Clear cache to reload SFX with new mute state
        });
    }
}
