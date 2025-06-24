package dev.emberline.game.world.spawnpoints;

import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.utility.Coordinate2D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class SpawnpointsTest {

    private final String spawnpointsPath = "/spawnpoints/";
    private final Spawnpoints spawnpoints = new Spawnpoints(spawnpointsPath);

    @Test
    void testRetrieveEnemiesTimingAndTypeUntilEmptyList() {
        List<Spawnpoints.EnemyToSpawn> retrieved;
        List<Spawnpoints.EnemyToSpawn> expected;

        long spawnTime = 0;
        retrieved = spawnpoints.retrieveEnemiesToSpawnNanoseconds(spawnTime);
        expected = List.of(
                new Spawnpoints.EnemyToSpawn(spawnTime, new Coordinate2D(8.5, 15.5), EnemyType.PIG),
                new Spawnpoints.EnemyToSpawn(spawnTime, new Coordinate2D(29.5, 11.5), EnemyType.OGRE)
        );
        Assertions.assertIterableEquals(expected, retrieved);

        spawnTime = 3000000000L;
        retrieved = spawnpoints.retrieveEnemiesToSpawnNanoseconds(spawnTime);
        expected = List.of(
                new Spawnpoints.EnemyToSpawn(spawnTime, new Coordinate2D(8.5, 15.5), EnemyType.PIG)
        );
        Assertions.assertIterableEquals(expected, retrieved);
    }
}