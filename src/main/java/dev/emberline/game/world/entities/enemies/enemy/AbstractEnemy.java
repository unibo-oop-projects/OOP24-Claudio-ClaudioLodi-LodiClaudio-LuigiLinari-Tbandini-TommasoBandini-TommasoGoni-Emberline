package dev.emberline.game.world.entities.enemies.enemy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.World;
import dev.emberline.utility.Vector2D;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * Represents an abstract implementation of an enemy in the game, defining
 * common properties and behaviors that all specific enemy types must implement.
 */
public abstract class AbstractEnemy implements IEnemy {

    @Serial
    private static final long serialVersionUID = 2633064928656056204L;

    private final EnemyUpdateComponent updateComponent;
    private final EnemyRenderComponent renderComponent;

    /**
     * Represents metadata for an enemy in the game. This class encapsulates
     * configuration and physical properties that define an enemy's behavior
     * and appearance, such as dimensions, health, and movement speed.
     *
     * @param tileWidth the width of the enemy in world space
     * @param tileHeight the height of the enemy in world space
     * @param fullHealth the full health value of the enemy
     * @param speed the speed of the enemy in tile/ns
     */
    protected record Metadata(
            @JsonProperty double tileWidth,
            @JsonProperty double tileHeight,
            @JsonProperty double fullHealth,
            @JsonProperty double speed
    ) {
    }

    /**
     * This enum is used to indicate the direction an entity is currently facing.
     * <p>
     * The available directions are: {@code UP}, {@code RIGHT}, {@code DOWN} and {@code LEFT}.
     * </p>
     * It also provides a utility for converting string representations of directions
     * into their corresponding enum values.
     */
    public enum FacingDirection implements Serializable {
        /**
         * Represents that the enemy is facing upwards
         */
        UP,
        /**
         * Represents that the enemy is facing rightwards
         */
        RIGHT,
        /**
         * Represents that the enemy is facing downwards
         */
        DOWN,
        /**
         * Represents that the enemy is facing leftwards
         */
        LEFT;

        /**
         * Converts a given string representation of a direction into its corresponding
         * {@code FacingDirection} enum value.
         *
         * @param direction the string representation of the direction; must match one of
         *                  the enum constants (case insensitive).
         * @return the {@code FacingDirection} enum value corresponding to the given string.
         * @throws IllegalArgumentException if the provided string does not match any of the
         *                                  defined {@code FacingDirection} constants.
         */
        @JsonCreator
        public static FacingDirection fromString(final String direction) {
            return FacingDirection.valueOf(direction.toUpperCase(Locale.US));
        }
    }

    /**
     * Initializes the update and render components of the enemy.
     *
     * @param spawnPoint the initial position of the enemy
     * @param world the game world associated with this enemy
     */
    public AbstractEnemy(final Vector2D spawnPoint, final World world) {
        this.updateComponent = new EnemyUpdateComponent(spawnPoint, world, this);
        this.renderComponent = new EnemyRenderComponent(this);
    }

    /**
     * Returns the {@link Metadata} associated with the enemy
     * @return the {@link Metadata} associated with the enemy
     */
    protected abstract Metadata getMetadata();

    /**
     * Returns the {@link Metadata} associated with the enemy
     * @return the {@link EnemyType} associated with the enemy
     */
    protected abstract EnemyType getEnemyType();

    /**
     * {@inheritDoc}
     */
    @Override
    public double getWidth() {
        return getMetadata().tileWidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getHealth() {
        return updateComponent.getHealth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getHeight() {
        return getMetadata().tileHeight;
    }

    /**
     * Returns the full health value of the enemy as described by its metadata
     * @return the full health value of the enemy as described by its metadata
     */
    protected double getFullHealth() {
        return getMetadata().fullHealth;
    }

    /**
     * Returns the speed value of the enemy as described by its metadata.
     * @return the speed value of the enemy as described by its metadata.
     */
    protected double getSpeed() {
        return getMetadata().speed;
    }

    /**
     * Updates the enemy.
     * @see EnemyUpdateComponent#update(long)
     */
    @Override
    public void update(final long elapsed) {
        updateComponent.update(elapsed);
    }

    /**
     * Renders the enemy.
     * @see EnemyRenderComponent#render()
     */
    @Override
    public void render() {
        renderComponent.render();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dealDamage(final double damage) {
        updateComponent.dealDamage(damage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyEffect(final EnchantmentEffect effect) {
        updateComponent.applyEffect(effect);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSlowFactor(final double slowFactor) {
        updateComponent.setSlowFactor(slowFactor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDead() {
        return updateComponent.isDead();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHittable() {
        return updateComponent.isHittable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniformMotion> getMotionUntil(final long time) {
        return updateComponent.getMotionUntil(time);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector2D getPosition() {
        return updateComponent.getPosition();
    }

    double getHealthPercentage() {
        return updateComponent.getHealthPercentage();
    }

    double getSlowFactor() {
        return updateComponent.getSlowFactor();
    }

    FacingDirection getFacingDirection() {
        return updateComponent.getFacingDirection();
    }

    EnemyAnimation.EnemyAppearance getEnemyAppearance() {
        return updateComponent.getEnemyAppearance();
    }

    boolean isDyingAnimationFinished() {
        return renderComponent.isDyingAnimationFinished();
    }

    Updatable getAnimationUpdatable() {
        return renderComponent.getAnimationUpdatable();
    }
}
