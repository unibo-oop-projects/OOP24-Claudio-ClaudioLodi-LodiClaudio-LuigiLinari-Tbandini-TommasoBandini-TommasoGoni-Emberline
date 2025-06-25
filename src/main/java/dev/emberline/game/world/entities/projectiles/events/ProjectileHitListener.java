package dev.emberline.game.world.entities.projectiles.events;

import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.entities.enemies.IEnemiesManager;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Vector2D;

import java.util.Optional;

public class ProjectileHitListener {

    private final IEnemiesManager enemiesManager;

    public ProjectileHitListener(final IEnemiesManager enemiesManager) {
        this.enemiesManager = enemiesManager;
    }

    public void onProjectileHit(final ProjectileHitEvent e) {
        final Vector2D landingLocation = e.getLandingLocation();
        final double damage = e.getDamage();
        final double damageArea = e.getDamageArea().isPresent() ? e.getDamageArea().get() : 0.1;
        final Optional<EnchantmentEffect> effect = e.getEffect();

        for (final IEnemy enemy : enemiesManager.getNear(landingLocation, damageArea)) {
            effect.ifPresent(enemy::applyEffect);
            enemy.dealDamage(damage);

            if (e.getDamageArea().isEmpty()) {
                break;
            }
        }
    }
}
