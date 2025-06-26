package dev.emberline.game.world.waves;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;
import dev.emberline.game.world.graphics.Fog;
import dev.emberline.game.world.graphics.Zoom;
import dev.emberline.game.world.roads.Roads;
import dev.emberline.game.world.spawnpoints.Spawnpoints;
import dev.emberline.game.world.spawnpoints.Spawnpoints.EnemyToSpawn;
import dev.emberline.utility.Vector2D;

import java.util.List;
import java.util.Optional;

/**
 * The Wave class contains all the elements that characterize a single wave
 */
public class Wave implements Updatable, Renderable {

    private final World world;
    private final Roads roads;
    private final Spawnpoints spawnpoints;
    private final Zoom zoom;
    private final Fog fog;
    private boolean firstRender;

    // In nanoseconds, keeps track of the total time elapsed from the start of the wave
    private long accumulatorNs;

    /**
     * Creates a new {@code Wave} object with the provided {@link World} and the waveDirectoryPath.
     * @param world             the world in which the wave is being played
     * @param waveDirectoryPath the path of the directory containing the wave files
     * @see Wave
     */
    public Wave(final World world, final String waveDirectoryPath) {
        this.world = world;
        this.roads = new Roads(waveDirectoryPath);
        this.spawnpoints = new Spawnpoints(waveDirectoryPath);
        this.zoom = new Zoom(waveDirectoryPath);
        this.fog = new Fog(waveDirectoryPath);
    }

    /**
     * This method is supposed to be used by entities to find their path in the map.
     *
     * @param pos is the current position of the entity
     * @return next node to go to
     */
    public Optional<Vector2D> getNext(final Vector2D pos) {
        return roads.getNextNode(pos);
    }

    /**
     * Returns true if the wave is over
     * @return true if the wave is over
     */
    public boolean isOver() {
        return world.getEnemiesManager().areAllDead() && !spawnpoints.hasMoreEnemiesToSpawn();
    }

    private void sendEnemies() {
        final List<EnemyToSpawn> enemiesToSpawn = spawnpoints.retrieveEnemiesToSpawnNanoseconds(accumulatorNs);

        for (final EnemyToSpawn enemyToSpawn : enemiesToSpawn) {
            world.getEnemiesManager().addEnemy(
                    enemyToSpawn.spawnLocation(),
                    enemyToSpawn.enemyType()
            );
        }
    }

    /**
     * Updates the CoordinateSystem and sends to the EnemyManager the new enemies to spawn,
     * at the current time @param elapsed
     */
    @Override
    public void update(final long elapsed) {
        accumulatorNs += elapsed;
        sendEnemies();
    }

    /**
     * Renders the fog and zoom
     */
    @Override
    public void render() {
        if (!firstRender) {
            startWaveAnimations();
            firstRender = true;
        }
        fog.render();
        if (!zoom.isOver()) {
            zoom.render();
        }
    }

    private void startWaveAnimations() {
        zoom.startAnimation();
        fog.startAnimation();
    }
}
