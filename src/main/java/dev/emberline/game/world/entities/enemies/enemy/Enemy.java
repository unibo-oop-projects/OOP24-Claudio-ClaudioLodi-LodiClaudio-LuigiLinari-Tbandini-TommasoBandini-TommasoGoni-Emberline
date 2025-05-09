package dev.emberline.game.world.entities.enemies.enemy;

import java.util.List;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.World;
import utility.Coordinate2D;

public class Enemy implements IEnemy {

    private final EnemyUpdateComponent updateComponent;
    private final EnemyRenderComponent renderComponent;

    public Enemy(Coordinate2D spawnPoint, World world) {
        this.updateComponent = new EnemyUpdateComponent(spawnPoint, world, this);
        this.renderComponent = new EnemyRenderComponent(this);
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
    public void applyEffect(EnchantmentEffect effect) {
        updateComponent.applyEffect(effect);
    }

    @Override
    public void dealDamage(double damage) {
        updateComponent.dealDamage(damage);
    }

    @Override
    public boolean isDead() {
        return updateComponent.isDead();
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

    Coordinate2D getPosition() {
        return updateComponent.getPosition();
    }

    double getHealthPercentage() {
        return updateComponent.getHealthPercentage();
    }
}
