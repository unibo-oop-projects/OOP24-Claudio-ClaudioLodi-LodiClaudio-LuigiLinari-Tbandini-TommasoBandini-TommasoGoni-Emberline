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

/**
 * A factory for creating {@link Sprite} objects based on {@link CrystalSpriteKey}.
 * This class is used to load and generate animated sprites for crystal assets
 * of different enchantment types.
 * <p>
 * The crystal sprite atlas is a single image containing all sprite frames,
 * and the individual frames are extracted during sprite creation based on metadata values.
 */
public final class CrystalSpriteFactory implements SpriteFactory<CrystalSpriteKey> {

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

    /**
     * Constructs a new {@code CrystalSpriteFactory} instance.
     * @see CrystalSpriteFactory
     */
    public CrystalSpriteFactory() {

    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<CrystalSpriteKey> getKeyType() {
        return CrystalSpriteKey.class;
    }
}
