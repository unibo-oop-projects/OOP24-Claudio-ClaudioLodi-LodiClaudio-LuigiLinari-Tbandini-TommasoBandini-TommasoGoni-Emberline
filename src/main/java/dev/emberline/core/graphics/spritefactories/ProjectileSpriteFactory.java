package dev.emberline.core.graphics.spritefactories;

import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.emberline.core.ConfigLoader;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys.ProjectileSpriteKey;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class ProjectileSpriteFactory implements SpriteFactory<ProjectileSpriteKey> {
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
        @JsonProperty("size")
        private Map<ProjectileInfo.Type, Integer> size;
        @JsonProperty("enchant")
        private Map<EnchantmentInfo.Type, Integer> enchant;
    }

    private final static Metadata metadata = ConfigLoader.loadConfig("/sprites/towerAssets/projectile.json", Metadata.class);

    @Override
    public Sprite loadSprite(ProjectileSpriteKey key) {
        ProjectileInfo.Type size = key.size();
        EnchantmentInfo.Type enchant = key.enchant();

        int xOffset = metadata.size.get(size);
        int yOffset = metadata.enchant.get(enchant);

        Image projectileAtals = getProjectileAtlas();

        Image[] frames = new Image[metadata.frames];
        for (int i = 0; i < metadata.frames; ++i) {
            int frameStep = metadata.width * metadata.size.size();
            int x = xOffset + i * frameStep;
            int y = yOffset;
            frames[i] = new WritableImage(projectileAtals.getPixelReader(), x, y, metadata.width, metadata.height);
        }

        return new AnimatedSprite(frames, metadata.frameTimeNs);
    }

    private static Image getProjectileAtlas() {
        return new Image(Objects.requireNonNull(ProjectileSpriteFactory.class.getResourceAsStream(metadata.filename)));
    }

    @Override
    public Class<ProjectileSpriteKey> getKeyType() {
        return ProjectileSpriteKey.class;
    }

}
