package dev.emberline.game.world.entities.enemies;

import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.game.world.entities.enemies.enemy.concrete.Ogre;
import dev.emberline.game.world.entities.enemies.enemy.concrete.Pig;
import dev.emberline.utility.Vector2D;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory class responsible for creating instances of enemies within the game world.
 * It uses a registry system to associate different types of enemies with their respective creation logic.
 * Enemies are created by providing their spawn position, type, and the game world.
 * The factory ensures that only registered enemy types can be instantiated, throwing an exception otherwise.
 */
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

    /**
     * Creates an enemy instance of the specified type at the given spawn point within the game world.
     * If the requested enemy type is not registered, an {@code IllegalArgumentException} is thrown.
     *
     * @param spawnPoint the position in the game world where the enemy should spawn
     * @param type the type of enemy to be created
     * @param world the game world in which the enemy will exist
     * @return an instance of {@code IEnemy} corresponding to the specified type and spawn point
     * @throws IllegalArgumentException if the specified {@code type} is not found in the creator registry
     */
    public IEnemy createEnemy(final Vector2D spawnPoint, final EnemyType type, final World world) {
        if (!creatorRegistry.containsKey(type)) {
            throw new IllegalArgumentException("Type " + type + " isn't present in the creator registry");
        }

        return creatorRegistry.get(type).createEnemy(spawnPoint, world);
    }
}
