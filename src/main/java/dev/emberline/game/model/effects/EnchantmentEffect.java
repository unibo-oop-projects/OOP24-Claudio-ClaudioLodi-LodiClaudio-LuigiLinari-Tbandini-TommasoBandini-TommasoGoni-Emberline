package dev.emberline.game.model.effects;

import dev.emberline.gui.towerdialog.stats.TowerStatsProvider;

/**
 * EnchantmentEffects are effects that can be applied to enemies modifying their behavior.
 * EnchantmentEffects get determined by the enchantment type (of an enchantment).
 */
public interface EnchantmentEffect extends TowerStatsProvider {
    // void updateEffect(Enemy enemy, long elapsed);

    /**
     * Checks if the effect is expired.
     *
     * @return true if the effect is expired, false otherwise.
     */
    boolean isExpired();
}
