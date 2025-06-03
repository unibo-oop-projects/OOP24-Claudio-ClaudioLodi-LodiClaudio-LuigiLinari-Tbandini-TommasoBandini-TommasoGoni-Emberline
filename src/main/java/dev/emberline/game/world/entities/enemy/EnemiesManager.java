package dev.emberline.game.world.entities.enemy;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;
import utility.Vector2D;

public class EnemiesManager implements Updatable, Renderable {

    // TODO data structure: efficient querying based on area
    private final List<Enemy> enemies = new LinkedList<>();
    private final World world;
    //vars for statistics
    private int enemiesKilled = 0;

    public EnemiesManager(World world) {
        this.world = world;
    }

    public void addEnemy(Vector2D spawnPoint) {
        enemies.add(new Enemy(spawnPoint, world));
    }
    
    public List<Enemy> getEnemiesInArea(/* Area */) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean areAllDead() {
        return enemies.isEmpty();
    }

    public int getEnemiesKilled() {
        return enemiesKilled;
    }

    @Override
    public void update(long elapsed) {
        Iterator<Enemy> it = enemies.iterator();
        Enemy currEnemy;
        while (it.hasNext()) {
            currEnemy = it.next();

            currEnemy.update(elapsed);
            if (currEnemy.isDead()) {
                it.remove();
                enemiesKilled++;
            }
        }
    }

    @Override
    public void render() {
        for (final Enemy enemy : enemies) {
            enemy.render();
        }
    }
}
