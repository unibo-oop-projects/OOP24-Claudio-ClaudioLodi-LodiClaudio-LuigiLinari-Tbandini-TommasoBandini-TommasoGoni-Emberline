package dev.emberline.core.graphics.spritefactories;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys.MapSpriteKey;
import javafx.scene.image.Image;

import java.util.Objects;

public class MapSpriteFactory implements SpriteFactory<MapSpriteKey> {

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

    private final static Metadata metadata = ConfigLoader.loadConfig("/world/waves/map.json", Metadata.class);

    @Override
    public Sprite loadSprite(final MapSpriteKey key) {
        final String wave = String.valueOf(key.waveNumber());
        final int frameNumber = metadata.waves[key.waveNumber()].frames;
        final Image[] frames = new Image[frameNumber];

        for (int i = 0; i < frameNumber; i++) {
            frames[i] = getMapAtlas(wave, String.valueOf(i));
        }
        return new AnimatedSprite(frames, metadata.frameTimeNs);
    }

    private static Image getMapAtlas(final String wave, final String frame) {
        return new Image(Objects.requireNonNull(
                MapSpriteFactory.class.getResourceAsStream(
                        metadata.wavesFolder + wave + metadata.mapFolder + frame + metadata.mapFile
                )));
    }

    @Override
    public Class<MapSpriteKey> getKeyType() {
        return MapSpriteKey.class;
    }
}
