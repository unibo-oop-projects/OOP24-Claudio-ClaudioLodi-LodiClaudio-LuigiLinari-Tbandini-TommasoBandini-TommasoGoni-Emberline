package dev.emberline.core.graphics.spritekeys;

/**
 * Represents a unique key used to identify and cache sprites
 * associated with specific map appearance in a game. The key is based on the
 * wave number, providing a simple and efficient way to manage sprite assets
 * related to different gameplay waves.
 * <p>
 * Using a record ensures that the {@code hashCode()} and {@code equals(Object)} methods
 * are correctly implemented, making it suitable as a key in a hashing mechanism.
 *
 * @param waveNumber The wave number used as the identifier for the sprite.
 */
public record MapSpriteKey(int waveNumber) implements SpriteKey {
}
