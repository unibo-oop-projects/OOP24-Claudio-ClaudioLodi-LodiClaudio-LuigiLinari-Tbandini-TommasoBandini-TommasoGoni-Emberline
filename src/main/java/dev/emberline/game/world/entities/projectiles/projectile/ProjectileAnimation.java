package dev.emberline.game.world.entities.projectiles.projectile;

import dev.emberline.core.components.Updatable;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.ProjectileSpriteKey;
import javafx.scene.image.Image;

public class ProjectileAnimation implements Updatable {

    private final AnimatedSprite projectileSprite;

    private int frameIndex = 0;
    private long accumulatedTimeNs = 0;

    public ProjectileAnimation(final Projectile owner) {
        this.projectileSprite = (AnimatedSprite) SpriteLoader.loadSprite(
                new ProjectileSpriteKey(owner.getSizeType(), owner.getEnchantmentType()));
    }

    @Override
    public void update(final long elapsed) {
        accumulatedTimeNs += elapsed;

        while (accumulatedTimeNs >= projectileSprite.getFrameTimeNs()) {
            accumulatedTimeNs -= projectileSprite.getFrameTimeNs();
            frameIndex = (frameIndex + 1) % projectileSprite.getFrameCount();
        }
    }

    public Image getImage() {
        return projectileSprite.image(frameIndex);
    }
}
