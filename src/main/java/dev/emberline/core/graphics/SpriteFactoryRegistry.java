package dev.emberline.core.graphics;

import dev.emberline.core.graphics.spritefactories.*;
import dev.emberline.core.graphics.spritekeys.SpriteKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SpriteFactoryRegistry {
    private final static List<SpriteFactory<?>> FACTORIES = Collections.synchronizedList(new ArrayList<>());

    private SpriteFactoryRegistry() {}

    static <K extends SpriteKey> void registerFactory(final SpriteFactory<K> factory) {
        FACTORIES.add(factory);
    }

    static <K extends SpriteKey> SpriteFactory<K> getFactory(final K key) {
        final Class<?> keyType = key.getClass(); //todo check uf this is correct

        for (final SpriteFactory<?> factory : FACTORIES) {
            if (factory.getKeyType().isAssignableFrom(keyType)) {
                // This cast is safe because we check the key type against the factory's key type,
                // and we know that the factory is registered for this key type.
                @SuppressWarnings("unchecked") final SpriteFactory<K> result = (SpriteFactory<K>) factory;
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
        registerFactory(new MapSpriteFactory());
    }
}
