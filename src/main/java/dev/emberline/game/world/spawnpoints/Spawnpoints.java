package dev.emberline.game.world.spawnpoints;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.config.ConfigLoader;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * This class represents spawnpoints as containers of enemies to spawn at a given time
 */

/**
 * The {@code Spawnpoints} class is responsible for keeping track of spawnpoints and providing
 * the enemies to spawn given an elapsed time.
 * <p>
 * It loads spawnpoints configuration from a file and creates a scheduled queue of enemies to spawn
 * based on specified timings and locations.
 */
public class Spawnpoints implements Serializable {

    @Serial
    private static final long serialVersionUID = 6410862547692370971L;

    private final Spawnpoint[] rawSpawnpoints;
    private final Queue<EnemyToSpawn> spawnQueue = new PriorityQueue<>();

    private static final String SPAWNPOINT_CONFIG_FILENAME = "spawnpoints.json";

    // Single spawnpoint configuration
    private record SpawnSequence (
        @JsonProperty
        long firstSpawnTimeNs,
        @JsonProperty
        long spawnIntervalNs,
        @JsonProperty
        EnemyType[] enemies
    ) implements Serializable { }

    private record Spawnpoint (
        @JsonProperty
        double x,
        @JsonProperty
        double y,
        @JsonProperty
        SpawnSequence[] spawnSequences
    ) implements Serializable { }

    /**
     * Single enemy identified by these 3 parameters.
     *
     * @param spawnTimeNs the spawn time of the enemy in nanoseconds
     * @param spawnLocation the spawn location of the enemy as a {@link Vector2D}
     * @param enemyType the {@link EnemyType}
     */
    public record EnemyToSpawn(long spawnTimeNs, Vector2D spawnLocation,
                               EnemyType enemyType) implements Comparable<EnemyToSpawn>, Serializable {
        /**
         * The comparison is based on the {@code spawnTimeNs} field of each instance.
         *
         * @param enemyToSpawn the {@code EnemyToSpawn} instance to be compared
         * @return a negative integer, zero, or a positive integer as this {@code EnemyToSpawn}'s {@code spawnTimeNs}
         *         is less than, equal to, or greater than the specified {@code EnemyToSpawn}'s {@code spawnTimeNs}.
         */
        @Override
        public int compareTo(final EnemyToSpawn enemyToSpawn) {
            return Long.compare(this.spawnTimeNs, enemyToSpawn.spawnTimeNs);
        }

        /**
         * Initializes an instance of {@code EnemyToSpawn} and validates that the provided parameters.
         * @throws IllegalArgumentException if {@code spawnTimeNs} is negative, {@code spawnLocation} is null,
         *                                  or {@code enemyType} is null.
         */
        public EnemyToSpawn {
            if (spawnTimeNs < 0) {
                throw new IllegalArgumentException("Spawn time cannot be negative");
            }
            if (spawnLocation == null) {
                throw new IllegalArgumentException("Spawn location cannot be null");
            }
            if (enemyType == null) {
                throw new IllegalArgumentException("Enemy type cannot be null");
            }
        }
    }

    /**
     * Creates an instance of {@code SpawnPoints} based on the configuration file passed as a parameter.
     *
     * @param wavePath the path of the directory containing the wave files
     * @see Spawnpoints
     */
    public Spawnpoints(final String wavePath) {
        rawSpawnpoints = ConfigLoader.loadConfig(wavePath + SPAWNPOINT_CONFIG_FILENAME, Spawnpoint[].class);
        populateSpawnQueue();
    }

    private void populateSpawnQueue() {
        for (final Spawnpoint spawnpoint : rawSpawnpoints) {
            //adding (0.5, 0.5) to use the center of the tile's coordinates.
            final Vector2D spawnLocation = new Coordinate2D(spawnpoint.x, spawnpoint.y).add(0.5, 0.5);
            for (final SpawnSequence sequence : spawnpoint.spawnSequences) {
                final EnemyType[] enemies = sequence.enemies;
                for (int i = 0; i < enemies.length; ++i) {
                    final long currentSpawnTimeNs = sequence.firstSpawnTimeNs + i * sequence.spawnIntervalNs;
                    spawnQueue.add(new EnemyToSpawn(currentSpawnTimeNs, spawnLocation, enemies[i]));
                }
            }
        }
    }

    /**
     * Returns whether there are any more enemies to spawn.
     * @return whether there are any more enemies to spawn.
     */
    public boolean hasMoreEnemiesToSpawn() {
        return !spawnQueue.isEmpty();
    }

    /**
     * Returns the list of enemies to spawn at and before the current time.
     *
     * @param timeNs time in nanoseconds
     * @return list of enemies
     */
    public List<EnemyToSpawn> retrieveEnemiesToSpawnNanoseconds(final long timeNs) {
        final List<EnemyToSpawn> result = new ArrayList<>();
        while (!spawnQueue.isEmpty() && spawnQueue.peek().spawnTimeNs <= timeNs) {
            result.add(spawnQueue.poll());
        }
        return result;
    }
}
