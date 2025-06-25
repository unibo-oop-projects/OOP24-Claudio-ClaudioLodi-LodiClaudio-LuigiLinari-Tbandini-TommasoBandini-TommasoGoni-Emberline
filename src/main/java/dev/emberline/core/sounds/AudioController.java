package dev.emberline.core.sounds;

import java.net.URL;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.emberline.core.ConfigLoader;
import dev.emberline.core.event.EventDispatcher;
import dev.emberline.core.event.EventHandler;
import dev.emberline.core.sounds.event.SetVolumeEvent;
import dev.emberline.core.sounds.event.SfxSoundEvent;
import dev.emberline.core.sounds.event.ToggleMuteEvent;
import dev.emberline.core.sounds.event.SfxSoundEvent.SoundType;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class AudioController implements EventListener {

    private static final String METADATA_PATH = "/audio/audioController.json";
    private Media musicMedia;
    private final MediaPlayer mediaPlayer;
    private static Metadata metadata;
    private final Map<SoundType, Media> cachedSfxMedia = new HashMap<>();

    private record Metadata (
        @JsonProperty String MUSIC_PATH,
        @JsonProperty Map<SoundType, String> SFX_PATHS,
        @JsonProperty double MUSIC_VOLUME,
        @JsonProperty double SFX_VOLUME,
        @JsonProperty boolean MUSIC_MUTE,
        @JsonProperty boolean SFX_MUTE
    ) {}

    public AudioController() {
        EventDispatcher.getInstance().registerListener(this);
        metadata = ConfigLoader.loadConfig(METADATA_PATH, Metadata.class);
        
        loadSoundtrack();
        mediaPlayer = new MediaPlayer(musicMedia);
        initializeSoundtrack();
    }

    public void startSoundtrack() {
        if (!mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            mediaPlayer.play();
        }
    }

    private void initializeSoundtrack() {
        mediaPlayer.setStartTime(Duration.ZERO);
        mediaPlayer.setStopTime(musicMedia.getDuration());
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(metadata.MUSIC_VOLUME);
        mediaPlayer.setMute(metadata.MUSIC_MUTE);
    }

    private void loadSoundtrack() {
        final URL fileURL = Objects.requireNonNull(getClass().getResource(metadata.MUSIC_PATH));
        musicMedia = new Media(fileURL.toExternalForm());
    }

    private Media getCachedSfxMedia(final SfxSoundEvent event) {
        SoundType type = event.getSoundType();

        return cachedSfxMedia.computeIfAbsent(type, t -> {
            String soundPath = metadata.SFX_PATHS.get(t);
            if (soundPath == null) {
                throw new IllegalArgumentException("Sound type " + t + " not found in metadata.");
            }
            URL fileURL = Objects.requireNonNull(getClass().getResource(soundPath));
            return new Media(fileURL.toExternalForm());
        });
    }

    private void playSfx(final SfxSoundEvent event) {
        Media sfxMedia = getCachedSfxMedia(event);
        MediaPlayer sfxPlayer = new MediaPlayer(sfxMedia);
        sfxPlayer.setVolume(metadata.SFX_VOLUME);
        sfxPlayer.setMute(metadata.SFX_MUTE);
        sfxPlayer.play();
    }

    @EventHandler
    private void handleSFXSoundEvent(final SfxSoundEvent event) {
        Platform.runLater(() -> {
            playSfx(event);
        });
    }

    @EventHandler
    private void handleToggleMuteEvent(final ToggleMuteEvent event) {
        Platform.runLater(() -> {
            mediaPlayer.setMute(event.muteState());
        });
    }

    @EventHandler
    private void handleSetVolumeEvent(final SetVolumeEvent event) {
        Platform.runLater(() -> {
            mediaPlayer.setVolume(event.getVolume());
        });
    }

}
