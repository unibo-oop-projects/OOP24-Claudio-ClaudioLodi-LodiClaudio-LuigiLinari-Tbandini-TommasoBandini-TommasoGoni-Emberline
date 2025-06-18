package dev.emberline.game.model.effects;

import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.world.entities.enemies.enemy.EnemyAnimation;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.gui.towerdialog.stats.TowerStat;
import dev.emberline.gui.towerdialog.stats.TowerStat.TowerStatType;

import java.util.List;

/**
 * Represents a burn effect that deals damage over time.
 * The effect is associated with fire enchantments in the game.
 *
 * @param duration The total duration in seconds over which the burn effect persists.
 * @param damagePerSecond The amount of damage in hp dealt to the affected entity each second.
 *
 * @see dev.emberline.game.model.EnchantmentInfo.Type#FIRE
 */
public class BurnEffect implements EnchantmentEffect {
    
    private final double damagePerSecond;
    private final double damagePerNs;

    private final double duration;
    private final long durationNs;
    private long totalElapsed = 0;

    private boolean isExpired = false;

    public BurnEffect(double damagePerSecond, double duration) {
        this.damagePerSecond = damagePerSecond;
        this.damagePerNs = (damagePerSecond / 1_000_000_000);

        this.duration = duration;
        this.durationNs = (long) (duration * 1_000_000_000);
    }

    @Override
    public void updateEffect(IEnemy enemy, long elapsed) {
        if (totalElapsed + elapsed >= durationNs) {
            long spareTime = durationNs - totalElapsed;
            enemy.dealDamage(damagePerNs * spareTime);

            totalElapsed = durationNs;
            isExpired = true;
            return;
        }

        totalElapsed += elapsed;
        enemy.dealDamage(damagePerNs * elapsed);
    }

    @Override
    public boolean isExpired() {
        return isExpired;
    }

    @Override
    public EnchantmentInfo.Type getEnchantmentType() {
        return EnchantmentInfo.Type.FIRE;
    }

    @Override
    public List<TowerStat> getTowerStats() {
        return List.of(new TowerStat(TowerStatType.BURN_EFFECT, damagePerSecond),
                new TowerStat(TowerStatType.EFFECT_DURATION, duration)
        );
    }

    @Override
    public EnemyAnimation.EnemyAppearance getEnemyAppearance() {
        return EnemyAnimation.EnemyAppearance.BURNING;
    }
}
