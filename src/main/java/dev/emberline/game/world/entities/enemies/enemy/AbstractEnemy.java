package dev.emberline.game.world.entities.enemies.enemy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.World;
import dev.emberline.utility.Vector2D;

import java.util.List;

public abstract class AbstractEnemy implements IEnemy {

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

    public enum FacingDirection {
        UP, RIGHT, DOWN, LEFT;

        @JsonCreator
        public static FacingDirection fromString(String direction) {
            return FacingDirection.valueOf(direction.toUpperCase());
        }
    }

    private final EnemyUpdateComponent updateComponent;
    private final EnemyRenderComponent renderComponent;

    public AbstractEnemy(Vector2D spawnPoint, World world) {
        this.updateComponent = new EnemyUpdateComponent(spawnPoint, world, this);
        this.renderComponent = new EnemyRenderComponent(this);
    }

    abstract protected Metadata getMetadata();

    abstract protected EnemyType getEnemyType();

    public double getWidth() {
        return getMetadata().tileWidth;
    }

    @Override
    public double getHealth() {
        return updateComponent.getHealth();
    }

    public double getHeight() {
        return getMetadata().tileHeight;
    }

    protected double getFullHealth() {
        return getMetadata().fullHealth;
    }

    protected double getSpeed() {
        return getMetadata().speed;
    }

    @Override
    public void update(long elapsed) {
        updateComponent.update(elapsed);
    }

    @Override
    public void render() {
        renderComponent.render();
    }

    @Override
    public void dealDamage(double damage) {
        updateComponent.dealDamage(damage);
    }

    @Override
    public void applyEffect(EnchantmentEffect effect) {
        updateComponent.applyEffect(effect);
    }

    @Override
    public void setSlowFactor(double slowFactor) {
        updateComponent.setSlowFactor(slowFactor);
    }

    ;

    @Override
    public boolean isDead() {
        return updateComponent.isDead();
    }

    @Override
    public boolean isHittable() {
        return updateComponent.isHittable();
    }

    /**
     * @param time time of truncation
     * @return All the uniform motions starting from the current position of the enemy
     * That is described by a list of {@code UniformMotion}
     */
    @Override
    public List<UniformMotion> getMotionUntil(long time) {
        return updateComponent.getMotionUntil(time);
    }

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
