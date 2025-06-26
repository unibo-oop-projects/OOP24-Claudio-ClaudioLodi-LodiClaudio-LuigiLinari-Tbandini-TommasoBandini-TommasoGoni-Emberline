package dev.emberline.core.graphics.spritefactories;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys.MapSpriteKey;
import dev.emberline.core.graphics.spritekeys.ProjectileSpriteKey;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Map;
import java.util.Objects;

/**
 * A factory class for creating animated projectile sprites based on a provided {@link ProjectileSpriteKey}.
 * The projectile sprite atlas is a single image containing all sprite frames,
 * and the individual frames are extracted during sprite creation based on metadata values.
 */
public final class ProjectileSpriteFactory implements SpriteFactory<ProjectileSpriteKey> {

    private final static Metadata METADATA = ConfigLoader.loadConfig("/sprites/towerAssets/projectile.json", Metadata.class);

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

    /**
     * Constructs a new {@code ProjectileSpriteFactory} instance.
     * @see ProjectileSpriteFactory
     */
    public ProjectileSpriteFactory() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sprite loadSprite(final ProjectileSpriteKey key) {
        final ProjectileInfo.Type size = key.size();
        final EnchantmentInfo.Type enchant = key.enchant();

        final int xOffset = METADATA.size.get(size);
        final int yOffset = METADATA.enchant.get(enchant);

        final Image projectileAtals = getProjectileAtlas();

        final Image[] frames = new Image[METADATA.frames];
        for (int i = 0; i < METADATA.frames; ++i) {
            final int frameStep = METADATA.width * METADATA.size.size();
            final int x = xOffset + i * frameStep;
            final int y = yOffset;
            frames[i] = new WritableImage(projectileAtals.getPixelReader(), x, y, METADATA.width, METADATA.height);
        }

        return new AnimatedSprite(frames, METADATA.frameTimeNs);
    }

    private static Image getProjectileAtlas() {
        return new Image(Objects.requireNonNull(ProjectileSpriteFactory.class.getResourceAsStream(METADATA.filename)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<ProjectileSpriteKey> getKeyType() {
        return ProjectileSpriteKey.class;
    }

}
