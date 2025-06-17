package dev.emberline.game.world.entities.enemies;

import java.util.HashMap;
import java.util.Map;

import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.game.world.entities.enemies.enemy.concrete.Ogre;
import dev.emberline.game.world.entities.enemies.enemy.concrete.Pig;
import dev.emberline.utility.Vector2D;

public class EnemiesFactory {
    
    @FunctionalInterface
    private interface EnemyCreator {
        IEnemy createEnemy(Vector2D spawnPoint, World world);
    }

    private final Map<EnemyType, EnemyCreator> creatorRegistry = new HashMap<>();

    {
        creatorRegistry.put(EnemyType.PIG, (spawnPoint, world) -> new Pig(spawnPoint, world));
        creatorRegistry.put(EnemyType.OGRE, (spawnPoint, world) -> new Ogre(spawnPoint, world));
    }

    public IEnemy createEnemy(Vector2D spawnPoint, EnemyType type, World world) {
        if (!creatorRegistry.containsKey(type)) {
            throw new IllegalArgumentException("Type " + type + " isn't present in the creator registry");
        }
        
        return creatorRegistry.get(type).createEnemy(spawnPoint, world);
    }
}
