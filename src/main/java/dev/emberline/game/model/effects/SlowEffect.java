package dev.emberline.game.model.effects;

import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.world.entities.enemies.enemy.EnemyAnimation;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.gui.towerdialog.stats.TowerStat;
import dev.emberline.gui.towerdialog.stats.TowerStat.TowerStatType;

import java.util.List;

/**
 * Represents a slow effect that slows down an enemy by a certain factor over a specified duration.
 * The effect is associated with ice enchantments in the game.
 *
 * @param duration The total duration in seconds over which the slow effect persists.
 * @param slowingFactor The multiplicative factor by which the speed of the affected entity is reduced.
 *
 * @see dev.emberline.game.model.EnchantmentInfo.Type#ICE
 */
public class SlowEffect implements EnchantmentEffect {

    private final double duration;
    private final double slowingFactor;

    private final long durationNs;
    private long totalElapsed = 0;

    private boolean isExpired = false;

    public SlowEffect(double slowingFactor, double duration) {
        this.slowingFactor = slowingFactor;
        
        this.duration = duration;
        this.durationNs = (long) (duration * 1_000_000_000);
    }

    @Override
    public void updateEffect(IEnemy enemy, long elapsed) {
        totalElapsed += elapsed;

        if (totalElapsed >= durationNs) {
            enemy.setSlowFactor(1.0);
            
            isExpired = true;
            return;
        }

        enemy.setSlowFactor(slowingFactor);
    }

    @Override
    public boolean isExpired() {
        return isExpired;
    }

    @Override
    public EnchantmentInfo.Type getEnchantmentType() {
        return EnchantmentInfo.Type.ICE;
    }

    @Override
    public List<TowerStat> getTowerStats() {
        return List.of(new TowerStat(TowerStatType.SLOW_EFFECT, slowingFactor),
                new TowerStat(TowerStatType.EFFECT_DURATION, duration)
        );
    }

    @Override
    public EnemyAnimation.EnemyAppearance getEnemyAppearance() {
        return EnemyAnimation.EnemyAppearance.FREEZING;
    }
}
