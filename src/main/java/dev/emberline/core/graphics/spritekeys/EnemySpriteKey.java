package dev.emberline.core.graphics.spritekeys;

import dev.emberline.game.world.entities.enemies.enemy.EnemyType;

public record EnemySpriteKey(EnemyType type, FacingDirection direction, EnemyAppearance state) implements SpriteKey {}