package dev.emberline.game.model.effects;

import dev.emberline.game.model.EnchantmentInfo;
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

    /**
     * Retrieves the type of enchantment associated with this effect.
     *
     * @return The {@link EnchantmentInfo.Type} corresponding to this enchantment effect.
     */
    EnchantmentInfo.Type getEnchantmentType();
}
