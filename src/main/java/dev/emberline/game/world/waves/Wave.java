package dev.emberline.game.world.waves;

import java.util.List;
import java.util.Optional;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;
import dev.emberline.game.world.roads.Roads;
import dev.emberline.game.world.spawnpoints.Spawnpoints;
import utility.Vector2D;

public class Wave implements Updatable, Renderable {
    
    private final World world;
    //getting the file path should be done with methods such as
    //getResources() or similar
    private final Roads roads;
    private final Spawnpoints spawnpoints;

    private long acc = 0;

    public Wave(World world, String wavePath) {
        this.world = world;
        roads = new Roads(wavePath);
        spawnpoints = new Spawnpoints(wavePath);
    }

    public Optional<Vector2D> getNext(Vector2D pos) {
        return roads.getNextNode(pos);
    }

    public boolean isOver() {
        return world.getEnemiesManager().areAllDead() && spawnpoints.hasMoreToSpawn();
    }

    private void sendEnemies() {
        List<Vector2D> enemiesQueue = spawnpoints.getEnemies(acc);
        for (var enemy : enemiesQueue) {
            world.getEnemiesManager().addEnemy(enemy);
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