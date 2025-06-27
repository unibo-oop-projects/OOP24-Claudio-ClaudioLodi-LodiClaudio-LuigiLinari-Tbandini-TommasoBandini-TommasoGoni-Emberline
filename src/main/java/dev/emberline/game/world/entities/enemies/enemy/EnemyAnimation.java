package dev.emberline.game.world.entities.enemies.enemy;

import com.fasterxml.jackson.annotation.JsonCreator;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.EnemySpriteKey;
import javafx.scene.image.Image;

import java.util.Locale;

/**
 * The EnemyAnimation class is responsible for managing the visual representation
 * of an enemy in the game. This includes handling animations and updating the
 * sprite based on the enemy's state, appearance, and facing direction.
 */
public class EnemyAnimation implements Updatable {

    private final AbstractEnemy enemy;
    private AnimatedSprite animatedSprite;
    private EnemyAppearance enemyAppearance = EnemyAppearance.NORMAL;
    private AbstractEnemy.FacingDirection facingDirection = AbstractEnemy.FacingDirection.RIGHT;

    private int frameIndex;
    private long accumulatedTimeNs;

    private boolean dyingAnimationFinished; // To track if the dying animation has finished
    private boolean isDying; // To track if the enemy is currently in a dying state

    /**
     * This enumeration defines different visual states that an enemy can have,
     * which include:
     * <ul>
     * <li>NORMAL: The enemy is in its default appearance.</li>
     * <li>BURNING: The enemy is on fire.</li>
     * <li>FREEZING: The enemy is frozen.</li>
     * <li>DYING: The enemy is in the process of dying.</li>
     * </ul>
     */
    public enum EnemyAppearance {
        /**
         * The enemy default appearance.
         */
        NORMAL,

        /**
         * The enemy is on fire.
         */
        BURNING,

        /**
         * The enemy is frozen.
         */
        FREEZING,

        /**
         * The enemy is in the process of dying.
         */
        DYING;

        /**
         * Converts a string representation of an enemy's appearance to the corresponding
         * {@code EnemyAppearance} enum value.
         *
         * @param appearance the string representation of the enemy's appearance; must match
         *                   one of the enum values (case-insensitive)
         * @return the {@code EnemyAppearance} enum value corresponding to the provided string
         *         representation
         * @throws IllegalArgumentException if the provided string does not match any of the
         *                                  defined enum values
         */
        @JsonCreator
        public static EnemyAppearance fromString(final String appearance) {
            return EnemyAppearance.valueOf(appearance.toUpperCase(Locale.US));
        }
    }

    /**
     * Constructs an instance of the {@link EnemyAnimation} class.
     *
     * @param enemy The AbstractEnemy instance for which the animation is to be managed.
     */
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
    private boolean updateEnemyAppearance(final EnemyAppearance enemyAppearance) {
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
    private boolean updateFacingDirection(final AbstractEnemy.FacingDirection facingDirection) {
        if (this.facingDirection == facingDirection) {
            return false; // No change needed
        }
        this.facingDirection = facingDirection;
        return true;
    }

    private void updateAnimatedSprite() {
        boolean changed = updateFacingDirection(enemy.getFacingDirection());
        changed |= updateEnemyAppearance(enemy.getEnemyAppearance());
        if (!changed) {
            return; // No changes to the sprite, no need to update
        }
        this.animatedSprite = (AnimatedSprite) SpriteLoader.loadSprite(
                new EnemySpriteKey(enemy.getEnemyType(), facingDirection, enemyAppearance)
        );
    }

    /**
     * Returns the current frame of the enemy's animation as an image.
     *
     * @return the current frame of the enemy's animation as an image
     */
    public Image getImage() {
        return animatedSprite.image(frameIndex);
    }

    /**
     * Returns whether the enemy's dying animation has completed.
     *
     * @return whether the enemy's dying animation has completed.
     */
    public boolean isDyingAnimationFinished() {
        return dyingAnimationFinished;
    }

    /**
     * Updates the animation state of the enemy based on the elapsed time and current enemy state.
     *
     * @param elapsed The time in nanoseconds that has elapsed since the last update.
     *                This is used to determine the progression of the animation.
     */
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
