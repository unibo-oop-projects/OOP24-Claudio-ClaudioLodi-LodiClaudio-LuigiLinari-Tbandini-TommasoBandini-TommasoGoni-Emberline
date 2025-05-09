package dev.emberline.game.world.entities.enemies.enemy;

import java.util.List;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.effects.EnchantmentEffect;
import javafx.geometry.Point2D;

public interface IEnemy extends Updatable, Renderable {

    void dealDamage(double damage);
    
    void applyEffect(EnchantmentEffect effect);
    
    void setSlowFactor(double slowFactor);
    
    boolean isDead();

    /** 
     * Uniform motion (s_0 + v * t) with t in [0, {@code duration}] ns
     */
    record UniformMotion(Point2D origin, Point2D velocity, long duration) {};

    /**
     * @param time time of truncation
     * @return All the uniform motions starting from the current position of the enemy.
     * That is described by a list of {@code UniformMotion}
     */
    List<UniformMotion> getMotionUntil(long time);
}
