package dev.emberline.game.model.effects;

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
 * <p>
 * This effect will never expire, even if {@link #endEffect(IEnemy)} is called.
 */
public class DummyEffect implements EnchantmentEffect {

    /**
     * Constructs a {@code DummyEffect} instance.
     * @see DummyEffect
     */
    public DummyEffect() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEffect(final IEnemy enemy, final long elapsedNs) {
    }

    /**
     * In this implementation, the method does nothing and will not expire the effect.
     */
    @Override
    public void endEffect(final IEnemy enemy) {
    }

    /**
     * {@inheritDoc}
     * This implementation always returns {@code false} as the DummyEffect does not expire.
     * It is intended to be a permanent effect without any time-based expiration.
     * <p>
     * Calling {@link #endEffect(IEnemy)} will not expire this effect.
     */
    @Override
    public boolean isExpired() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TowerStat> getTowerStats() {
        return List.of();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EnemyAnimation.EnemyAppearance getEnemyAppearance() {
        return EnemyAnimation.EnemyAppearance.NORMAL;
    }
}
