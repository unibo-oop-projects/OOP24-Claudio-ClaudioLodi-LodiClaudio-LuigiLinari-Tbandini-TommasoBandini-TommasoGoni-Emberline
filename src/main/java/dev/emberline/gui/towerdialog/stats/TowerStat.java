package dev.emberline.gui.towerdialog.stats;

import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.UISpriteKey;
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
        FIRE_RATE("Fire Rate", SpriteLoader.loadSprite(UISpriteKey.FIRE_RATE).getImage()),
        DAMAGE("Damage", SpriteLoader.loadSprite(UISpriteKey.DAMAGE).getImage()),
        PROJECTILE_SPEED("Projectile Speed", SpriteLoader.loadSprite(UISpriteKey.PROJECTILE_SPEED).getImage()),
        TOWER_RANGE("Tower Range", SpriteLoader.loadSprite(UISpriteKey.TOWER_RANGE).getImage()),
        DAMAGE_AREA("Damage Radius", SpriteLoader.loadSprite(UISpriteKey.DAMAGE_AREA).getImage()),
        // Enchantment stats
        EFFECT_DURATION("Effect Duration", SpriteLoader.loadSprite(UISpriteKey.EFFECT_DURATION).getImage()),
        BURN_EFFECT("Dmg per sec", SpriteLoader.loadSprite(UISpriteKey.BURN_EFFECT).getImage()),
        SLOW_EFFECT("Slowing Factor", SpriteLoader.loadSprite(UISpriteKey.SLOW_EFFECT).getImage());

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
