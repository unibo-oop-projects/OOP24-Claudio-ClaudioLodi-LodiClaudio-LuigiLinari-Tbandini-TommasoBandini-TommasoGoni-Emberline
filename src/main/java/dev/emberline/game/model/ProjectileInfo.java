package dev.emberline.game.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.gui.towerdialog.stats.TowerStat;
import dev.emberline.gui.towerdialog.stats.TowerStatsProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static dev.emberline.gui.towerdialog.stats.TowerStat.TowerStatType;

/**
 * Represents the information of a projectile, including its type and level.
 * The projectile can be of different types (see {@link ProjectileInfo.Type}) and can be upgraded to different levels.
 * The class provides methods to check if the projectile can be upgraded or if its type can be changed,
 * as well as methods to retrieve various properties of the projectile such as fire rate, damage, range, etc.
 *
 * @param type  The type of the projectile.
 * @param level The level of the projectile, which can be between 0 and {@link #MAX_LEVEL}.
 */
public record ProjectileInfo(Type type,
                             int level) implements TowerStatsProvider, UpgradableInfo<ProjectileInfo.Type, ProjectileInfo> {
    /**
     * Represents the type of projectile in the game, influencing the projectile's behavior, such as damage, speed,
     * firing rate, and other attributes.
     */
    public enum Type implements UpgradableInfo.InfoType {
        /**
         * The default projectile type. It has no special properties and cannot be upgraded.
         */
        BASE,
        /**
         * Represents a projectile with a higher firing rate but lower damage.
         */
        SMALL,
        /**
         * Represents a projectile with a lower firing rate but higher damage with an area of effect.
         */
        BIG
    }

    /**
     * The maximum level a projectile can achieve through upgrades.
     * This constant determines the upper limit for projectile levels,
     * controlling progression and upgrades within the system.
     */
    public static final int MAX_LEVEL = 4;

    /**
     * Constructs a new {@code ProjectileInfo} object with validation of its parameters.
     *
     * @param type  the type of the projectile. Must not be {@code null}.
     * @param level the upgrade level of the projectile.
     *              Must be in the range of 0 to {@code MAX_LEVEL}.
     *              For {@code Type.BASE}, the level must be 0 as it does not allow upgrades.
     * @throws IllegalArgumentException if parameters do not meet the specified constraints.
     */
    public ProjectileInfo {
        if (type == null) {
            throw new IllegalArgumentException("ProjectileInfo: 'type' must not be null");
        }
        if (level < 0 || level > MAX_LEVEL) {
            throw new IllegalArgumentException("ProjectileInfo: 'level' must be between 0 and " + MAX_LEVEL);
        }
        if (type == Type.BASE && level != 0) {
            throw new IllegalArgumentException("ProjectileInfo: BASE type does not allow upgrades");
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
    public boolean canChangeType() {
        return type == Type.BASE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectileInfo getUpgrade() {
        if (canUpgrade()) {
            return new ProjectileInfo(type, level + 1);
        }
        throw new IllegalStateException("Cannot upgrade projectile: " + this);
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
    public ProjectileInfo getChangeType(final Type newType) {
        if (canChangeType()) {
            return new ProjectileInfo(newType, 0);
        }
        throw new IllegalStateException("Cannot change type of projectile: " + this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectileInfo getDefault() {
        return new ProjectileInfo(ProjectileInfo.Type.BASE, 0);
    }

    private static class Metadata {
        @JsonProperty("baseUpgradeCost")
        private int BASE_UPGRADE_COST;
        @JsonProperty("upgradeCosts")
        private int[] UPGRADE_COSTS;
        @JsonProperty("resetRefunds")
        private int[] RESET_REFUNDS;
        @JsonProperty("baseFireRate")
        private double BASE_FIRE_RATE;
        @JsonProperty("fireRateSmall")
        private double[] SMALL_FIRE_RATE;
        @JsonProperty("fireRateBig")
        private double[] BIG_FIRE_RATE;
        @JsonProperty("baseDamage")
        private double BASE_DAMAGE;
        @JsonProperty("damageSmall")
        private double[] SMALL_DAMAGE;
        @JsonProperty("damageBig")
        private double[] BIG_DAMAGE;
        @JsonProperty("bigDamageArea")
        private double[] BIG_DAMAGE_AREA;
        @JsonProperty("towerRange")
        private double[] TOWER_RANGE;
        @JsonProperty("baseProjectileSpeed")
        private double BASE_PROJECTILE_SPEED;
        @JsonProperty("projectileSpeedSmall")
        private double[] SMALL_PROJECTILE_SPEED;
        @JsonProperty("projectileSpeedBig")
        private double[] BIG_PROJECTILE_SPEED;
    }

    private final static Metadata metadata = ConfigLoader.loadConfig("/sprites/towerAssets/projectileInfoStats.json", Metadata.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUpgradeCost() {
        if (type == Type.BASE) {
            return metadata.BASE_UPGRADE_COST;
        }
        return metadata.UPGRADE_COSTS[level];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRefundValue() {
        if (type == Type.BASE) {
            return 0;
        }
        return metadata.RESET_REFUNDS[level];
    }

    /**
     * Returns the number of shots the tower can fire per second,
     * depending on the projectile type and upgrade level.
     *
     * @return the firing rate as a double value, measured in hertz.
     */
    public double getFireRate() {
        return switch (type) {
            case SMALL -> metadata.SMALL_FIRE_RATE[level];
            case BIG -> metadata.BIG_FIRE_RATE[level];
            case BASE -> metadata.BASE_FIRE_RATE;
        };
    }

    /**
     * Returns the base damage dealt by the projectile when it hits one or more enemies.
     *
     * @return the damage value as a double, measured in health points.
     */
    public double getDamage() {
        return switch (type) {
            case SMALL -> metadata.SMALL_DAMAGE[level];
            case BIG -> metadata.BIG_DAMAGE[level];
            case BASE -> metadata.BASE_DAMAGE;
        };
    }

    /**
     * Returns the effective radius around the impact point of the projectile where damage is applied.
     * Only projectiles of type {@code BIG} have an area of effect; for other types, this returns an empty Optional.
     * The damage area represents the radius within which the effect and the damage are applied.
     *
     * @return an Optional containing the damage area as a double value (radius in tiles) if the projectile type is BIG;
     * otherwise, an empty Optional.
     */
    public Optional<Double> getDamageArea() {
        if (type == Type.BIG) {
            return Optional.of(metadata.BIG_DAMAGE_AREA[level]);
        }
        return Optional.empty();
    }

    /**
     * Returns the distance (in tile units) that the tower can reach with its projectiles.
     * Higher levels may increase the effective range.
     *
     * @return the tower range radius as a double value, measured in tiles.
     */
    public double getTowerRange() {
        return metadata.TOWER_RANGE[level];
    }

    /**
     * Retrieves the travel speed of the projectile based on its type and level.
     *
     * @return the travel speed of the projectile as a double value, measured in tiles per second.
     */
    public double getProjectileSpeed() {
        return switch (type) {
            case SMALL -> metadata.SMALL_PROJECTILE_SPEED[level];
            case BIG -> metadata.BIG_PROJECTILE_SPEED[level];
            case BASE -> metadata.BASE_PROJECTILE_SPEED;
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TowerStat> getTowerStats() {
        // To add the optional damage area a resizable list is needed,
        // Arrays.asList() returns a fixed size list so we need to create
        // a new one with the elements of the fixed size list.
        final List<TowerStat> towerStats = new ArrayList<>(Arrays.asList(
                new TowerStat(TowerStatType.FIRE_RATE, getFireRate()),
                new TowerStat(TowerStatType.DAMAGE, getDamage()),
                new TowerStat(TowerStatType.TOWER_RANGE, getTowerRange()),
                new TowerStat(TowerStatType.PROJECTILE_SPEED, getProjectileSpeed())
        ));
        // Optional damage area
        getDamageArea().ifPresent(area -> towerStats.add(new TowerStat(TowerStatType.DAMAGE_AREA, area)));

        return towerStats;
    }
}
