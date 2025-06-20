package dev.emberline.game.model.effects;

import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.world.entities.enemies.enemy.EnemyAnimation;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.gui.towerdialog.stats.TowerStatsProvider;

/**
 * EnchantmentEffects are effects that can be applied to enemies modifying their behavior.
 * EnchantmentEffects get determined by the enchantment type (of an enchantment).
 */
public interface EnchantmentEffect extends TowerStatsProvider {

    /**
     * Updates the effect applied to the specified enemy based on the elapsed time.
     *
     * @param enemy  The enemy onto which this effect is applied. This object represents
     *               the target whose attributes or behavior might be modified by the
     *               effect.
     * @param elapsed  The time elapsed since the last update, in nanoseconds. Used to
     *                 determine the progression or impact of the effect over time.
     */
    void updateEffect(IEnemy enemy, long elapsed);

    void endEffect(IEnemy enemy);

    /**
     * Checks if the effect is expired.
     *
     * @return true if the effect is expired, false otherwise.
     */
    boolean isExpired();

    /**
     * Retrieves the type of enchantment associated with this effect.
     *
     * @return The {@link EnchantmentInfo.Type} corresponding to this enchantment effect.
     */
    EnchantmentInfo.Type getEnchantmentType();

    EnemyAnimation.EnemyAppearance getEnemyAppearance();
}
