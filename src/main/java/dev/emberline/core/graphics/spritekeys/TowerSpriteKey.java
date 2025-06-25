package dev.emberline.core.graphics.spritekeys;

import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;

/**
 * Represents a key used to identify tower sprites.
 * The key is defined by a combination of the projectile size (represented by {@link ProjectileInfo.Type})
 * and the enchantment type (represented by {@link EnchantmentInfo.Type}).
 * <p>
 * Using a record ensures that the {@code hashCode()} and {@code equals(Object)} methods
 * are correctly implemented, making it suitable as a key in a hashing mechanism.
 *
 * @param size   the type of the projectile associated with the tower sprite.
 * @param enchant the type of enchantment associated with the tower sprite.
 */
public record TowerSpriteKey(ProjectileInfo.Type size, EnchantmentInfo.Type enchant) implements SpriteKey {
}