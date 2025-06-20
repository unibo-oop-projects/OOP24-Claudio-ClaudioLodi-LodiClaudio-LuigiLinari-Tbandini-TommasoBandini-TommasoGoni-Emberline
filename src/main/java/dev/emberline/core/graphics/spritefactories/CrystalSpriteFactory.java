package dev.emberline.core.graphics.spritefactories;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys.CrystalSpriteKey;
import dev.emberline.game.model.EnchantmentInfo;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Map;
import java.util.Objects;

public class CrystalSpriteFactory implements SpriteFactory<CrystalSpriteKey> {
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
        @JsonProperty("enchant")
        private Map<EnchantmentInfo.Type, Integer> enchant;
    }

    private final static Metadata metadata = ConfigLoader.loadConfig("/sprites/towerAssets/crystal.json", Metadata.class);

    public Sprite loadSprite(CrystalSpriteKey key) {
        EnchantmentInfo.Type enchant = key.type();

        int yOffset = metadata.enchant.get(enchant);

        Image crystalAtlas = getCrystalAtlas();
        Image[] frames = new Image[metadata.frames];
        for (int i = 0; i < metadata.frames; i++) {
            int y = yOffset;
            int x = metadata.width * i;
            frames[i] = new WritableImage(crystalAtlas.getPixelReader(), x, y, metadata.width, metadata.height);
        }
        return new AnimatedSprite(frames, metadata.frameTimeNs);
    }

    private static Image getCrystalAtlas() {
        return new Image(Objects.requireNonNull(CrystalSpriteFactory.class.getResourceAsStream(metadata.filename)));
    }

    @Override
    public Class<CrystalSpriteKey> getKeyType() {
        return CrystalSpriteKey.class;
    }
}
