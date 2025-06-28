package dev.emberline.game.model.effects;

import dev.emberline.game.world.entities.enemies.enemy.EnemyAnimation;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.gui.towerdialog.stats.TowerStat;
import dev.emberline.gui.towerdialog.stats.TowerStat.TowerStatType;

import java.io.Serial;
import java.util.List;

/**
 * Represents a burn effect that deals damage over time.
 * The effect is associated with fire enchantments in the game.
 *
 * @see dev.emberline.game.model.EnchantmentInfo.Type#FIRE
 * @see dev.emberline.game.world.entities.enemies.enemy.EnemyAnimation.EnemyAppearance#BURNING
 */
public class BurnEffect implements EnchantmentEffect {
    @Serial
    private static final long serialVersionUID = 4592711708572597944L;

    private static final int SECONDS_IN_NS = 1_000_000_000;

    private final double damagePerSecond;
    private final double damagePerNs;

    private final double duration;
    private final long durationNs;
    private long totalElapsed;

    private boolean isExpired;

    /**
     * Constructs a {@code BurnEffect} that applies burn damage to an enemy over a specified duration.
     *
     * @param damagePerSecond The amount of damage dealt every second, measured in hit points (hp).
     * @param duration        The total duration of the burn effect in seconds.
     */
    public BurnEffect(final double damagePerSecond, final double duration) {
        this.damagePerSecond = damagePerSecond;
        this.damagePerNs = damagePerSecond / SECONDS_IN_NS;

        this.duration = duration;
        this.durationNs = (long) (duration * SECONDS_IN_NS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEffect(final IEnemy enemy, final long elapsedNs) {
        if (isExpired) {
            return;
        }
        totalElapsed += elapsedNs;
        if (totalElapsed >= durationNs) {
            // Deal remaining damage before ending effect
            enemy.dealDamage(damagePerNs * (durationNs - (totalElapsed - elapsedNs)));
            endEffect(enemy);
            return;
        }
        enemy.dealDamage(damagePerNs * elapsedNs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endEffect(final IEnemy enemy) {
        // No specific end effect for burn; it naturally expires after the duration.
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
        return List.of(new TowerStat(TowerStatType.BURN_EFFECT, damagePerSecond),
                new TowerStat(TowerStatType.EFFECT_DURATION, duration)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EnemyAnimation.EnemyAppearance getEnemyAppearance() {
        return EnemyAnimation.EnemyAppearance.BURNING;
    }
}
