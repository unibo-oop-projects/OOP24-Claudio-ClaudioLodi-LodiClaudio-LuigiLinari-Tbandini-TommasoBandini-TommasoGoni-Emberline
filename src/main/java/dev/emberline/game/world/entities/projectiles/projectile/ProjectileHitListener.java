package dev.emberline.game.world.entities.projectiles.projectile;

import java.util.Optional;

import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.entities.enemies.EnemiesManager;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import javafx.geometry.Point2D;

public class ProjectileHitListener {
    
    private final EnemiesManager enemiesManager;

    public ProjectileHitListener(EnemiesManager enemiesManager) {
        this.enemiesManager = enemiesManager;
    }

    public void onProjectileHit(ProjectileHitEvent e) {
        Point2D landingLocation = e.getLandingLocation();
        double damage = e.getDamage();
        double damageArea = (e.getDamageArea().isPresent() ? e.getDamageArea().get() : 0.1);
        Optional<EnchantmentEffect> effect = e.getEffect();

        for (final IEnemy enemy : enemiesManager.getNear(landingLocation, damageArea)) {
            if (effect.isPresent()) {
                enemy.applyEffect(effect.get());
            }
            enemy.dealDamage(damage);

            if (e.getDamageArea().isEmpty()) {
                break;
            }
        }
    }
}
