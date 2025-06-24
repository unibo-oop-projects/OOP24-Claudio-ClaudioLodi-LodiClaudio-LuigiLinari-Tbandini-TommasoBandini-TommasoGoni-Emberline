package dev.emberline.game.world.entities.enemies.enemy;

import com.fasterxml.jackson.annotation.JsonCreator;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.EnemySpriteKey;
import javafx.scene.image.Image;

public class EnemyAnimation implements Updatable {

    private final AbstractEnemy enemy;
    private AnimatedSprite animatedSprite;
    private EnemyAppearance enemyAppearance = EnemyAppearance.NORMAL;
    private AbstractEnemy.FacingDirection facingDirection = AbstractEnemy.FacingDirection.RIGHT;

    private int frameIndex = 0;
    private long accumulatedTimeNs = 0;

    private boolean dyingAnimationFinished = false; // To track if the dying animation has finished
    private boolean isDying = false; // To track if the enemy is currently in a dying state

    public enum EnemyAppearance {
        NORMAL, BURNING, FREEZING, DYING;

        @JsonCreator
        public static EnemyAppearance fromString(final String appearance) {
            return EnemyAppearance.valueOf(appearance.toUpperCase());
        }
    }

    public EnemyAnimation(final AbstractEnemy enemy) {
        this.enemy = enemy;
        updateAnimatedSprite();
    }

    /**
     * Updates the enemy's current appearance.
     *
     * @param enemyAppearance The new enemy appearance.
     * @return {@code true} if the appearance was changed, {@code false} otherwise.
     */
    private boolean setEnemyAppearance(final EnemyAppearance enemyAppearance) {
        if (this.enemyAppearance == enemyAppearance) {
            return false; // No change needed
        }
        this.enemyAppearance = enemyAppearance;
        return true;
    }

    /**
     * Updates the facing direction of the enemy.
     *
     * @param facingDirection The new facing direction.
     * @return {@code true} if the direction was changed, {@code false} otherwise.
     */
    private boolean setFacingDirection(final AbstractEnemy.FacingDirection facingDirection) {
        if (this.facingDirection == facingDirection) {
            return false; // No change needed
        }
        this.facingDirection = facingDirection;
        return true;
    }

    private void updateAnimatedSprite() {
        boolean changed = setFacingDirection(enemy.getFacingDirection());
        changed |= setEnemyAppearance(enemy.getEnemyAppearance());
        if (!changed) {
            return; // No changes to the sprite, no need to update
        }
        this.animatedSprite = (AnimatedSprite) SpriteLoader.loadSprite(new EnemySpriteKey(enemy.getEnemyType(), facingDirection, enemyAppearance));
    }

    public Image getImage() {
        return animatedSprite.image(frameIndex);
    }

    public boolean isDyingAnimationFinished() {
        return dyingAnimationFinished;
    }

    @Override
    public void update(final long elapsed) {
        if (dyingAnimationFinished) {
            return;
        }
        updateAnimatedSprite();

        // Initialize death animation when the enemy starts dying.
        if (enemyAppearance == EnemyAppearance.DYING && !isDying) {
            frameIndex = 0;
            accumulatedTimeNs = 0;
            isDying = true;
        }

        final long frameTimeNs = (long) (animatedSprite.getFrameTimeNs() / enemy.getSlowFactor());
        accumulatedTimeNs += elapsed;
        while (accumulatedTimeNs >= frameTimeNs) {
            accumulatedTimeNs -= frameTimeNs;
            frameIndex = (frameIndex + 1) % animatedSprite.getFrameCount();

            // If the enemy is dying, we need to check if the animation has finished, do not loopback.
            if (isDying && frameIndex == animatedSprite.getFrameCount() - 1) {
                dyingAnimationFinished = true;
                break;
            }
        }
    }
}
