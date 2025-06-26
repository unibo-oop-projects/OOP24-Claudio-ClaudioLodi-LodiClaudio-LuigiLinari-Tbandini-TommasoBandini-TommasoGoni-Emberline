package dev.emberline.core.graphics.spritekeys;

import dev.emberline.game.model.EnchantmentInfo;

/**
 * Represents a unique key used for caching and retrieving sprites specifically
 * related to tower crystals given an enchantment type.
 * <p>
 * The key uses the {@link EnchantmentInfo.Type} to identify the type of enchantment,
 * enabling the caching system to store and access sprites associated with different
 * enchantment types effectively.
 *
 * @param type The type of enchantment associated with the sprite.
 */
public record CrystalSpriteKey(EnchantmentInfo.Type type) implements SpriteKey {
}
