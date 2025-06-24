package dev.emberline.core.graphics.spritefactories;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.graphics.SingleSprite;
import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys.TowerSpriteKey;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Map;
import java.util.Objects;

public class TowerSpriteFactory implements SpriteFactory<TowerSpriteKey> {
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

    private static final Metadata metadata = ConfigLoader.loadConfig("/sprites/towerAssets/tower.json", Metadata.class);

    @Override
    public Sprite loadSprite(TowerSpriteKey key) {
        ProjectileInfo.Type size = key.size();
        EnchantmentInfo.Type enchant = key.enchant();

        int width = metadata.width;
        int height = metadata.height.get(size);
        int x = metadata.size.get(size);
        int y = metadata.enchant.get(enchant) - height;

        Image towerAtlas = getTowerAtlas();

        return new SingleSprite(new WritableImage(towerAtlas.getPixelReader(), x, y, width, height));
    }

    private static Image getTowerAtlas() {
        return new Image(Objects.requireNonNull(TowerSpriteFactory.class.getResourceAsStream(metadata.filename)));
    }


    @Override
    public Class<TowerSpriteKey> getKeyType() {
        return TowerSpriteKey.class;
    }

}
