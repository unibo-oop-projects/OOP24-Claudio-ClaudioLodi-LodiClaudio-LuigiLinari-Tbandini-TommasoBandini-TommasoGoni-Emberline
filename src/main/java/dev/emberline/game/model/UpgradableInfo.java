package dev.emberline.game.model;

public interface UpgradableInfo<T extends UpgradableInfo.InfoType, SELF extends UpgradableInfo<T, SELF>> {
    interface InfoType {}

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
    SELF getUpgrade();

    /**
     * Returns a new instance of the same object updated to the specified type.
     * <p>
     * Before calling this method, ensure that {@link #canChangeType()} returns true.
     *
     * @param type the target type to which the current instance should be changed.
     * @return the updated version of the current object to the specified type.
     */
    SELF getChangeType(T type);

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
     * Retrieves the default instance of the implementing object.
     *
     * @return the default instance of the object.
     */
    SELF getDefault();
}
