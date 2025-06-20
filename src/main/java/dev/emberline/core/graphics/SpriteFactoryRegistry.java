package dev.emberline.core.graphics;

import dev.emberline.core.graphics.spritefactories.*;
import dev.emberline.core.graphics.spritekeys.SpriteKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SpriteFactoryRegistry {
    private final static List<SpriteFactory<?>> factories = Collections.synchronizedList(new ArrayList<>());

    static <K extends SpriteKey> void registerFactory(SpriteFactory<K> factory) {
        factories.add(factory);
    }

    static <K extends SpriteKey> SpriteFactory<K> getFactory(K key) {
        Class<?> keyType = key.getClass(); //todo check uf this is correct

        for (SpriteFactory<?> factory : factories) {
            if (factory.getKeyType().isAssignableFrom(keyType)) {
                // This cast is safe because we check the key type against the factory's key type,
                // and we know that the factory is registered for this key type.
                @SuppressWarnings("unchecked")
                SpriteFactory<K> result = (SpriteFactory<K>) factory;
                return result;
            }
        }
        throw new IllegalArgumentException("No factory found for key type: " + keyType.getName());
    }

    static {
        registerFactory(new StringSpriteFactory());
        registerFactory(new SingleSpriteFactory());
        registerFactory(new EnemySpriteFactory());
        registerFactory(new ProjectileSpriteFactory());
        registerFactory(new TowerSpriteFactory());
        registerFactory(new CrystalSpriteFactory());
    }
}
