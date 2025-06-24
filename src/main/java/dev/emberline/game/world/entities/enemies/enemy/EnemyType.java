package dev.emberline.game.world.entities.enemies.enemy;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Enum representing the type of enemies in the game.
 */
public enum EnemyType {
    PIG, OGRE;

    @JsonCreator
    public static EnemyType fromString(final String enemyType) {
        return EnemyType.valueOf(enemyType.toUpperCase());
    }
}
