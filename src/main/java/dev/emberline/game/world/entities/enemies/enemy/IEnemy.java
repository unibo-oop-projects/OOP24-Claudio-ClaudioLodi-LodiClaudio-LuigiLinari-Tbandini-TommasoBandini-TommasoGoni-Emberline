package dev.emberline.game.world.entities.enemies.enemy;

import java.util.List;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.utility.Vector2D;

public interface IEnemy extends Updatable, Renderable {

    double getHeight();

    double getWidth();

    void dealDamage(double damage);
    
    void applyEffect(EnchantmentEffect effect);
    
    void setSlowFactor(double slowFactor);
    
    boolean isDead();

    boolean isHittable();

    /** 
     * Uniform motion (s_0 + v * t) with t in [0, {@code duration}] ns
     */
    record UniformMotion(Vector2D origin, Vector2D velocity, long duration) {};

    /**
     * @param time time of truncation
     * @return All the uniform motions starting from the current position of the enemy.
     * That is described by a list of {@code UniformMotion}
     */
    List<UniformMotion> getMotionUntil(long time);

    Vector2D getPosition();
}
