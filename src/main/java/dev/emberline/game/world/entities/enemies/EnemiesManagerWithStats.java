package dev.emberline.game.world.entities.enemies;

import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.game.world.statistics.Statistics;
import dev.emberline.utility.Vector2D;

import java.util.List;

public class EnemiesManagerWithStats implements IEnemiesManager {

    private final EnemiesManager enemiesManager;
    private final Statistics statistics;
    private int totEnemies = 0;
    private int deadEnemies = 0;

    public EnemiesManagerWithStats(World world) {
        enemiesManager = new EnemiesManager(world);
        statistics = world.getStatistics();
    }

    @Override
    public void addEnemy(Vector2D spawnPoint, EnemyType type) {
        enemiesManager.addEnemy(spawnPoint, type);
    }

    @Override
    public List<IEnemy> getNear(Vector2D location, double radius) {
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

    @Override
    public void update(long elapsed) {
        int alivePreUpdate = enemiesManager.getAliveEnemiesNumber();

        enemiesManager.update(elapsed);

        int alivePostUpdate = enemiesManager.getAliveEnemiesNumber();
        deadEnemies += alivePreUpdate - alivePostUpdate;
        statistics.updateEnemiesKilled(deadEnemies);
    }
}
