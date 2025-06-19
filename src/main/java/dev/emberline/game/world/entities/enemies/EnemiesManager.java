package dev.emberline.game.world.entities.enemies;

import java.util.*;

import dev.emberline.game.world.entities.enemies.enemy.EnemyWithStats;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Vector2D;
import dev.emberline.game.world.World;

public class EnemiesManager implements IEnemiesManager {

    private final EnemiesFactory enemiesFactory = new EnemiesFactory();

    // Enemies are sorted by their Y position (top to bottom)
    private final Collection<IEnemy> enemies = new TreeSet<>(
            (e1, e2) -> {
                return Double.compare(e1.getPosition().getY() + e1.getHeight()/2, e2.getPosition().getY() + e2.getHeight()/2);
            }
    );

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
        enemies.add(newEnemyWrapper);
        spatialHashGrid.add(newEnemyWrapper);
    }
    
    public List<IEnemy> getNear(Vector2D location, double radius) {
        return spatialHashGrid.getNear(location, radius);
    }

    public boolean areAllDead() {
        return enemies.isEmpty();
    }

    int getAliveEnemiesNumber() {
        return enemies.size();
    }

    @Override
    public void update(long elapsed) {
        Iterator<IEnemy> enemiesIt = enemies.iterator();
        IEnemy currEnemy;
        while (enemiesIt.hasNext()) {
            currEnemy = enemiesIt.next();
            
            currEnemy.update(elapsed);
            if (currEnemy.isDead()) {
                enemiesIt.remove();
            }
        }

        List<IEnemy> toUpdate = new LinkedList<>();
        List<IEnemy> toRemove = new LinkedList<>();
        for (final IEnemy enemy : spatialHashGrid) {
            if (enemy.isHittable()) {
                toUpdate.add(enemy);
            } else {
                toRemove.add(enemy);
            }
        }
        spatialHashGrid.updateAll(toUpdate);
        spatialHashGrid.removeAll(toRemove);
    }

    @Override
    public void render() {
        for (final IEnemy enemy : enemies) {
            enemy.render();
        }
    }
}
