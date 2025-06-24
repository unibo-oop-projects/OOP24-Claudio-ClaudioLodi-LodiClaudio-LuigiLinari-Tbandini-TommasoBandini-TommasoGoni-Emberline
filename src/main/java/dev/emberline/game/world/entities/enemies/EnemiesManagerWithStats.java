package dev.emberline.game.world.entities.enemies;

import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.game.world.statistics.Statistics;
import dev.emberline.utility.Vector2D;

import java.util.List;

/**
 * Decorator for the basic enemymanager class, used for statistics.
 */
public class EnemiesManagerWithStats implements IEnemiesManager {

    private final EnemiesManager enemiesManager;
    private final Statistics statistics;
    private int deadEnemies;

    public EnemiesManagerWithStats(final World world) {
        enemiesManager = new EnemiesManager(world);
        statistics = world.getStatistics();
    }

    @Override
    public void addEnemy(final Vector2D spawnPoint, final EnemyType type) {
        enemiesManager.addEnemy(spawnPoint, type);
    }

    @Override
    public List<IEnemy> getNear(final Vector2D location, final double radius) {
        return enemiesManager.getNear(location, radius);
    }

    @Override
    public boolean areAllDead() {
        return enemiesManager.areAllDead();
    }

    @Override
    public void render() {
        enemiesManager.render();
    }

    /**
     * Updates the basic enemymanager and keeps track of the dead enemy's number.
     *
     * @param elapsed
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
