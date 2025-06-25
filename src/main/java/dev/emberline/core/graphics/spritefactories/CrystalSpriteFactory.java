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

    private final static Metadata METADATA = ConfigLoader.loadConfig("/sprites/towerAssets/crystal.json", Metadata.class);

    private static class Metadata {
        @JsonProperty
        String filename;
        @JsonProperty
        int width;
        @JsonProperty
        int height;
        @JsonProperty
        int frames;
        @JsonProperty
        int frameTimeNs;
        @JsonProperty
        Map<EnchantmentInfo.Type, Integer> enchant;
    }

    @Override
    public Sprite loadSprite(final CrystalSpriteKey key) {
        final EnchantmentInfo.Type enchant = key.type();

        final int yOffset = METADATA.enchant.get(enchant);

        final Image crystalAtlas = getCrystalAtlas();
        final Image[] frames = new Image[METADATA.frames];
        for (int i = 0; i < METADATA.frames; i++) {
            final int y = yOffset;
            final int x = METADATA.width * i;
            frames[i] = new WritableImage(crystalAtlas.getPixelReader(), x, y, METADATA.width, METADATA.height);
        }
        return new AnimatedSprite(frames, METADATA.frameTimeNs);
    }

    private static Image getCrystalAtlas() {
        return new Image(Objects.requireNonNull(CrystalSpriteFactory.class.getResourceAsStream(METADATA.filename)));
    }

    @Override
    public Class<CrystalSpriteKey> getKeyType() {
        return CrystalSpriteKey.class;
    }
}
