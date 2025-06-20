package dev.emberline.game.world.entities.enemies;

import java.util.*;

import dev.emberline.game.world.entities.enemies.enemy.EnemyWithStats;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Vector2D;
import dev.emberline.game.world.World;

public class EnemiesManager implements IEnemiesManager {

    private final EnemiesFactory enemiesFactory = new EnemiesFactory();

    private final SpatialHashGrid spatialHashGrid;

    private final World world;

    public EnemiesManager(World world) {
        this.world = world;

        //TODO
        this.spatialHashGrid = new SpatialHashGrid(
            0, 0,
            32, 18
        );
    }

    public void addEnemy(Vector2D spawnPoint, EnemyType type) {
        IEnemy newEnemy = enemiesFactory.createEnemy(spawnPoint, type, world);
        IEnemy newEnemyWrapper = new EnemyWithStats(newEnemy, world.getStatistics());
        spatialHashGrid.add(newEnemyWrapper);
    }
    
    public List<IEnemy> getNear(Vector2D location, double radius) {
        List<IEnemy> near = spatialHashGrid.getNear(location, radius);
        near.removeIf(iEnemy -> !iEnemy.isHittable());
        return near;
    }

    public boolean areAllDead() {
        return spatialHashGrid.size() == 0;
    }

    int getAliveEnemiesNumber() {
        return spatialHashGrid.size();
    }

    @Override
    public void update(long elapsed) {
        List<IEnemy> toUpdate = new LinkedList<>();
        List<IEnemy> toRemove = new LinkedList<>();
        for (final IEnemy enemy : spatialHashGrid) {
            if (enemy.isDead()) {
                toRemove.add(enemy);
            } else {
                enemy.update(elapsed);
                toUpdate.add(enemy);
            }
        }
        spatialHashGrid.updateAll(toUpdate);
        spatialHashGrid.removeAll(toRemove);
    }

    @Override
    public void render() {
        for (final IEnemy enemy : spatialHashGrid) {
            enemy.render();
        }
    }
}
