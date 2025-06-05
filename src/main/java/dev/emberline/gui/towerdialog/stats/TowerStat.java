package dev.emberline.gui.towerdialog.stats;

import dev.emberline.gui._TemporarySpriteProvider;
import javafx.scene.image.Image;
import dev.emberline.gui.towerdialog.stats.TowerStatsViewsBuilder.TowerStatView;

/**
 * A tower stat is a measurable property of a tower, such as damage, range, or attack speed.
 * These stats are used to describe the tower's performance and can be displayed in the GUI
 * for comparison or informational purposes using {@link TowerStatView} constructed by {@link TowerStatsViewsBuilder}.
 *
 * @param type The type of the tower stat, for example, damage, range, or attack speed.
 * @param value The value of the tower stat, for example, 100 for damage, 5 for range, or 1.5 for attack speed.
 *
 * @see TowerStatsProvider
 */
public record TowerStat(TowerStatType type, double value) {
    public enum TowerStatType {
        // Projectile stats
        FIRE_RATE("Fire Rate", _TemporarySpriteProvider.FIRE_RATE.getImage()),
        DAMAGE("Damage", _TemporarySpriteProvider.DAMAGE.getImage()),
        PROJECTILE_SPEED("Projectile Speed", _TemporarySpriteProvider.PROJECTILE_SPEED.getImage()),
        TOWER_RANGE("Tower Range", _TemporarySpriteProvider.TOWER_RANGE.getImage()),
        DAMAGE_AREA("Damage Radius", _TemporarySpriteProvider.DAMAGE_AREA.getImage()),
        // Enchantment stats
        EFFECT_DURATION("Effect Duration", _TemporarySpriteProvider.EFFECT_DURATION.getImage()),
        BURN_EFFECT("Dmg per sec", _TemporarySpriteProvider.BURN_EFFECT.getImage()),
        SLOW_EFFECT("Slowing Factor", _TemporarySpriteProvider.SLOW_EFFECT.getImage());

        private final String displayName;
        private final Image icon;

        TowerStatType(String displayName, Image icon) {
            this.displayName = displayName;
            this.icon = icon;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Image getIcon() {
            return icon;
        }
    }
}
