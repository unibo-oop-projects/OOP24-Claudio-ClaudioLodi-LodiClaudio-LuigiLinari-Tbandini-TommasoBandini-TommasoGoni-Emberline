package dev.emberline.core.graphics.spritefactories;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys._CrystalSpriteKey;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Objects;

public class _CrystalSpriteFactory implements SpriteFactory<_CrystalSpriteKey> {
    private static class Metadata {
        @JsonProperty("filename")
        private String filename;
        @JsonProperty("width")
        private int width;
        @JsonProperty("height")
        private int height;
        @JsonProperty("frames")
        private int frames;
        @JsonProperty("frameTimeNs")
        private int frameTimeNs;
    }

    private final static Metadata metadata = ConfigLoader.loadConfig("/sprites/towerAssets/crystal.json", Metadata.class);

    public Sprite loadSprite(_CrystalSpriteKey key) {
        Image crystalAtlas = new Image(Objects.requireNonNull(_CrystalSpriteFactory.class.getResourceAsStream(metadata.filename)));
        Image[] frames = new Image[metadata.frames];
        for (int i = 0; i < metadata.frames; i++) {
            int y = 0;
            int x = metadata.width * i;
            frames[i] = new WritableImage(crystalAtlas.getPixelReader(), x, y, metadata.width, metadata.height);
        }
        return new AnimatedSprite(frames, metadata.frameTimeNs);
    }

    @Override
    public Class<_CrystalSpriteKey> getKeyType() {
        return _CrystalSpriteKey.class;
    }
}
