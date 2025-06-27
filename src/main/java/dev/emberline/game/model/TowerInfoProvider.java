package dev.emberline.game.model;

import dev.emberline.gui.event.SetTowerAimTypeEvent.AimType;
import dev.emberline.utility.Vector2D;

/**
 * Provides read-only access to a tower's information.
 * Towers should implement this interface to provide their specific information.
 * <p>
 * A reference to a {@code TowerInfoProvider} may be stored in order to keep information
 * up to date over time. Therefore, if the underlying information can change during the
 * lifetime of the provider, it should be designed to be mutable and always return
 * the most current data.
 */
public interface TowerInfoProvider {
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

    /**
     * Retrieves the aim type of the tower, which determines its targeting behavior.
     *
     * @return an instance of AimType representing the tower's current aiming strategy.
     */
    AimType getAimType();

    /**
     * Retrieves the current position of the tower as a two-dimensional vector.
     * <p>
     * This position is the center of the tower's base, which is used to determine
     * the tower's range circumference center.
     *
     * @return an instance of {@code Vector2D} representing the tower's current position.
     */
    Vector2D getPosition();
}
