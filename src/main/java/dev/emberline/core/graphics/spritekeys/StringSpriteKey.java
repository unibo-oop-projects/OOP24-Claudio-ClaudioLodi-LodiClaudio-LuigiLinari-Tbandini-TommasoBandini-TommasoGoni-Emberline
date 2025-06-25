package dev.emberline.core.graphics.spritekeys;

/**
 * Represents a unique key used for identifying and retrieving sprites
 * based on a string identifier.
 * <p>
 * The string field serves as the primary identifier for the key. Using
 * a record ensures that the {@code hashCode()} and {@code equals(Object)} methods
 * are correctly implemented, making it suitable as a key in a hashing mechanism.
 *
 * @param string The string identifier for the sprite.
 */
public record StringSpriteKey(String string) implements SpriteKey {
}
