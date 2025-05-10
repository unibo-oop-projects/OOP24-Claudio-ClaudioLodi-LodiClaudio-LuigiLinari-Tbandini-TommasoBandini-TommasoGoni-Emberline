package dev.emberline.game.model;

import dev.emberline.game.model.effects.BurnEffect;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.model.effects.SlowEffect;

import java.util.Optional;

/**
 * Represents the information of an enchantment, including its type and level.
 * The enchantment can be of different types (see {@link EnchantmentInfo.Type}) and can be upgraded to different levels.
 * The class provides methods to check if the enchantment can be upgraded or if its type can be changed,
 * as well as methods to retrieve various properties of the enchantment such as upgrade cost and effects.
 *
 * @param type  The type of the enchantment.
 * @param level The level of the enchantment, which can be between 0 and {@link #MAX_LEVEL}.
 *
 * @see EnchantmentEffect
 */
public record EnchantmentInfo(Type type, int level) {

    /**
     * Represents the type of enchantment in the game.
     * The type of enchantment influences its {@code EnchantmentEffect}.
     */
    public enum Type {
        /** The default enchantment type. It has no effect and cannot be upgraded. */
        BASE,
        /** Represents a fire enchantment that deals a {@link BurnEffect}. */
        FIRE,
        /** Represents an ice enchantment that deals a {@link SlowEffect}. */
        ICE
    }

    /**
     * The maximum level an enchantment can achieve through upgrades.
     * This constant determines the upper limit for enchantment levels,
     * controlling progression and upgrades within the system.
     */
    public static final int MAX_LEVEL = 4;

    /**
     * Constructs a new {@code EnchantmentInfo} object with validation of its parameters.
     *
     * @param type the type of the enchantment. Must not be {@code null}.
     * @param level the upgrade level of the enchantment.
     *              Must be in the range of 0 to {@code MAX_LEVEL}.
     *              For {@code Type.BASE}, the level must be 0 as it does not allow upgrades.
     *
     * @throws IllegalArgumentException if parameters do not meet the specified constraints.
     */
    public EnchantmentInfo {
        if (type == null) {
            throw new IllegalArgumentException("EnchantmentInfo: 'type' must not be null");
        }
        if (level < 0 || level > MAX_LEVEL) {
            throw new IllegalArgumentException("EnchantmentInfo: 'level' must be between 0 and " + MAX_LEVEL);
        }
        if (type == Type.BASE && level != 0) {
            throw new IllegalArgumentException("EnchantmentInfo: BASE type does not allow upgrades");
        }
    }

    /**
     * Determines whether the enchantment can be upgraded. A enchantment can be upgraded
     * if its current type is not {@code Type.BASE} and its level is lower than the maximum level.
     * An enchantment of type {@code Type.BASE} cannot be upgraded and can only be changed to a specific type.
     *
     * @see #canChangeType()
     * @return {@code true} if the enchantment can be upgraded, otherwise {@code false}.
     */
    public boolean canUpgrade() {
        return type != Type.BASE && level < MAX_LEVEL;
    }

    /**
     * Determines whether the enchantment's type can be changed.
     * An enchantment's type can be changed if its level is currently 0.
     *
     * @see #canUpgrade()
     * @return {@code true} if the current level is 0 and the type can be changed,
     *         otherwise {@code false}.
     */
    public boolean canChangeType() {
        return level == 0;
    }

    // STATS //
    // Upgrade costs
    private static final int BASE_UPGRADE_COST = 50;
    private static final int[] UPGRADE_COSTS = {100, 100, 100, 100, 0};
    // Effect duration
    private static final double[] EFFECT_DURATION = {3, 3.2, 3.4, 3.6, 3.8};
    // Effects
    private static final double[] FIRE_DAMAGE_PER_SECOND = {20,25,30,35,40};
    private static final double[] ICE_SLOWING_FACTOR = {0.9,0.8,0.7,0.6,0.5};

    /**
     * <p> Calculates and retrieves the cost required for upgrading the enchantment to the next level
     * or transitioning from the base type to a specific enchantment type. </p>
     * <p> The presence of an upgrade cost does not guarantee that upgrading is possible, you must always check
     * {@link #canUpgrade()} before attempting to upgrade or {@link #canChangeType()} before changing the type. </p>
     *
     * @return The cost associated with either upgrading to the next level or switching from the base type.
     */
    public int getUpgradeCost() {
        if (type == Type.BASE) return BASE_UPGRADE_COST;
        return UPGRADE_COSTS[level];
    }

    /**
     * Retrieves the effect associated with the enchantment's type and level.
     * The effect is determined by the enchantment type and its respective parameters.
     * An {@code EnchantmentEffect} will be returned for types {@code FIRE} and {@code ICE} based on their specific attributes.
     * If an enchantment type is {@code BASE}, no effect is returned.
     *
     * @return An {@code Optional<EnchantmentEffect>} that contains the related effect if applicable,
     *         or an empty {@code Optional} if no effect is associated with the enchantment.
     */
    public Optional<EnchantmentEffect> getEffect() {
        double duration = EFFECT_DURATION[level];
        return Optional.ofNullable(switch (type) {
            case Type.ICE -> new SlowEffect(ICE_SLOWING_FACTOR[level], duration);
            case Type.FIRE -> new BurnEffect(FIRE_DAMAGE_PER_SECOND[level], duration);
            case Type.BASE -> null;
        });
    }
}
