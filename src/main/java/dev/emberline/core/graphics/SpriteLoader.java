package dev.emberline.core.graphics;

import dev.emberline.core.graphics.spritekeys.SpriteKey;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code SpriteLoader} class is responsible for managing the retrieval and caching of {@link Sprite} objects
 * based on their corresponding {@link SpriteKey}.
 * <p>
 * This class uses an internal cache to store loaded sprites, reducing redundant constructions
 * and improving performance. When a sprite is requested, it attempts to retrieve it from
 * the cache. If the sprite is not found, it delegates the loading operation to the appropriate
 * {@link dev.emberline.core.graphics.spritefactories.SpriteFactory} associated with the given key.
 * <p>
 * Since the loading and retrival of sprites is done both on the Game Loop thread and the JavaFX application thread,
 * a synchronizedMap is used for mutual exclusive access to the sprite cache.
 */
public final class SpriteLoader {
    private final static Map<SpriteKey, Sprite> SPRITE_CACHE = Collections.synchronizedMap(new HashMap<>());

    private SpriteLoader() { }

    /**
     * Loads a {@link Sprite} corresponding to the provided {@link SpriteKey}. If the sprite
     * is already cached, it will be retrieved from the cache. Otherwise, it will be
     * created using the appropriate {@link dev.emberline.core.graphics.spritefactories.SpriteFactory}
     * and stored in the cache.
     *
     * @param <K>       the type of the {@link SpriteKey}
     * @param spriteKey the key used to identify and load the sprite
     * @return the {@link Sprite} associated with the given key
     * @throws IllegalArgumentException if no factory is found for the provided key type
     */
    public static <K extends SpriteKey> Sprite loadSprite(final K spriteKey) {
        return SPRITE_CACHE.computeIfAbsent(spriteKey, currKey -> SpriteFactoryRegistry.getFactory(currKey).loadSprite(currKey));
    }

    /**
     * Loads a {@link Sprite} corresponding to the provided {@link SpriteKey}.
     * It will ignore the cache, and it will create the Sprite
     * using the appropriate {@link dev.emberline.core.graphics.spritefactories.SpriteFactory}.
     * This method is supposed to be used only after deserialization.
     *
     * @param <K>       the type of the {@link SpriteKey}
     * @param spriteKey the key used to identify and load the sprite
     * @return the {@link Sprite} associated with the given key
     * @throws IllegalArgumentException if no factory is found for the provided key type
     */
    public static <K extends SpriteKey> Sprite loadSpriteAfterSerialization(final K spriteKey) {
        return SpriteFactoryRegistry.getFactory(spriteKey).loadSprite(spriteKey);
    }
}
