package dev.emberline.game.world.entities.enemies;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import javafx.geometry.Point2D;
import dev.emberline.game.world.entities.enemies.enemy.Enemy;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;
import utility.Coordinate2D;

public class EnemiesManager implements Updatable, Renderable {

    // TODO data structure: efficient querying based on area
    private final List<IEnemy> enemies = new LinkedList<>();
    private final World world;

    public EnemiesManager(World world) {
        this.world = world;
    }

    public void addEnemy(Coordinate2D spawnPoint) {
        enemies.add(new Enemy(spawnPoint, world));
    }
    
    public List<IEnemy> getNear(Point2D location, double radius) {
        // TODO
        return enemies;
        /*throw new UnsupportedOperationException();*/
    }

    @Override
    public void update(long elapsed) {
        Iterator<IEnemy> it = enemies.iterator();
        IEnemy currEnemy;
        while (it.hasNext()) {
            currEnemy = it.next();

            currEnemy.update(elapsed);
            if (currEnemy.isDead()) {
                it.remove();
            }
        }
    }

    @Override
    public void render() {
        for (final IEnemy enemy : enemies) {
            enemy.render();
        }
    }
}
