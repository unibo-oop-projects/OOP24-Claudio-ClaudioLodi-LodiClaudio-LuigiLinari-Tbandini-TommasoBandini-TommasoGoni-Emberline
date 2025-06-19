package dev.emberline.game.world.entities.projectiles.projectile;

import dev.emberline.core.components.Updatable;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.ProjectileSpriteKey;
import javafx.scene.image.Image;

public class ProjectileAnimation implements Updatable {

    private final AnimatedSprite projectileSprites;

    private final long frameTimeNs;
    private int frameIndex = 0;
    private long accumulatedTimeNs = 0;

    public ProjectileAnimation(Projectile owner) {
        this.projectileSprites = (AnimatedSprite) SpriteLoader.loadSprite(
            new ProjectileSpriteKey(owner.getSizeType(), owner.getEnchantmentType()));
        this.projectileSprites.setFrame(frameIndex);
        this.frameTimeNs = (long) (projectileSprites.getFrameTimeNs() * 1e6);
    }

    @Override
    public void update(long elapsed) {
        accumulatedTimeNs += elapsed;

        while (accumulatedTimeNs >= frameTimeNs) {
            accumulatedTimeNs -= frameTimeNs;
            frameIndex = (frameIndex + 1) % projectileSprites.getFrameCount();
        }

        projectileSprites.setFrame(frameIndex);
    }

    public Image getImage() {
        return projectileSprites.getImage();
    }
}
