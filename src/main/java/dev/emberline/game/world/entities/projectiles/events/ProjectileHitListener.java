package dev.emberline.game.world.entities.projectiles.events;

import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.entities.enemies.IEnemiesManager;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Vector2D;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

/**
 * This class listens for projectile hit events and handles what
 * should happen when a specific hit event is thrown.
 */
public class ProjectileHitListener implements Serializable {

    @Serial
    private static final long serialVersionUID = -6166989976490461169L;

    private final IEnemiesManager enemiesManager;

    /**
     * Constructs a new ProjectileHitListener to handle projectile-hit events.
     *
     * @param enemiesManager the {@code EnemiesManager} associated with the listener
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "The enemies pool has to be on track with the latest state of the game."
    )
    public ProjectileHitListener(final IEnemiesManager enemiesManager) {
        this.enemiesManager = enemiesManager;
    }

    /**
     * Handles the logic for when a projectile hits a target.
     * If the {@code ProjectileHitEvent} has a damage area this method deals
     * damage and applies the effect to all the enemies inside that area.
     * Otherwise, it just hits whoever is in the proximity of the landing location
     *
     * @param e the {@code ProjectileHitEvent} containing information about what
     *          should happen upon hitting a location
     */
    public void onProjectileHit(final ProjectileHitEvent e) {
        final Vector2D landingLocation = e.getLandingLocation();
        final double damage = e.getDamage();
        final double defaultDamageArea = 0.1;
        final double damageArea = e.getDamageArea().isPresent() ? e.getDamageArea().get() : defaultDamageArea;
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
