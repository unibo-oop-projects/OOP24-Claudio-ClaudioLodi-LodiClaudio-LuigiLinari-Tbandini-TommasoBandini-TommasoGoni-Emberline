package dev.emberline.game.world.entities.enemies.enemy;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.utility.Vector2D;

import java.util.List;

/**
 * Represents an enemy entity. This interface defines
 * the core behavior and properties that any enemy implementation must
 * fulfill, covering health management, motion, rendering, and status updates.
 * <p>
 * Enemies implementing this interface can be updated and rendered within the game loop.
 *
 * @see Updatable
 * @see Renderable
 */
public interface IEnemy extends Updatable, Renderable {

    /**
     * Returns the height of the enemy in the world space
     * @return the height of the enemy in the world space
     */
    double getHeight();

    /**
     * Returns the width of the enemy in the world space
     * @return the width of the enemy in the world space
     */
    double getWidth();

    /**
     * Retrieves the current health of the enemy.
     * @return the current health value of the enemy
     */
    double getHealth();

    /**
     * Reduces the enemy's health by the given damage value.
     * @param damage the amount of damage to be dealt to the enemy
     */
    void dealDamage(double damage);

    /**
     * Applies the provided {@link EnchantmentEffect}
     * @param effect the effect to be assigned on the enemy
     */
    void applyEffect(EnchantmentEffect effect);

    /**
     * Sets the slow factor of the enemy.
     * @param slowFactor the value that will multiply the speed of the enemy
     */
    void setSlowFactor(double slowFactor);

    /**
     * Returns whether the enemy is dead
     *
     * @return whether the enemy is dead
     */
    boolean isDead();

    /**
     * Returns whether the enemy is dead
     *
     * @return whether the enemy is in a hittable state
     */
    boolean isHittable();

    /**
     * Uniform motion ({@code origin}+ {@code velocity} * {@code t}) with {@code t} in [{@code 0}, {@code durationNs}] ns
     * @param origin the position at {@code t=0}
     * @param velocity the velocity vector of the {@code UniformMotion}
     * @param durationNs how much does the {@code UniformMotion} last
     */
    record UniformMotion(Vector2D origin, Vector2D velocity, long durationNs) {
    }

    /**
     * Returns all the uniform motions describing the enemy's movement starting from the current position of the enemy.
     * @param timeNs time of truncation.
     * @return All the uniform motions starting from the current position of the enemy.
     * That is described by a list of {@link UniformMotion}
     */
    List<UniformMotion> getMotionUntil(long timeNs);

    /**
     * Retrieves the current center position of the enemy in the world space.
     * @return a {@code Vector2D} representing the current center position of the enemy.
     */
    Vector2D getPosition();

    /**
     * Retrieves the remaining distance to the target destination.
     *
     * @return the remaining distance to the target in world units.
     */
    double getRemainingDistanceToTarget();
}
