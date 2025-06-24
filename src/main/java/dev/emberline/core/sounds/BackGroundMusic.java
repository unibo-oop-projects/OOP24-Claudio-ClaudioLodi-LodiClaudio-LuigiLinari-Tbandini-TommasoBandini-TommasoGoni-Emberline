package dev.emberline.core.sounds;

import javafx.scene.media.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.Objects;

/**
 * This class is responsible for the soundtrack playing in loop in this game.
 */
public class BackGroundMusic {

    private static final String musicFile = "/audio/emberlineSoundtrack.wav";
            //"/sounds/test.mp3";
    private Media media;
    private final MediaPlayer mediaPlayer;

    /**
     * Loads the song and sets the player to loop on it.
     */
    public BackGroundMusic() {
        loadSong();
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setStartTime(Duration.ZERO);
        mediaPlayer.setStopTime(media.getDuration());
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }

    /**
     * Starts playing the song.
     */
    public void start() {
        mediaPlayer.play();
    }

    private void loadSong() {
        URL fileURL = Objects.requireNonNull(getClass().getResource(musicFile));
        media = new Media(fileURL.toExternalForm());
    }
}
