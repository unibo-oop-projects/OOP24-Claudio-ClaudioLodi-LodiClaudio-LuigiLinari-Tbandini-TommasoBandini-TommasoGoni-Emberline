package dev.emberline.game.world.entities.enemies.enemy;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EnemyType {
    PIG, OGRE;

    @JsonCreator
    public static EnemyType fromString(String enemyType) {
        return EnemyType.valueOf(enemyType.toUpperCase());
    }
}
