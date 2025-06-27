package dev.emberline.core.graphics.spritefactories;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.graphics.SingleSprite;
import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Objects;

/**
 * A factory responsible for loading and creating instances of {@link SingleSprite} using
 * a {@link SingleSpriteKey} as an identifier. This implementation reads sprite metadata
 * from a pre-configured JSON resource file and uses that data to construct the sprite
 * based on its defined properties.
 */
public final class SingleSpriteFactory implements SpriteFactory<SingleSpriteKey> {

    private static final JsonNode CONFIGS_ROOT = ConfigLoader.loadNode("/sprites/singleSprites.json");

    private static class SpriteMetadata {
        @JsonProperty
        String filename;
        @JsonProperty
        int x;
        @JsonProperty
        int y;
        @JsonProperty
        int width;
        @JsonProperty
        int height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sprite loadSprite(final SingleSpriteKey uiSpriteKey) {
        final JsonNode currentNode = CONFIGS_ROOT.get(uiSpriteKey.name());
        final SpriteMetadata spriteMetadata = ConfigLoader.loadConfig(currentNode, SpriteMetadata.class);
        final Image spriteAtlas = new Image(Objects.requireNonNull(SingleSpriteFactory.class.getResourceAsStream(spriteMetadata.filename)));
        return new SingleSprite(new WritableImage(spriteAtlas.getPixelReader(), spriteMetadata.x, spriteMetadata.y, spriteMetadata.width, spriteMetadata.height));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<SingleSpriteKey> getKeyType() {
        return SingleSpriteKey.class;
    }
}
