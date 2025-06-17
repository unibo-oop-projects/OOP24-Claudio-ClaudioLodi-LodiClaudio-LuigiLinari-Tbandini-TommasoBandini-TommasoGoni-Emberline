package dev.emberline.game.world.entities.enemies;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Vector2D;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;

public class EnemiesManager implements Updatable, Renderable {

    private final EnemiesFactory enemiesFactory = new EnemiesFactory();

    private final List<IEnemy> enemies = new LinkedList<>();
    private final SpatialHashGrid<IEnemy> spatialHashGrid;

    private final World world;

    public EnemiesManager(World world) {
        this.world = world;

        //TODO
        this.spatialHashGrid = new SpatialHashGrid<>(
            0, 0, 
            32, 18
        );
    }

    public void addEnemy(Vector2D spawnPoint, EnemyType type) {
        IEnemy newEnemy = enemiesFactory.createEnemy(spawnPoint, type, world);
        enemies.add(newEnemy);
        spatialHashGrid.add(newEnemy);
    }
    
    public List<IEnemy> getNear(Vector2D location, double radius) {
        return spatialHashGrid.getNear(location, radius);
    }

    public boolean areAllDead() {
        return enemies.isEmpty();
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
