package dev.emberline.gui.towerdialog.stats;

import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import dev.emberline.gui.towerdialog.stats.TowerStatsViewsBuilder.TowerStatView;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.scene.image.Image;

/**
 * A tower stat is a measurable property of a tower, such as damage, range, or attack speed.
 * These stats are used to describe the tower's performance and can be displayed in the GUI
 * for comparison or informational purposes using {@link TowerStatView} constructed by {@link TowerStatsViewsBuilder}.
 *
 * @param type  The type of the tower stat, for example, damage, range, or attack speed.
 * @param value The value of the tower stat, for example, 100 for damage, 5 for range, or 1.5 for attack speed.
 * @see TowerStatsProvider
 */
public record TowerStat(TowerStatType type, double value) {
    /**
     * An enum representing various types of statistical properties for towers.
     * These properties describe specific attributes of a tower's performance, such as the
     * rate of fire, damage output, range, and special effects. Each type is associated with
     * a human-readable name and a graphical icon.
     * <p>
     * The enumeration is divided into two categories:
     * <ul>
     * <li>Projectile stats: Attributes affecting the tower's basic attack capabilities.</li>
     * <li>Enchantment stats: Attributes affecting additional special effects applied by the tower.</li>
     * </ul>
     */
    public enum TowerStatType {
        // Projectile stats
        /**
         * Represents the "Fire Rate" statistical property of a tower, describing the frequency
         * at which the tower can attack or fire projectiles during gameplay.
         */
        FIRE_RATE("Fire Rate", SpriteLoader.loadSprite(SingleSpriteKey.FIRE_RATE).image()),
        /**
         * Represents the "Damage" statistical property of a tower.
         * This property defines the amount of damage dealt to targets by a tower's projectiles or attacks.
         */
        DAMAGE("Damage", SpriteLoader.loadSprite(SingleSpriteKey.DAMAGE).image()),
        /**
         * Represents the "Projectile Speed" statistical property of a tower.
         * This property defines the velocity at which a tower's projectiles travel
         * through the game environment.
         */
        PROJECTILE_SPEED("Projectile Speed", SpriteLoader.loadSprite(SingleSpriteKey.PROJECTILE_SPEED).image()),
        /**
         * Represents the "Tower Range" statistical property of a tower.
         * This property defines the maximum distance at which the tower can target enemies.
         */
        TOWER_RANGE("Tower Range", SpriteLoader.loadSprite(SingleSpriteKey.TOWER_RANGE).image()),
        /**
         * Represents the "Damage Area" statistical property of a tower.
         * This property defines the area affected by the projectile hit.
         */
        DAMAGE_AREA("Damage Radius", SpriteLoader.loadSprite(SingleSpriteKey.DAMAGE_AREA).image()),

        // Enchantment stats
        /**
         * Represents the duration of an effect applied by a tower in the game.
         */
        EFFECT_DURATION("Effect Duration", SpriteLoader.loadSprite(SingleSpriteKey.EFFECT_DURATION).image()),
        /**
         * Represents the burn effect stat for a tower, which damages targets over time.
         */
        BURN_EFFECT("Dmg per sec", SpriteLoader.loadSprite(SingleSpriteKey.BURN_EFFECT).image()),
        /**
         * Represents the slow effect stat for a tower, which slows targets over time.
         */
        SLOW_EFFECT("Slowing Factor", SpriteLoader.loadSprite(SingleSpriteKey.SLOW_EFFECT).image());

        private final String displayName;
        private final Image icon;

        TowerStatType(final String displayName, final Image icon) {
            this.displayName = displayName;
            this.icon = icon;
        }

        /**
         * Returns the human-readable display name associated with this tower stat type.
         *
         * @return the human-readable display name associated with this tower stat type.
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Returns the icon representing the tower stat type.
         *
         * @return the icon associated with the tower stat type
         */
        @SuppressFBWarnings(
                value = "EI_EXPOSE_REP",
                justification = "This is intended behaviour as Image is an immutable container."
        )
        public Image getIcon() {
            return icon;
        }
    }
}
