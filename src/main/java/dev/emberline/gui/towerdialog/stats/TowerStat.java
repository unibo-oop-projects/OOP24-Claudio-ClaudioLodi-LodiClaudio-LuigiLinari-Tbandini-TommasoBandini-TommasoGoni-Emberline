package dev.emberline.gui.towerdialog.stats;

import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import dev.emberline.gui.towerdialog.stats.TowerStatsViewsBuilder.TowerStatView;
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
    public enum TowerStatType {
        // Projectile stats
        FIRE_RATE("Fire Rate", SpriteLoader.loadSprite(SingleSpriteKey.FIRE_RATE).image()),
        DAMAGE("Damage", SpriteLoader.loadSprite(SingleSpriteKey.DAMAGE).image()),
        PROJECTILE_SPEED("Projectile Speed", SpriteLoader.loadSprite(SingleSpriteKey.PROJECTILE_SPEED).image()),
        TOWER_RANGE("Tower Range", SpriteLoader.loadSprite(SingleSpriteKey.TOWER_RANGE).image()),
        DAMAGE_AREA("Damage Radius", SpriteLoader.loadSprite(SingleSpriteKey.DAMAGE_AREA).image()),
        // Enchantment stats
        EFFECT_DURATION("Effect Duration", SpriteLoader.loadSprite(SingleSpriteKey.EFFECT_DURATION).image()),
        BURN_EFFECT("Dmg per sec", SpriteLoader.loadSprite(SingleSpriteKey.BURN_EFFECT).image()),
        SLOW_EFFECT("Slowing Factor", SpriteLoader.loadSprite(SingleSpriteKey.SLOW_EFFECT).image());

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
