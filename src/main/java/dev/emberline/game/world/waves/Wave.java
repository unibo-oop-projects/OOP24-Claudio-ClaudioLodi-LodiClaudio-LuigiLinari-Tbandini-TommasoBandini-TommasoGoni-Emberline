package dev.emberline.game.world.waves;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.render.Zoom;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.game.world.roads.Roads;
import dev.emberline.game.world.spawnpoints.Spawnpoints;
import dev.emberline.utility.Pair;
import dev.emberline.utility.Vector2D;

/**
 * The Wave class contains all the elements that characterize a single wave
 */
public class Wave implements Updatable, Renderable {
    
    private final World world;
    private final Roads roads;
    private final Spawnpoints spawnpoints;
    private final Zoom csManager;

    private long acc = 0;

    /**
     * @param world is the reference to the World
     * @param wavePath represents the path of the files regarding the current wave
     */
    public Wave(World world, String wavePath) {
        this.world = world;
        roads = new Roads(wavePath);
        spawnpoints = new Spawnpoints(wavePath);
        csManager = new Zoom(wavePath);
    }

    /**
     * This method is supposed to be used by entities to find their path in the map.
     * @param pos is the current position of the entity
     * @return next node to go to
     */
    public Optional<Vector2D> getNext(Vector2D pos) {
        return roads.getNextNode(pos);
    }

    /**
     * @return true if the wave is over
     */
    public boolean isOver() {
        //return world.getEnemiesManager().areAllDead() && spawnpoints.hasMoreToSpawn();
        return false;
    }

    /*
    private void sendEnemies() {
        List<Pair<Vector2D, EnemyType>> enemiesQueue = spawnpoints.getEnemies(acc);
        for (var enemy : enemiesQueue) {
            world.getEnemiesManager().addEnemy(enemy.getX(), enemy.getY());
        }
    }
    */

    /**
     * Updates the CoordinateSystem and sends to the EnemyManager the new enemies to spawn,
     * at the current time @param elapsed
     */
    @Override
    public void update(long elapsed) {
        acc += elapsed;

        //sendEnemies();

        csManager.update(elapsed);

        roads.update(elapsed);
    }

    @Override
    public void render() {
        roads.render();
    }
}