package dev.emberline.core.graphics.spritefactories;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys.EnemySpriteKey;
import dev.emberline.core.graphics.spritekeys.MapSpriteKey;
import javafx.scene.image.Image;

import java.util.Objects;

/**
 * A factory class for creating animated map sprites based on a provided {@link MapSpriteKey}.
 * The map sprite atlas is a single image containing all sprite frames,
 * and the individual frames are extracted during sprite creation based on metadata values.
 */
public class MapSpriteFactory implements SpriteFactory<MapSpriteKey> {

    private final static Metadata METADATA = ConfigLoader.loadConfig("/world/waves/map.json", Metadata.class);

    private static class Waves {
        @JsonProperty
        String wave;
        @JsonProperty
        int frames;
    }

    private static class Metadata {
        @JsonProperty
        String wavesFolder;
        @JsonProperty
        String mapFolder;
        @JsonProperty
        String mapFile;
        @JsonProperty
        int frameTimeNs;
        @JsonProperty
        Waves[] waves;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sprite loadSprite(final MapSpriteKey key) {
        final String wave = String.valueOf(key.waveNumber());
        final int frameNumber = METADATA.waves[key.waveNumber()].frames;
        final Image[] frames = new Image[frameNumber];

        for (int i = 0; i < frameNumber; i++) {
            frames[i] = getMapAtlas(wave, String.valueOf(i));
        }
        return new AnimatedSprite(frames, METADATA.frameTimeNs);
    }

    private static Image getMapAtlas(final String wave, final String frame) {
        return new Image(Objects.requireNonNull(
                MapSpriteFactory.class.getResourceAsStream(
                        METADATA.wavesFolder + wave + METADATA.mapFolder + frame + METADATA.mapFile
                )));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<MapSpriteKey> getKeyType() {
        return MapSpriteKey.class;
    }
}
