package dev.emberline.core.graphics.spritekeys;

import dev.emberline.game.world.entities.enemies.enemy.AbstractEnemy.FacingDirection;
import dev.emberline.game.world.entities.enemies.enemy.EnemyAnimation.EnemyAppearance;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;

/**
 * Represents a unique key for efficiently storing and
 * retrieving enemy sprite assets in a game. This key combines an enemy's type,
 * facing direction and appearance to uniquely identify its corresponding
 * sprite.
 * <p>
 * Using a record ensures that the {@code hashCode()} and {@code equals(Object)} methods
 * are correctly implemented, making it suitable as a key in a hashing mechanism.
 *
 * @param type      The type of the enemy, determining its base characteristics.
 * @param direction The direction in which the enemy is facing.
 * @param state     The current visual state or appearance of the enemy.
 */
public record EnemySpriteKey(EnemyType type, FacingDirection direction, EnemyAppearance state) implements SpriteKey {
}