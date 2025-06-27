package dev.emberline.game.model;

import java.io.Serializable;

/**
 * Provides information about a tower's projectile and enchantment capabilities.
 * Towers should implement this interface to provide their specific projectile and enchantment information.
 * <p>
 * A reference to a {@code TowerInfoProvider} may be stored in order to keep information
 * up to date over time. Therefore, if the underlying information can change during the
 * lifetime of the provider, it should be designed to be mutable and always return
 * the most current data.
 */
public interface TowerInfoProvider extends Serializable {
    /**
     * Retrieves the projectile information associated with the tower.
     *
     * @return an instance of ProjectileInfo representing the characteristics of the tower's projectile.
     */
    ProjectileInfo getProjectileInfo();

    /**
     * Retrieves the enchantment information associated with the tower.
     *
     * @return an instance of EnchantmentInfo representing the characteristics of the tower's enchantments.
     */
    EnchantmentInfo getEnchantmentInfo();
}
