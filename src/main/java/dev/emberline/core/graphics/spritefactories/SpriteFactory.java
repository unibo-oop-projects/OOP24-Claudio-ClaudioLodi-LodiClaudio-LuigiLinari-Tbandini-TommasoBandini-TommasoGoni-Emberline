package dev.emberline.core.graphics.spritefactories;

import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys.SpriteKey;

public interface SpriteFactory<K extends SpriteKey> {
    Sprite loadSprite(K key);

    Class<K> getKeyType();
}
