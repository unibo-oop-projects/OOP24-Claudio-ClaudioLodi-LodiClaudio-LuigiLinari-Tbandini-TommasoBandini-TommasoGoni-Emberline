package dev.emberline.core.graphics.spritekeys;

import dev.emberline.game.world.entities.enemies.enemy.AbstractEnemy.FacingDirection;
import dev.emberline.game.world.entities.enemies.enemy.EnemyAnimation.EnemyAppearance;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;

public record EnemySpriteKey(EnemyType type, FacingDirection direction, EnemyAppearance state) implements SpriteKey {}