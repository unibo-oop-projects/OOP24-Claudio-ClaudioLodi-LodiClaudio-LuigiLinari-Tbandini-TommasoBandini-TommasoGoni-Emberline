package dev.emberline.game.world.entities.enemies;

import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.game.world.entities.enemies.enemy.concrete.Ogre;
import dev.emberline.game.world.entities.enemies.enemy.concrete.Pig;
import dev.emberline.utility.Vector2D;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class EnemiesFactory {

    private static final Map<EnemyType, EnemyCreator> CREATOR_REGISTRY = new EnumMap<>(EnemyType.class);

    @FunctionalInterface
    private interface EnemyCreator {
        IEnemy createEnemy(Vector2D spawnPoint, World world);
    }

    static {
        CREATOR_REGISTRY.put(EnemyType.PIG, Pig::new);
        CREATOR_REGISTRY.put(EnemyType.OGRE, Ogre::new);
    }

    public IEnemy createEnemy(final Vector2D spawnPoint, final EnemyType type, final World world) {
        if (!CREATOR_REGISTRY.containsKey(type)) {
            throw new IllegalArgumentException("Type " + type + " isn't present in the creator registry");
        }

        return CREATOR_REGISTRY.get(type).createEnemy(spawnPoint, world);
    }
}
