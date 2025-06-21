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
        @JsonProperty("wave")
        private String wave;
        @JsonProperty("frame")
        private int frames;
    }
    private static class Metadata {
        @JsonProperty("wavesFolder")
        private String wavesFolder;
        @JsonProperty("mapFolder")
        private String mapFolder;
        @JsonProperty("mapFile")
        private String mapFile;
        @JsonProperty("frameTimeNs")
        private int frameTimeNs;
        @JsonProperty("waves")
        private Waves[] waves;
    }

    private final static Metadata metadata = ConfigLoader.loadConfig("/world/waves/map.json", Metadata.class);

    @Override
    public Sprite loadSprite(MapSpriteKey key) {
        String wave = String.valueOf(key.waveNumber());
        int frameNumber = metadata.waves[key.waveNumber()].frames;
        Image[] frames = new Image[frameNumber];

        for (int i = 0; i < frameNumber; i++) {
            frames[i] = getMapAtlas(wave, String.valueOf(i));
        }
        return new AnimatedSprite(frames, metadata.frameTimeNs);
    }

    private static Image getMapAtlas(String wave, String frame) {
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
