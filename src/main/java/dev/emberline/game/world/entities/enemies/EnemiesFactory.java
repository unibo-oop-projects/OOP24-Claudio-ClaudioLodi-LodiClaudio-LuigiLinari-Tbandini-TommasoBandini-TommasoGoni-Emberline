package dev.emberline.game.world.entities.enemies;

import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.game.world.entities.enemies.enemy.concrete.Ogre;
import dev.emberline.game.world.entities.enemies.enemy.concrete.Pig;
import dev.emberline.utility.Vector2D;

import java.util.HashMap;
import java.util.Map;

public class EnemiesFactory {

    @FunctionalInterface
    private interface EnemyCreator {
        IEnemy createEnemy(Vector2D spawnPoint, World world);
    }

    private static final Map<EnemyType, EnemyCreator> creatorRegistry = new HashMap<>();

    static {
        creatorRegistry.put(EnemyType.PIG, Pig::new);
        creatorRegistry.put(EnemyType.OGRE, Ogre::new);
    }

    public IEnemy createEnemy(Vector2D spawnPoint, EnemyType type, World world) {
        if (!creatorRegistry.containsKey(type)) {
            throw new IllegalArgumentException("Type " + type + " isn't present in the creator registry");
        }

        return creatorRegistry.get(type).createEnemy(spawnPoint, world);
    }
}
