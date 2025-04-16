package dev.emberline.game.world.entities.enemy;

import java.util.LinkedList;
import java.util.List;

import dev.emberline.game.world.World;
import javafx.geometry.Point2D;

public class EnemiesManager {

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

    public void updateEnemies(long elapsed) {
        for (final Enemy enemy : enemies) {
            enemy.update(elapsed);
        }
    }

    public void renderEnemies() {
        for (final Enemy enemy : enemies) {
            enemy.render();
        }
    }
}
