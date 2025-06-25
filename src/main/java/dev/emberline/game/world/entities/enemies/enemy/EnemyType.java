package dev.emberline.game.world.entities.enemies.enemy;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum EnemyType {
    PIG, OGRE;

    @JsonCreator
    public static EnemyType fromString(final String enemyType) {
        return EnemyType.valueOf(enemyType.toUpperCase(Locale.US));
    }
}
