package dev.emberline.game.model.effects;

import dev.emberline.game.world.entities.enemies.enemy.EnemyAnimation;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.gui.towerdialog.stats.TowerStat;
import dev.emberline.gui.towerdialog.stats.TowerStat.TowerStatType;

import java.util.List;

/**
 * Represents a slow effect that slows down an enemy by a certain factor over a specified duration.
 * The effect is associated with ice enchantments in the game.
 *
 * @see dev.emberline.game.model.EnchantmentInfo.Type#ICE
 * @see dev.emberline.game.world.entities.enemies.enemy.EnemyAnimation.EnemyAppearance#FREEZING
 */
public class SlowEffect implements EnchantmentEffect {

    private static final int SECONDS_IN_NS = 1_000_000_000;

    private final double duration;
    private final double slowingFactor;

    private final long durationNs;
    private long totalElapsed = 0;

    private boolean isExpired = false;

    /**
     * Constructs a {@code SlowEffect} that applies a slowing effect to an enemy.
     *
     * @param slowingFactor The factor by which the enemy's speed is reduced (e.g., 0.5 for half the speed).
     * @param duration      The total duration of the burn effect in seconds.
     */
    public SlowEffect(final double slowingFactor, final double duration) {
        this.slowingFactor = slowingFactor;
        this.duration = duration;
        this.durationNs = (long) (duration * SECONDS_IN_NS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEffect(final IEnemy enemy, final long elapsedNs) {
        totalElapsed += elapsedNs;
        if (totalElapsed >= durationNs) {
            endEffect(enemy);
            return;
        }
        enemy.setSlowFactor(slowingFactor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endEffect(final IEnemy enemy) {
        enemy.setSlowFactor(1.0);
        isExpired = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExpired() {
        return isExpired;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TowerStat> getTowerStats() {
        return List.of(new TowerStat(TowerStatType.SLOW_EFFECT, slowingFactor),
                new TowerStat(TowerStatType.EFFECT_DURATION, duration)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EnemyAnimation.EnemyAppearance getEnemyAppearance() {
        return EnemyAnimation.EnemyAppearance.FREEZING;
    }
}
