package dev.emberline.game.model;

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
public record ProjectileInfo(Type type, int level) implements TowerStatsProvider, UpgradableInfo<ProjectileInfo.Type, ProjectileInfo> {
    /**
     * Represents the type of projectile in the game, influencing the projectile's behavior, such as damage, speed,
     * firing rate, and other attributes.
     */
    public enum Type implements UpgradableInfo.InfoType {
        /** The default projectile type. It has no special properties and cannot be upgraded. */
        BASE,
        /** Represents a projectile with a higher firing rate but lower damage. */
        SMALL,
        /** Represents a projectile with a lower firing rate but higher damage with an area of effect. */
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
     * @param type the type of the projectile. Must not be {@code null}.
     * @param level the upgrade level of the projectile.
     *              Must be in the range of 0 to {@code MAX_LEVEL}.
     *              For {@code Type.BASE}, the level must be 0 as it does not allow upgrades.
     *
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

    @Override
    public boolean canUpgrade() {
        return type != Type.BASE && level < MAX_LEVEL;
    }

    @Override
    public boolean canChangeType() {
        return type == Type.BASE;
    }

    @Override
    public ProjectileInfo getUpgrade() {
        if (canUpgrade()) {
            return new ProjectileInfo(type, level + 1);
        }
        throw new IllegalStateException("Cannot upgrade projectile: " + this);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public ProjectileInfo getChangeType(Type newType) {
        if (canChangeType()) {
            return new ProjectileInfo(newType, 0);
        }
        throw new IllegalStateException("Cannot change type of projectile: " + this);
    }

    @Override
    public ProjectileInfo getDefault() {
        return new ProjectileInfo(ProjectileInfo.Type.BASE, 0);
    }

    // STATS //
    private static class Stats {
        private static final int BASE_UPGRADE_COST = 50;
        private static final int[] UPGRADE_COSTS = {100, 100, 100, 100, 0};
        // Fire rate (Hz)
        private static final double BASE_FIRE_RATE = 0.8;
        private static final double[] SMALL_FIRE_RATE = {1.0, 1.2, 1.4, 1.6, 1.8};
        private static final double[] BIG_FIRE_RATE = {0.5, 0.6, 0.7, 0.8, 0.9};
        // Damage (hp)
        private static final double BASE_DAMAGE = 25;
        private static final double[] SMALL_DAMAGE = {21, 22, 23, 24, 25};
        private static final double[] BIG_DAMAGE = {27, 29, 31, 33, 35};
        // Damage Area (radius in tiles) - only the BIG type has damage area, check getDamageArea
        private static final double[] BIG_DAMAGE_AREA = {0.5, 0.6875, 0.875, 1.0625, 1.25};
        // Tower Range (radius in tiles)
        private static final double[] TOWER_RANGE = {3, 3.2, 3.4, 4, 5};
        // Projectile speed (tiles per second)
        private static final double BASE_PROJECTILE_SPEED = 0.9;
        private static final double[] SMALL_PROJECTILE_SPEED = {1.0, 1.2, 1.4, 1.6, 1.8};
        private static final double[] BIG_PROJECTILE_SPEED = {0.7, 0.8, 0.9, 1.0, 1.1};
    }

    @Override
    public int getUpgradeCost() {
        if (type == Type.BASE) return Stats.BASE_UPGRADE_COST;
        return Stats.UPGRADE_COSTS[level];
    }

    /**
     * Returns the number of shots the tower can fire per second,
     * depending on the projectile type and upgrade level.
     *
     * @return the firing rate as a double value, measured in hertz.
     */
    public double getFireRate() {
        return switch (type) {
            case SMALL -> Stats.SMALL_FIRE_RATE[level];
            case BIG -> Stats.BIG_FIRE_RATE[level];
            case BASE -> Stats.BASE_FIRE_RATE;
        };
    }

    /**
     * Returns the base damage dealt by the projectile when it hits one or more enemies.
     *
     * @return the damage value as a double, measured in health points.
     */
    public double getDamage() {
        return switch (type) {
            case SMALL -> Stats.SMALL_DAMAGE[level];
            case BIG -> Stats.BIG_DAMAGE[level];
            case BASE -> Stats.BASE_DAMAGE;
        };
    }

    /**
     * Returns the effective radius around the impact point of the projectile where damage is applied.
     * Only projectiles of type {@code BIG} have an area of effect; for other types, this returns an empty Optional.
     * The damage area represents the radius within which the effect and the damage are applied.
     *
     * @return an Optional containing the damage area as a double value (radius in tiles) if the projectile type is BIG;
     *         otherwise, an empty Optional.
     */
    public Optional<Double> getDamageArea() {
        if (type == Type.BIG) return Optional.of(Stats.BIG_DAMAGE_AREA[level]);
        return Optional.empty();
    }

    /**
     * Returns the distance (in tile units) that the tower can reach with its projectiles.
     * Higher levels may increase the effective range.
     *
     * @return the tower range radius as a double value, measured in tiles.
     */
    public double getTowerRange() {
        return Stats.TOWER_RANGE[level];
    }

    /**
     * Retrieves the travel speed of the projectile based on its type and level.
     *
     * @return the travel speed of the projectile as a double value, measured in tiles per second.
     */
    public double getProjectileSpeed() {
        return switch (type) {
            case SMALL -> Stats.SMALL_PROJECTILE_SPEED[level];
            case BIG -> Stats.BIG_PROJECTILE_SPEED[level];
            case BASE -> Stats.BASE_PROJECTILE_SPEED;
        };
    }

    @Override
    public List<TowerStat> getTowerStats() {
        // To add the optional damage area a resizable list is needed,
        // Arrays.asList() returns a fixed size list so we need to create
        // a new one with the elements of the fixed size list.
        List<TowerStat> towerStats = new ArrayList<>(Arrays.asList(
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
