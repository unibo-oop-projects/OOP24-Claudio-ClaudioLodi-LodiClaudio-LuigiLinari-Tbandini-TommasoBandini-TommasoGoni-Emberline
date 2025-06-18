package dev.emberline.core.graphics.spritefactories;

import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import dev.emberline.core.ConfigLoader;
import dev.emberline.core.graphics.SingleSprite;
import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys.TowerSpriteKey;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class TowerSpriteFactory implements SpriteFactory<TowerSpriteKey> {
    private static class Metadata {
        @JsonProperty("filename")
        private String filename;
        @JsonProperty("width")
        private int width;
        @JsonProperty("height")
        private int height;
        @JsonProperty("size")
        private Map<ProjectileInfo.Type, Integer> size;
        @JsonProperty("enchant")
        private Map<EnchantmentInfo.Type, Integer> enchant;
    }

    private static final Metadata metadata = ConfigLoader.loadConfig("/sprites/towerAssets/tower.json", Metadata.class);

    @Override
    public Sprite loadSprite(TowerSpriteKey key) {
        ProjectileInfo.Type size = key.size();
        EnchantmentInfo.Type enchant = key.enchant();

        int x = metadata.size.get(size);
        int y = metadata.enchant.get(enchant);

        Image towerAtlas = getTowerAtlas();

        return new SingleSprite(new WritableImage(towerAtlas.getPixelReader(), x, y, metadata.width, metadata.height));
    }

    private static Image getTowerAtlas() {
        return new Image(Objects.requireNonNull(TowerSpriteFactory.class.getResourceAsStream(metadata.filename)));
    }


    @Override
    public Class<TowerSpriteKey> getKeyType() {
        return TowerSpriteKey.class;
    }

}
