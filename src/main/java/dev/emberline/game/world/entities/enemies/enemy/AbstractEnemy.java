package dev.emberline.game.world.entities.enemies.enemy;

import java.util.List;

import dev.emberline.core.animations.Animation;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.World;
import dev.emberline.utility.Vector2D;

public abstract class AbstractEnemy implements IEnemy {

    enum FacingDirection { UP, RIGHT, DOWN, LEFT }
    enum EnemyState      { WALKING, ATTACKING, DYING, DEAD }

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

    FacingDirection getFacingDirection() {
        return updateComponent.getFacingDirection();
    }

    EnemyState getEnemyState() {
        return updateComponent.getEnemyState();
    }

    // TODO FIX
    Animation getAnimation() {
        return renderComponent.getAnimation();
    }
}
