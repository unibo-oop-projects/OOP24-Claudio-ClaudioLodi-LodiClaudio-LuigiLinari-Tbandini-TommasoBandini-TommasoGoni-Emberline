package dev.emberline.game.world.entities.enemies.enemy;

import java.util.List;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.World;
import dev.emberline.utility.Vector2D;

public abstract class AbstractEnemy implements IEnemy {

    public enum FacingDirection { UP, RIGHT, DOWN, LEFT }

    private final EnemyUpdateComponent updateComponent;
    private final EnemyRenderComponent renderComponent;

    public AbstractEnemy(Vector2D spawnPoint, World world) {
        this.updateComponent = new EnemyUpdateComponent(spawnPoint, world, this);
        this.renderComponent = new EnemyRenderComponent(this);
    }

    abstract protected double getWidth();
    abstract protected double getHeight();
    abstract protected double getFullHealth();
    abstract protected double getSpeed();
    abstract protected EnemyType getEnemyType();
    //TODO ABSTRACT METHOD FOR ANIMATION SELECTION

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
    };

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
