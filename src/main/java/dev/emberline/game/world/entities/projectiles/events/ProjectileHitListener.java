package dev.emberline.game.world.entities.projectiles.events;

import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.entities.enemies.IEnemiesManager;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Vector2D;

import java.util.Optional;

public class ProjectileHitListener {

    private final IEnemiesManager enemiesManager;

    public ProjectileHitListener(IEnemiesManager enemiesManager) {
        this.enemiesManager = enemiesManager;
    }

    public void onProjectileHit(ProjectileHitEvent e) {
        Vector2D landingLocation = e.getLandingLocation();
        double damage = e.getDamage();
        double damageArea = e.getDamageArea().isPresent() ? e.getDamageArea().get() : 0.1;
        Optional<EnchantmentEffect> effect = e.getEffect();

        for (final IEnemy enemy : enemiesManager.getNear(landingLocation, damageArea)) {
            effect.ifPresent(enemy::applyEffect);
            enemy.dealDamage(damage);

            if (e.getDamageArea().isEmpty()) {
                break;
            }
        }
    }
}
