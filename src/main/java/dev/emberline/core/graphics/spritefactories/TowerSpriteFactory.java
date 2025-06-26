package dev.emberline.core.graphics.spritefactories;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.graphics.SingleSprite;
import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys.ProjectileSpriteKey;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import dev.emberline.core.graphics.spritekeys.TowerSpriteKey;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Map;
import java.util.Objects;

/**
 * A factory class for creating tower sprites based on a provided {@link TowerSpriteKey}.
 * The tower sprite atlas is a single image containing all possible towers,
 * and the specific ones are extracted during sprite creation based on metadata values.
 */
public final class TowerSpriteFactory implements SpriteFactory<TowerSpriteKey> {

    private static final Metadata METADATA = ConfigLoader.loadConfig("/sprites/towerAssets/tower.json", Metadata.class);

    private static class Metadata {
        @JsonProperty
        String filename;
        @JsonProperty
        int width;
        @JsonProperty
        Map<ProjectileInfo.Type, Integer> height;
        @JsonProperty
        Map<ProjectileInfo.Type, Integer> size;
        @JsonProperty
        Map<EnchantmentInfo.Type, Integer> enchant;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sprite loadSprite(final TowerSpriteKey key) {
        final ProjectileInfo.Type size = key.size();
        final EnchantmentInfo.Type enchant = key.enchant();

        final int width = METADATA.width;
        final int height = METADATA.height.get(size);
        final int x = METADATA.size.get(size);
        final int y = METADATA.enchant.get(enchant) - height;

        final Image towerAtlas = getTowerAtlas();

        return new SingleSprite(new WritableImage(towerAtlas.getPixelReader(), x, y, width, height));
    }

    private static Image getTowerAtlas() {
        return new Image(Objects.requireNonNull(TowerSpriteFactory.class.getResourceAsStream(METADATA.filename)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<TowerSpriteKey> getKeyType() {
        return TowerSpriteKey.class;
    }

}
