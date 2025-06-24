package dev.emberline.core.graphics.spritefactories;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys.ProjectileSpriteKey;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Map;
import java.util.Objects;

public class ProjectileSpriteFactory implements SpriteFactory<ProjectileSpriteKey> {
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
        Map<ProjectileInfo.Type, Integer> size;
        @JsonProperty
        Map<EnchantmentInfo.Type, Integer> enchant;
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
