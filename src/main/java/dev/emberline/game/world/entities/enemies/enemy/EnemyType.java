package dev.emberline.game.world.entities.enemies.enemy;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.io.Serializable;
import java.util.Locale;

/**
 * Enum representing the type of enemies in the game.
 */
public enum EnemyType implements Serializable {
    /**
     * Represents a pig type enemy in the game
     * @see dev.emberline.game.world.entities.enemies.enemy.concrete.Pig
     */
    PIG,
    /**
     * Represents the OGRE type of enemy in the game.
     * @see dev.emberline.game.world.entities.enemies.enemy.concrete.Ogre
     */
    OGRE;

    /**
     * Converts a string representation of an enemy type into its corresponding {@code EnemyType} enum value.
     * The input string is case-insensitive and will be converted to uppercase before matching with the enum values.
     *
     * @param enemyType the string representation of the enemy type
     * @return the corresponding {@code EnemyType} enum value
     * @throws IllegalArgumentException if the provided string does not match any {@code EnemyType} value
     * @throws NullPointerException if the provided string is null
     */
    @JsonCreator
    public static EnemyType fromString(final String enemyType) {
        return EnemyType.valueOf(enemyType.toUpperCase(Locale.US));
    }
}
