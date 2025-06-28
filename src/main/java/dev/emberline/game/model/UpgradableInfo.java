package dev.emberline.game.model;

import java.io.Serializable;

/**
 * An {@code UpgradableInfo} represents a family of attributes that can be altered through upgrades or type changes.
 * Implementations of this interface must be {@code immutable}:
 * type changes and upgrades must return a new instance of the object.
 * <p>
 * Every implementing class of this interface must be linked to one and only one implementation of {@link InfoType}.
 *
 * @param <T>    the class of the type of the specific {@code UpgradableInfo} implementation,
 *               which must implement the {@link InfoType} interface.
 *               This is used to link the object to its specific type information enforcing type safety.
 * @param <S> the class type of the implementing class, used for referencing itself in method signatures, for type safety.
 */
public interface UpgradableInfo<T extends UpgradableInfo.InfoType, S extends UpgradableInfo<T, S>> extends Serializable {
    /**
     * This is a tag interface,
     * every implementing class must be linked to one and only one implementation of {@link UpgradableInfo}.
     * Implementations of this interface
     * must be used to define and identify every possible type of the relative {@code UpgradableInfo} object.
     */
    interface InfoType extends Serializable {
    }

    /**
     * Retrieves the current level of the object.
     *
     * @return the current level value.
     */
    int level();

    /**
     * Retrieves the type information of the current object.
     *
     * @return the type instance associated with this object.
     */
    T type();

    /**
     * Retrieves the maximum level to which the current object can be upgraded.
     *
     * @return the maximum upgrade level for this object.
     */
    int getMaxLevel();

    /**
     * Determines if the current object is eligible for an upgrade.
     *
     * @return true if the object can be upgraded; false otherwise.
     */
    boolean canUpgrade();

    /**
     * Determines if the current object type can be changed to a different type.
     *
     * @return true if the object type can be changed, false otherwise.
     */
    boolean canChangeType();

    /**
     * Returns a new instance of the same object upgraded to the next level.
     * <p>
     * Before calling this method, ensure that {@link #canUpgrade()} returns true.
     *
     * @return the upgraded version of the current object.
     */
    S getUpgrade();

    /**
     * Returns a new instance of the same object updated to the specified type.
     * <p>
     * Before calling this method, ensure that {@link #canChangeType()} returns true.
     *
     * @param type the target type to which the current instance should be changed.
     * @return the updated version of the current object to the specified type.
     */
    S getChangeType(T type);

    /**
     * Calculates and retrieves the cost required for upgrading or transitioning from the base type to a specific type.
     * <p>
     * The presence of an upgrade cost does not guarantee that upgrading is possible, you must always check
     * {@link #canUpgrade()} before attempting to upgrade or {@link #canChangeType()} before changing the type.
     *
     * @return The cost associated with either upgrading to the next level or switching from the base type.
     */
    int getUpgradeCost();


    /**
     * Calculates and retrieves the refund value for the current object.
     * <p>
     * The refund value half of the upgrade cost,
     * and it is returned when the Enchant/Projectile size get resetted.
     *
     * @return The refund value associated with the current object.
     */
    int getRefundValue();

    /**
     * Retrieves an instance representing the default state.
     *
     * @return a new instance of the object in its default state.
     */
    S getDefault();
}
