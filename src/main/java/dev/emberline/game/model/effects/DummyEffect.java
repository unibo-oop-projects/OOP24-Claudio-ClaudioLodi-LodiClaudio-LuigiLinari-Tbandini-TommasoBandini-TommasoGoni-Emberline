package dev.emberline.game.model.effects;

import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.world.entities.enemies.enemy.EnemyAnimation;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.gui.towerdialog.stats.TowerStat;

import java.util.List;

/**
 * DummyEffect is a simple implementation of the {@link EnchantmentEffect} interface.
 * This implementation represents a base enchantment effect that does not provide any
 * additional behavior, such as modifying enemy behavior or applying specific stats.
 * It can be used as a placeholder or as the default state for non-upgraded enchantments.
 * <p>
 * It is associated with the {@code BASE} enchantment type, which is the default type
 * without any special effects or upgrade capabilities.
 */
public class DummyEffect implements EnchantmentEffect {
    @Override
    public void updateEffect(IEnemy enemy, long elapsed) {
        // TODO
    }

    @Override
    public boolean isExpired() {
        return true;
    }

    @Override
    public EnchantmentInfo.Type getEnchantmentType() {
        return EnchantmentInfo.Type.BASE;
    }

    @Override
    public List<TowerStat> getTowerStats() {
        return List.of();
    }

    @Override
    public EnemyAnimation.EnemyAppearance getEnemyAppearance() {
        return EnemyAnimation.EnemyAppearance.NORMAL;
    }
}
