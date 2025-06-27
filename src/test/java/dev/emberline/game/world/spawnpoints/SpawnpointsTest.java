package dev.emberline.game.world.spawnpoints;

import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class SpawnpointsTest {

    private static final String SPAWNPOINTS_PATH = "/spawnpoints/";
    private final Spawnpoints spawnpoints = new Spawnpoints(SPAWNPOINTS_PATH);

    private final Vector2D spawnPoint1 = new Coordinate2D(8.5, 15.5);
    private final Vector2D spawnPoint2 = new Coordinate2D(29.5, 11.5);

    @Test
    void testRetrieveEnemiesTimingAndTypeUntilEmptyList() {
        List<Spawnpoints.EnemyToSpawn> retrieved;
        List<Spawnpoints.EnemyToSpawn> expected;

        long spawnTime = 0;
        retrieved = spawnpoints.retrieveEnemiesToSpawnNanoseconds(spawnTime);
        expected = List.of(
                new Spawnpoints.EnemyToSpawn(spawnTime, spawnPoint1, EnemyType.PIG),
                new Spawnpoints.EnemyToSpawn(spawnTime, spawnPoint2, EnemyType.OGRE)
        );
        Assertions.assertIterableEquals(expected, retrieved);

        final long threeSeconds = 3_000_000_000L;
        spawnTime = threeSeconds;
        retrieved = spawnpoints.retrieveEnemiesToSpawnNanoseconds(spawnTime);
        expected = List.of(
                new Spawnpoints.EnemyToSpawn(spawnTime, spawnPoint1, EnemyType.PIG)
        );
        Assertions.assertIterableEquals(expected, retrieved);
    }
}
