package dev.emberline.game.world.entities.enemies.enemy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.World;
import dev.emberline.utility.Vector2D;

import java.util.List;
import java.util.Locale;

/**
 * Represents an abstract implementation of an enemy in the game, defining
 * common properties and behaviors that all specific enemy types must implement.
 */
public abstract class AbstractEnemy implements IEnemy {

    private final EnemyUpdateComponent updateComponent;
    private final EnemyRenderComponent renderComponent;

    /**
     * Represents metadata for an enemy in the game. This class encapsulates
     * configuration and physical properties that define an enemy's behavior
     * and appearance, such as dimensions, health, and movement speed.
     */
    protected static class Metadata {
        @JsonProperty
        public double tileWidth;
        @JsonProperty
        public double tileHeight;
        @JsonProperty
        public double fullHealth;
        @JsonProperty
        public double speed;
    }

    /**
     * This enum is used to indicate the direction an entity is currently facing.
     * <p>
     * The available directions are: {@code UP}, {@code RIGHT}, {@code DOWN} and {@code LEFT}.
     * </p>
     * It also provides a utility for converting string representations of directions
     * into their corresponding enum values.
     */
    public enum FacingDirection {
        UP, RIGHT, DOWN, LEFT;

        @JsonCreator
        public static FacingDirection fromString(final String direction) {
            return FacingDirection.valueOf(direction.toUpperCase(Locale.US));
        }
    }

    public AbstractEnemy(final Vector2D spawnPoint, final World world) {
        this.updateComponent = new EnemyUpdateComponent(spawnPoint, world, this);
        this.renderComponent = new EnemyRenderComponent(this);
    }

    /**
     * @return the {@link Metadata} associated with the enemy
     */
    abstract protected Metadata getMetadata();

    /**
     * @return the {@link EnemyType} associated with the enemy
     */
    abstract protected EnemyType getEnemyType();

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
     * @return the full health value of the enemy as described by its metadata
     */
    protected double getFullHealth() {
        return getMetadata().fullHealth;
    }

    /**
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
