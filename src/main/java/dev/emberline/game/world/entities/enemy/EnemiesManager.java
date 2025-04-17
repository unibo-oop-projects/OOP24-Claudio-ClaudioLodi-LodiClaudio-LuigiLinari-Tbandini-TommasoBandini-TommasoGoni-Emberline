package dev.emberline.game.world.entities.enemy;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;
import javafx.geometry.Point2D;

public class EnemiesManager implements Updatable, Renderable {

    // TODO data structure: efficient querying based on area
    private final List<Enemy> enemies = new LinkedList<>();
    private final World world;

    public EnemiesManager(World world) {
        this.world = world;
    }

    public void addEnemy(Point2D spawnPoint) {
        enemies.add(new Enemy(spawnPoint, world));
    }
    
    public List<Enemy> getEnemiesInArea(/* Area */) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(long elapsed) {
        for (final Enemy enemy : enemies) {
            enemy.update(elapsed);
        }
    }

    @Override
    public void render() {
        for (final Enemy enemy : enemies) {
            enemy.render();
        }
    }
}
