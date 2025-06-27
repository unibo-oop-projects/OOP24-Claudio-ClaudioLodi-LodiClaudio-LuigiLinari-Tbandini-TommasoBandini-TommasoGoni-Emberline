package dev.emberline.game.world.entities.enemies;

import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.game.world.statistics.Statistics;
import dev.emberline.utility.Vector2D;

import java.util.List;

/**
 * The EnemiesManagerWithStats class is a decorator for the EnemiesManager that adds the ability to track and update
 * statistical data related to enemies in the game.
 */
public class EnemiesManagerWithStats implements IEnemiesManager {

    private final EnemiesManager enemiesManager;
    private final Statistics statistics;
    private int deadEnemies;

    /**
     * Constructs an {@code EnemiesManagerWithStats} instance wrapping {@code EnemiesManager}.
     *
     * @param world the game world within which the enemies manager operates; it provides the context
     *              and statistics tracker used by this manager
     */
    public EnemiesManagerWithStats(final World world) {
        enemiesManager = new EnemiesManager(world);
        statistics = world.getStatistics();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEnemy(final Vector2D spawnPoint, final EnemyType type) {
        enemiesManager.addEnemy(spawnPoint, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IEnemy> getNear(final Vector2D location, final double radius) {
        return enemiesManager.getNear(location, radius);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean areAllDead() {
        return enemiesManager.areAllDead();
    }

    /**
     * Renders all the enemies inside the {@code EnemiesManagerWithStats}.
     * @see EnemiesManager#render()
     */
    @Override
    public void render() {
        enemiesManager.render();
    }

    /**
     * Updates all the enemies inside the {@code EnemiesManagerWithStats}, and keeps track of the statistics.
     * @see EnemiesManager#update(long)
     */
    @Override
    public void update(final long elapsed) {
        final int alivePreUpdate = enemiesManager.getAliveEnemiesNumber();

        enemiesManager.update(elapsed);

        final int alivePostUpdate = enemiesManager.getAliveEnemiesNumber();
        deadEnemies = alivePreUpdate - alivePostUpdate;
        statistics.updateEnemiesKilled(deadEnemies);
    }
}
