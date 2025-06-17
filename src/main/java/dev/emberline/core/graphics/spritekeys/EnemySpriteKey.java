package dev.emberline.core.graphics.spritekeys;

import dev.emberline.game.world.entities.enemies.enemy.AbstractEnemy;
import dev.emberline.game.world.entities.enemies.enemy.EnemyAnimation;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;

public record EnemySpriteKey(EnemyType type, AbstractEnemy.FacingDirection direction, EnemyAnimation.EnemyAppearance state) implements SpriteKey {}