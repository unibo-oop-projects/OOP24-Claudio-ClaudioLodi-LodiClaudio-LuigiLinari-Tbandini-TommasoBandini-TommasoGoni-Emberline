package dev.emberline.core.graphics.spritekeys;

import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;

/**
 * Represents a unique key for identifying and caching projectile sprite assets.
 * The key is composed of a projectile type and an enchantment type, which
 * define the visual representation of a projectile within the game.
 * <p>
 * Using a record ensures that the {@code hashCode()} and {@code equals(Object)} methods
 * are correctly implemented, making it suitable as a key in a hashing mechanism.
 *
 * @param size   The type of the projectile, represented by {@link ProjectileInfo.Type}.
 * @param enchant The type of enchantment applied to the projectile, represented by
 *                {@link EnchantmentInfo.Type}.
 */
public record ProjectileSpriteKey(ProjectileInfo.Type size, EnchantmentInfo.Type enchant) implements SpriteKey {
}
