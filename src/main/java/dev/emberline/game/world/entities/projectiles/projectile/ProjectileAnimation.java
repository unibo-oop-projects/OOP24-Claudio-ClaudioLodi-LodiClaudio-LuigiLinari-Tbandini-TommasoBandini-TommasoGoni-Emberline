package dev.emberline.game.world.entities.projectiles.projectile;

import dev.emberline.core.components.Updatable;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.ProjectileSpriteKey;
import javafx.scene.image.Image;

/**
 * The {@code ProjectileAnimation} class manages the visual representation of a projectile
 * by updating the current frame of an {@code AnimatedSprite} over time.
 * The type of projectile to be animated is determined by the
 * {@link dev.emberline.game.model.ProjectileInfo.Type} and {@link dev.emberline.game.model.EnchantmentInfo.Type}
 */
public class ProjectileAnimation implements Updatable {

    private final AnimatedSprite projectileSprite;

    private int frameIndex = 0;
    private long accumulatedTimeNs = 0;

    /**
     * Initializes a new instance of the {@code ProjectileAnimation} class for the specified projectile.
     *
     * @param owner the {@code Projectile} instance whose size and enchantment types are used
     *              to determine the animation sprite.
     */
    public ProjectileAnimation(final Projectile owner) {
        this.projectileSprite = (AnimatedSprite) SpriteLoader.loadSprite(
                new ProjectileSpriteKey(owner.getSizeType(), owner.getEnchantmentType()));
    }

    /**
     * Updates the animation of the projectile by advancing the frame index
     * based on the elapsed time and the frame duration of the associated sprite.
     *
     * @param elapsed the time (in nanoseconds) that has passed since the last update
     */
    @Override
    public void update(final long elapsed) {
        accumulatedTimeNs += elapsed;

        while (accumulatedTimeNs >= projectileSprite.getFrameTimeNs()) {
            accumulatedTimeNs -= projectileSprite.getFrameTimeNs();
            frameIndex = (frameIndex + 1) % projectileSprite.getFrameCount();
        }
    }

    /**
     * @return the current image representing the active animation frame of the projectile
     */
    public Image getImage() {
        return projectileSprite.image(frameIndex);
    }
}
