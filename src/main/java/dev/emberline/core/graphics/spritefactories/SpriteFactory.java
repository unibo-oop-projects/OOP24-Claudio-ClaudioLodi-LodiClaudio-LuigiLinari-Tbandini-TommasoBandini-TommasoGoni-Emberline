package dev.emberline.core.graphics.spritefactories;

import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys.SpriteKey;

/**
 * A factory interface for creating {@link Sprite} instances based on a specified key type.
 * <p>
 * This interface allows the creation of different sprite implementations, such as single
 * sprites or animated sprites based on the provided key.
 *
 * @param <K> the type of the key used for creating the sprite. Must extend {@link SpriteKey}.
 */
public interface SpriteFactory<K extends SpriteKey> {
    /**
     * Loads a sprite from memory based on the specified key.
     *
     * @param key the key used to identify and load the desired sprite
     * @return the loaded sprite corresponding to the provided key
     */
    Sprite loadSprite(K key);

    /**
     * Retrieves the class type of the key used by this factory.
     *
     * @return the class object representing the key type parameter K
     */
    Class<K> getKeyType();
}
