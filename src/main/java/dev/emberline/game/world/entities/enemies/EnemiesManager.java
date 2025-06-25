package dev.emberline.game.world.entities.enemies;

import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.game.world.entities.enemies.enemy.EnemyWithStats;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Vector2D;

import java.util.LinkedList;
import java.util.List;

public class EnemiesManager implements IEnemiesManager {

    private final EnemiesFactory enemiesFactory = new EnemiesFactory();

    private final SpatialHashGrid spatialHashGrid;

    private final World world;

    public EnemiesManager(final World world) {
        this.world = world;

        //TODO
        this.spatialHashGrid = new SpatialHashGrid(
                0, 0,
                32, 18
        );
    }

    @Override
    public void addEnemy(final Vector2D spawnPoint, final EnemyType type) {
        final IEnemy newEnemy = enemiesFactory.createEnemy(spawnPoint, type, world);
        final IEnemy newEnemyWrapper = new EnemyWithStats(newEnemy, world.getStatistics());
        spatialHashGrid.add(newEnemyWrapper);
    }

    @Override
    public List<IEnemy> getNear(final Vector2D location, final double radius) {
        final List<IEnemy> near = spatialHashGrid.getNear(location, radius);
        near.removeIf(iEnemy -> !iEnemy.isHittable());
        return near;
    }

    @Override
    public boolean areAllDead() {
        return spatialHashGrid.size() == 0;
    }

    int getAliveEnemiesNumber() {
        return spatialHashGrid.size();
    }

    @Override
    public void update(final long elapsed) {
        final List<IEnemy> toUpdate = new LinkedList<>();
        final List<IEnemy> toRemove = new LinkedList<>();
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
