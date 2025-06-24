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

import java.util.Map;
import java.util.Objects;

public class EnemySpriteFactory implements SpriteFactory<EnemySpriteKey> {
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

    @Override
    public Sprite loadSprite(EnemySpriteKey key) {
        EnemyType type = key.type();
        FacingDirection direction = key.direction();
        EnemyAppearance state = key.state();

        String jsonPath = String.format("/sprites/enemyAssets/%s.json", type.name().toLowerCase());
        Metadata metadata = ConfigLoader.loadConfig(jsonPath, Metadata.class);

        int xOffset = metadata.direction.get(direction);
        int yOffset = metadata.state.get(state);

        String enemyAtlasPath = String.format("/sprites/enemyAssets/%sAtlas.png", type.name().toLowerCase());
        Image enemyAtals = getEnemyAtlas(enemyAtlasPath);

        Image[] frames = new Image[metadata.frames];
        for (int i = 0; i < metadata.frames; ++i) {
            int frameStep = metadata.width * metadata.direction.size();
            int x = xOffset + i * frameStep;
            int y = yOffset;
            frames[i] = new WritableImage(enemyAtals.getPixelReader(), x, y, metadata.width, metadata.height);
        }

        return new AnimatedSprite(frames, metadata.frameTimeNs);
    }

    private static Image getEnemyAtlas(String enemyAtlasPath) {
        return new Image(Objects.requireNonNull(EnemySpriteFactory.class.getResourceAsStream(enemyAtlasPath)));
    }

    @Override
    public Class<EnemySpriteKey> getKeyType() {
        return EnemySpriteKey.class;
    }
}
