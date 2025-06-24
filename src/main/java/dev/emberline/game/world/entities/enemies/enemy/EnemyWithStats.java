package dev.emberline.game.world.entities.enemies.enemy;

import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.statistics.Statistics;
import dev.emberline.utility.Vector2D;

import java.util.List;

public class EnemyWithStats implements IEnemy {

    private final IEnemy enemy;
    private final Statistics statistics;

    public EnemyWithStats(final IEnemy newEnemy, final Statistics statistics) {
        this.enemy = newEnemy;
        this.statistics = statistics;
    }

    @Override
    public double getHeight() {
        return enemy.getHeight();
    }

    @Override
    public double getWidth() {
        return enemy.getWidth();
    }

    @Override
    public double getHealth() {
        return enemy.getHealth();
    }

    @Override
    public void dealDamage(final double damage) {
        final double damageDealt = Math.min(getHealth(), damage);
        statistics.updateTotalDamage(damageDealt);
        enemy.dealDamage(damage);
    }

    @Override
    public void applyEffect(final EnchantmentEffect effect) {
        enemy.applyEffect(effect);
    }

    @Override
    public void setSlowFactor(final double slowFactor) {
        enemy.setSlowFactor(slowFactor);
    }

    @Override
    public boolean isDead() {
        return enemy.isDead();
    }

    @Override
    public boolean isHittable() {
        return enemy.isHittable();
    }

    @Override
    public List<UniformMotion> getMotionUntil(final long time) {
        return enemy.getMotionUntil(time);
    }

    @Override
    public Vector2D getPosition() {
        return enemy.getPosition();
    }

    @Override
    public void render() {
        enemy.render();
    }

    @Override
    public void update(final long elapsed) {
        enemy.update(elapsed);
    }
}
