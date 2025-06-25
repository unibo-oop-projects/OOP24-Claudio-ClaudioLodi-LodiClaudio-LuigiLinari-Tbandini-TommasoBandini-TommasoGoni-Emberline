package dev.emberline.game.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.game.model.effects.BurnEffect;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.model.effects.SlowEffect;
import dev.emberline.gui.towerdialog.stats.TowerStat;
import dev.emberline.gui.towerdialog.stats.TowerStatsProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the information of an enchantment, including its type and level.
 * The enchantment can be of different types (see {@link EnchantmentInfo.Type}) and can be upgraded to different levels.
 * The class provides methods to check if the enchantment can be upgraded or if its type can be changed,
 * as well as methods to retrieve various properties of the enchantment such as upgrade cost and effects.
 *
 * @param type  The type of the enchantment.
 * @param level The level of the enchantment, which can be between 0 and {@link #MAX_LEVEL}.
 * @see EnchantmentEffect
 */
public record EnchantmentInfo(Type type,
                              int level) implements TowerStatsProvider, UpgradableInfo<EnchantmentInfo.Type, EnchantmentInfo> {

    /**
     * Represents the type of enchantment in the game.
     * The type of enchantment influences its {@code EnchantmentEffect}.
     */
    public enum Type implements UpgradableInfo.InfoType {
        /**
         * The default enchantment type. It has no effect and cannot be upgraded.
         */
        BASE,
        /**
         * Represents a fire enchantment that deals a {@link BurnEffect}.
         */
        FIRE,
        /**
         * Represents an ice enchantment that deals a {@link SlowEffect}.
         */
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
     * @param type  the type of the enchantment. Must not be {@code null}.
     * @param level the upgrade level of the enchantment.
     *              Must be in the range of 0 to {@code MAX_LEVEL}.
     *              For {@code Type.BASE}, the level must be 0 as it does not allow upgrades.
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
     * {@inheritDoc}
     */
    @Override
    public boolean canUpgrade() {
        return type != Type.BASE && level < MAX_LEVEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canChangeType() {
        return type == Type.BASE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EnchantmentInfo getUpgrade() {
        if (canUpgrade()) {
            return new EnchantmentInfo(type, level + 1);
        }
        throw new IllegalStateException("Cannot upgrade enchantment of type " + type + " at level " + level);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EnchantmentInfo getChangeType(final Type newType) {
        if (canChangeType()) {
            return new EnchantmentInfo(newType, 0);
        }
        throw new IllegalStateException("Cannot change type of enchantment of type " + type + " at level " + level);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EnchantmentInfo getDefault() {
        return new EnchantmentInfo(Type.BASE, 0);
    }

    private record Metadata (
        @JsonProperty
        int baseUpgradeCost,
        @JsonProperty
        int[] upgradeCosts,
        @JsonProperty
        int[] resetRefunds,
        @JsonProperty
        double[] effectDuration,
        @JsonProperty
        double[] fireDamagePerSecond,
        @JsonProperty
        double[] iceSlowingFactor
    ) {}

    private final static Metadata METADATA = ConfigLoader.loadConfig("/sprites/towerAssets/enchantmentInfoStats.json", Metadata.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUpgradeCost() {
        if (type == Type.BASE) {
            return METADATA.baseUpgradeCost;
        }
        return METADATA.upgradeCosts[level];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRefundValue() {
        if (type == Type.BASE) {
            return 0;
        }
        return METADATA.resetRefunds[level];
    }

    /**
     * Retrieves the effect associated with the enchantment's type and level.
     * The effect is determined by the enchantment type and its respective parameters.
     * An {@code EnchantmentEffect} will be returned for types {@code FIRE} and {@code ICE} based on their specific attributes.
     * If an enchantment type is {@code BASE}, no effect is returned.
     *
     * @return An {@code Optional<EnchantmentEffect>} that contains the related effect if applicable,
     * or an empty {@code Optional} if no effect is associated with the enchantment.
     */
    public Optional<EnchantmentEffect> getEffect() {
        final double duration = METADATA.effectDuration[level];
        return Optional.ofNullable(switch (type) {
            case Type.ICE -> new SlowEffect(METADATA.iceSlowingFactor[level], duration);
            case Type.FIRE -> new BurnEffect(METADATA.fireDamagePerSecond[level], duration);
            case Type.BASE -> null;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TowerStat> getTowerStats() {
        final List<TowerStat> towerStats = new ArrayList<>();

        // Optional effect
        getEffect().ifPresent(effect -> towerStats.addAll(effect.getTowerStats()));

        return towerStats;
    }
}
