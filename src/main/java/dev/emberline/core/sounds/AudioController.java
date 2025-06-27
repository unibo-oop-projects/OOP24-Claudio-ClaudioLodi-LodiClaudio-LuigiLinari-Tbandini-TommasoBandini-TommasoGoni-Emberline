package dev.emberline.core.sounds;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.event.EventDispatcher;
import dev.emberline.core.event.EventHandler;
import dev.emberline.core.sounds.event.SetVolumeEvent;
import dev.emberline.core.sounds.event.SfxSoundEvent;
import dev.emberline.core.sounds.event.SfxSoundEvent.SoundType;
import dev.emberline.core.sounds.event.ToggleMuteEvent;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.prefs.Preferences;

public class AudioController implements EventListener {

    private static final String METADATA_PATH = "/audio/audioController.json";
    private static final Preferences prefs = Preferences.userRoot().node("dev.emberline.audio");
    private Media musicMedia;
    private final MediaPlayer musicPlayer;
    private static Metadata metadata;
    private final Map<SoundType, Media> cachedSfxMedia = new HashMap<>();

    private record Metadata (
        @JsonProperty String MUSIC_PATH,
        @JsonProperty Map<SoundType, String> SFX_PATHS,
        @JsonProperty double MUSIC_VOLUME,
        @JsonProperty double SFX_VOLUME,
        @JsonProperty boolean MUSIC_MUTE,
        @JsonProperty boolean SFX_MUTE,
        @JsonProperty double STEP_VOLUME_AMOUNT
    ) {}

    public AudioController() {
        EventDispatcher.getInstance().registerListener(this);
        metadata = ConfigLoader.loadConfig(METADATA_PATH, Metadata.class);

        loadSoundtrack();
        musicPlayer = new MediaPlayer(musicMedia);
        initializeSoundtrack();
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
    public static void requestSfxSound(Object src, final SoundType soundType) {
        EventDispatcher.getInstance().dispatchEvent(new SfxSoundEvent(src, soundType));
    }


    public static void requestDecreaseMusicVolume(Object src) {
        double newVolume = Math.max(0, prefs.getDouble("musicVolume", metadata.MUSIC_VOLUME) - metadata.STEP_VOLUME_AMOUNT);
        prefs.putDouble("musicVolume", newVolume);
        System.out.println("Decreasing music volume to: " + newVolume);
        EventDispatcher.getInstance().dispatchEvent(new SetVolumeEvent(src, newVolume));
    }

    public static void requestIncreaseMusicVolume(Object src) {
        double newVolume = Math.min(1, prefs.getDouble("musicVolume", metadata.MUSIC_VOLUME) + metadata.STEP_VOLUME_AMOUNT);
        prefs.putDouble("musicVolume", newVolume);
        System.out.println("Decreasing music volume to: " + newVolume);
        EventDispatcher.getInstance().dispatchEvent(new SetVolumeEvent(src, newVolume));
    }

    private void initializeSoundtrack() {
        musicPlayer.setStartTime(Duration.ZERO);
        musicPlayer.setStopTime(musicMedia.getDuration());
        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        musicPlayer.setVolume(loadMusicVolume());
        musicPlayer.setMute(metadata.MUSIC_MUTE);
    }

    private void loadSoundtrack() {
        final URL fileURL = Objects.requireNonNull(getClass().getResource(metadata.MUSIC_PATH));
        musicMedia = new Media(fileURL.toExternalForm());
    }

    private Media getCachedSfxMedia(final SfxSoundEvent event) {
        final SoundType type = event.getSoundType();

        return cachedSfxMedia.computeIfAbsent(type, t -> {
            final String soundPath = metadata.SFX_PATHS.get(t);
            if (soundPath == null) {
                throw new IllegalArgumentException("Sound type " + t + " not found in metadata.");
            }
            final URL fileURL = Objects.requireNonNull(getClass().getResource(soundPath));
            return new Media(fileURL.toExternalForm());
        });
    }

    private void playSfx(final SfxSoundEvent event) {
        final Media sfxMedia = getCachedSfxMedia(event);
        final MediaPlayer sfxPlayer = new MediaPlayer(sfxMedia);
        sfxPlayer.setVolume(metadata.SFX_VOLUME);
        sfxPlayer.setMute(metadata.SFX_MUTE);
        sfxPlayer.play();
    }

    @EventHandler
    @SuppressWarnings("unused") // This method is used by the EventDispatcher and should not be removed.
    private void handleSFXSoundEvent(final SfxSoundEvent event) {
        Platform.runLater(() -> {
            playSfx(event);
        });
    }

    @EventHandler
    @SuppressWarnings("unused") // This method is used by the EventDispatcher and should not be removed.
    private void handleSetMusicVolumeEvent(final SetVolumeEvent event) {
        Platform.runLater(() -> {
            musicPlayer.setVolume(event.getVolume());
        });
    }

    @EventHandler
    @SuppressWarnings("unused") // This method is used by the EventDispatcher and should not be removed.
    private void handleToggleMusicMuteEvent(final ToggleMuteEvent event) {
        Platform.runLater(() -> {
            // musicPlayer.setMute(event.getMuteState());
        });
    }

    @EventHandler
    @SuppressWarnings("unused") // This method is used by the EventDispatcher and should not be removed.
    private void handleSetSfxVolumeEvent(final SetVolumeEvent event) {
        Platform.runLater(() -> {
            //metadata.SFX_VOLUME = event.getVolume();
            cachedSfxMedia.clear(); // Clear cache to reload SFX with new volume
        });
    }

    @EventHandler
    @SuppressWarnings("unused") // This method is used by the EventDispatcher and should not be removed.
    private void handleToggleSfxMuteEvent(final ToggleMuteEvent event) {
        Platform.runLater(() -> {
            //metadata.SFX_MUTE = event.getMuteState();
            cachedSfxMedia.clear(); // Clear cache to reload SFX with new mute state
        });
    }

    private double loadMusicVolume() {
        return prefs.getDouble("musicVolume", metadata.MUSIC_VOLUME);
    }

    private double loadSfxVolume() {
        return prefs.getDouble("sfxVolume", metadata.SFX_VOLUME);
    }

    private boolean loadMusicMute() {
        return prefs.getBoolean("musicMute", metadata.MUSIC_MUTE);
    }

    private boolean loadSfxMute() {
        return prefs.getBoolean("sfxMute", metadata.SFX_MUTE);
    }
}
