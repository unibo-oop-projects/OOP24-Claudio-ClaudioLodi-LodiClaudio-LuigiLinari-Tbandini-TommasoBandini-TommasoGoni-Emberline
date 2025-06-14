package dev.emberline.core.graphics;

import dev.emberline.core.graphics.spritekeys.SpriteKey;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SpriteLoader {
    private final static Map<SpriteKey, Sprite> SPRITE_CACHE = Collections.synchronizedMap(new HashMap<>());

    public static <K extends SpriteKey> Sprite loadSprite(K spriteKey) {
        return SPRITE_CACHE.computeIfAbsent(spriteKey, currKey -> SpriteFactoryRegistry.getFactory(currKey).loadSprite(currKey));
    }
}
