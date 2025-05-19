package dev.emberline.game.world.waves;

import java.io.File;
import java.util.List;
import java.util.Optional;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.world.World;
import dev.emberline.game.world.roads.Roads;
import dev.emberline.game.world.spawnpoints.Spawnpoints;
import javafx.scene.canvas.GraphicsContext;
import utility.Coordinate2D;
import utility.IntegerPoint2D;
import utility.Vector2D;

public class Wave implements Updatable, Renderable {
    
    private World world;
    //getting the file path should be done with methods such as
    //getResources() or similar
    private Roads roads;
    private Spawnpoints spawnpoints;
    private CoordinateSystemManager csManager;

    private double csIncrement = 0;
    private long acc = 0;

    public Wave(World world, String wavePath) {
        this.world = world;
        roads = new Roads(wavePath);
        spawnpoints = new Spawnpoints(wavePath);
        csManager = new CoordinateSystemManager(wavePath);
    }

    public Optional<IntegerPoint2D> getNext(IntegerPoint2D pos) {
        return roads.getNextNode(pos);
    }

    public boolean isOver() {
        return world.getEnemiesManager().areAllDead() && spawnpoints.hasMoreToSpawn();
    }

    private void sendEnemies() {
        List<IntegerPoint2D> enemiesQueue = spawnpoints.getEnemies(acc);
        for (var enemy : enemiesQueue) {
            Vector2D p2 = new Coordinate2D(enemy.getX(), enemy.getY());
            world.getEnemiesManager().addEnemy(p2);
        }
    }

    @Override
    public void update(long elapsed) {
        acc += elapsed;

        sendEnemies();

        roads.update(elapsed);
    }

    @Override
    public void render() {
        roads.render();
    }
}