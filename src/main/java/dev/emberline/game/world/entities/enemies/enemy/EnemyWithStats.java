package dev.emberline.game.world.entities.enemies.enemy;

import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.statistics.Statistics;
import dev.emberline.utility.Vector2D;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * A decorator class that enriches an {@link IEnemy} instance with additional
 * functionality to keep track of game statistics via a {@link Statistics} object.
 * <p>
 * This class delegates the core behavior to the provided {@link IEnemy} instance while
 * tracking and recording statistical information.
 */
public class EnemyWithStats implements IEnemy, Serializable {

    @Serial
    private static final long serialVersionUID = -1891378884257347492L;

    private final IEnemy enemy;
    private final Statistics statistics;

    /**
     * Constructs an instance of {@link EnemyWithStats}.
     *
     * @param newEnemy the {@link IEnemy} to be decorated and whose behavior is extended
     *                 with statistical tracking functionality
     * @param statistics the {@link Statistics} object used to record and manage game-related data
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "This is intended behavior as this class is a decorator for IEnemy."
    )
    public EnemyWithStats(final IEnemy newEnemy, final Statistics statistics) {
        this.enemy = newEnemy;
        this.statistics = statistics;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getHeight() {
        return enemy.getHeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getWidth() {
        return enemy.getWidth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getHealth() {
        return enemy.getHealth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dealDamage(final double damage) {
        final double damageDealt = Math.min(getHealth(), damage);
        statistics.updateTotalDamage(damageDealt);
        enemy.dealDamage(damage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyEffect(final EnchantmentEffect effect) {
        enemy.applyEffect(effect);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSlowFactor(final double slowFactor) {
        enemy.setSlowFactor(slowFactor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDead() {
        return enemy.isDead();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHittable() {
        return enemy.isHittable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniformMotion> getMotionUntil(final long time) {
        return enemy.getMotionUntil(time);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector2D getPosition() {
        return enemy.getPosition();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getRemainingDistanceToTarget() {
        return enemy.getRemainingDistanceToTarget();
    }

    /**
     * Renders the decorating {@code IEnemy} instance.
     * @see EnemyRenderComponent#render()
     */
    @Override
    public void render() {
        enemy.render();
    }

    /**
     * Updates the decorating {@code IEnemy} instance.
     * @see EnemyUpdateComponent#update(long)
     */
    @Override
    public void update(final long elapsed) {
        enemy.update(elapsed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGoldReward() {
        return enemy.getGoldReward();
    }
}
