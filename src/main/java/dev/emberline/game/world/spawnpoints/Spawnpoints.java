package dev.emberline.game.world.spawnpoints;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;

/**
 * This class represents spawnpoints as containers of enemies to spawn at a given time
 */
public class Spawnpoints {
    // Single spawnpoint configuration
    private static final String spawnpointConfigFilename = "spawnpoints.json";
    private static class SpawnSequence {
        @JsonProperty long firstSpawnTimeNs;
        @JsonProperty long spawnIntervalNs;
        @JsonProperty EnemyType[] enemies;
    }
    private static class Spawnpoint {
        @JsonProperty("x")
        private double x;
        @JsonProperty("y")
        private double y;
        @JsonProperty("spawnSequences")
        private SpawnSequence[] spawnSequences;
    }
    public record EnemyToSpawn(long spawnTimeNs, Vector2D spawnLocation, EnemyType enemyType) implements Comparable<EnemyToSpawn> {
        @Override
        public int compareTo(EnemyToSpawn enemyToSpawn) {
            return Long.compare(this.spawnTimeNs, enemyToSpawn.spawnTimeNs);
        }
        // Data validation
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

    private final Spawnpoint[] spawnpoints;
    private final Queue<EnemyToSpawn> spawnQueue = new PriorityQueue<>();

    /**
     * @param wavePath the path of the directory containing the wave files
     */
    public Spawnpoints(String wavePath) {
        spawnpoints = ConfigLoader.loadConfig(wavePath + spawnpointConfigFilename, Spawnpoint[].class);
        populateSpawnQueue();
    }

    private void populateSpawnQueue() {
        for (Spawnpoint spawnpoint : spawnpoints) {
            //adding (0.5, 0.5) to use the center of the tile's coordinates.
            Vector2D spawnLocation = new Coordinate2D(spawnpoint.x, spawnpoint.y).add(0.5, 0.5);
            for (SpawnSequence sequence : spawnpoint.spawnSequences) {
                EnemyType[] enemies = sequence.enemies;
                for (int i = 0; i < enemies.length; ++i) {
                    long currentSpawnTimeNs = sequence.firstSpawnTimeNs + i * sequence.spawnIntervalNs;
                    spawnQueue.add(new EnemyToSpawn(currentSpawnTimeNs, spawnLocation, enemies[i]));
                }
            }
        }
    }

    public boolean hasMoreEnemiesToSpawn() {
        return !spawnQueue.isEmpty();
    }

    public List<EnemyToSpawn> retrieveEnemiesToSpawnNanoseconds(long timeNs) {
        List<EnemyToSpawn> result = new ArrayList<>();
        while (!spawnQueue.isEmpty() && spawnQueue.peek().spawnTimeNs <= timeNs) {
            result.add(spawnQueue.poll());
        }
        return result;
    }
}
