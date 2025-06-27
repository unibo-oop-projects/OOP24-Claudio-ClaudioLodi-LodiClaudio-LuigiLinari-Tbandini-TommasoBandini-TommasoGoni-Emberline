package dev.emberline.game.model.effects;

import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.world.entities.enemies.enemy.EnemyAnimation;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.gui.towerdialog.stats.TowerStatsProvider;

import java.io.Serializable;

/**
 * EnchantmentEffect defines the behavior of effects applied to enemies during gameplay.
 * An EnchantmentEffect is applied to an enemy, and it can modify the enemy's attributes and behavior.
 * <p>
 * EnchantmentEffects are determined by the {@link EnchantmentInfo.Type} of a tower's enchantment.
 * <p>
 * Implementing classes should provide specific effect logic, including how the effect is updated,
 * when it expires, and how it modifies enemy stats or visuals.
 *
 * @see EnchantmentInfo#getEffect()
 */
public interface EnchantmentEffect extends TowerStatsProvider, Serializable {

    /**
     * Updates the effect applied to the specified enemy based on the elapsed time.
     *
     * @param enemy     The enemy onto which this effect is applied.
     * @param elapsedNs The time elapsed since the last update, in nanoseconds. Used to
     *                  determine the progression or impact of the effect over time.
     */
    void updateEffect(IEnemy enemy, long elapsedNs);

    /**
     * Terminates the effect applied to the specified enemy. This method marks the effect as
     * {@code expired}.
     * <p>
     * This method concludes the influence of the effect on the enemy, it ensures that any
     * temporary modifications to the enemy are reverted.
     *
     * @param enemy The enemy on which the effect is currently applied.
     * @see #isExpired()
     */
    void endEffect(IEnemy enemy);

    /**
     * This method checks if the effect has expired. It should be called to determine
     * whether the effect is still active or has reached its end and should no longer
     * be applied to the enemy.
     *
     * @return true if the effect is expired, false otherwise.
     */
    boolean isExpired();

    /**
     * Retrieves the appearance state of an enemy as determined by the effect.
     *
     * @return The {@link EnemyAnimation.EnemyAppearance} value representing the current visual state
     * or appearance of the enemy when the effect is applied.
     */
    EnemyAnimation.EnemyAppearance getEnemyAppearance();
}
