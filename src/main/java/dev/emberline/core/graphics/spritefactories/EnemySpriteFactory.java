package dev.emberline.core.graphics.spritefactories;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys.EnemySpriteKey;
import dev.emberline.game.world.entities.enemies.enemy.AbstractEnemy.FacingDirection;
import dev.emberline.game.world.entities.enemies.enemy.EnemyAnimation.EnemyAppearance;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * A factory class for creating animated enemy sprites based on a provided {@link EnemySpriteKey}.
 * The enemy sprite atlas is a single image containing all sprite frames,
 * and the individual frames are extracted during sprite creation based on metadata values.
 */
public final class EnemySpriteFactory implements SpriteFactory<EnemySpriteKey> {
    private static class Metadata {
        @JsonProperty
        int width;
        @JsonProperty
        int height;
        @JsonProperty
        int frames;
        @JsonProperty
        int frameTimeNs;
        @JsonProperty
        Map<FacingDirection, Integer> direction;
        @JsonProperty
        Map<EnemyAppearance, Integer> state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sprite loadSprite(final EnemySpriteKey key) {
        final EnemyType type = key.type();
        final FacingDirection direction = key.direction();
        final EnemyAppearance state = key.state();

        final String jsonPath = String.format("/sprites/enemyAssets/%s.json", type.name().toLowerCase(Locale.US));
        final Metadata metadata = ConfigLoader.loadConfig(jsonPath, Metadata.class);

        final int xOffset = metadata.direction.get(direction);
        final int yOffset = metadata.state.get(state);

        final String enemyAtlasPath = String.format("/sprites/enemyAssets/%sAtlas.png", type.name().toLowerCase(Locale.US));
        final Image enemyAtals = getEnemyAtlas(enemyAtlasPath);

        final Image[] frames = new Image[metadata.frames];
        for (int i = 0; i < metadata.frames; ++i) {
            final int frameStep = metadata.width * metadata.direction.size();
            final int x = xOffset + i * frameStep;
            final int y = yOffset;
            frames[i] = new WritableImage(enemyAtals.getPixelReader(), x, y, metadata.width, metadata.height);
        }

        return new AnimatedSprite(frames, metadata.frameTimeNs);
    }

    private static Image getEnemyAtlas(final String enemyAtlasPath) {
        return new Image(Objects.requireNonNull(EnemySpriteFactory.class.getResourceAsStream(enemyAtlasPath)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EnemySpriteKey> getKeyType() {
        return EnemySpriteKey.class;
    }
}
