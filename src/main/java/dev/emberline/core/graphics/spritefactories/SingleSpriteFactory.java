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

public class SingleSpriteFactory implements SpriteFactory<SingleSpriteKey> {
    private static class SpriteMetadata {
        @JsonProperty("filename")
        private String filename;
        @JsonProperty("x")
        private int x;
        @JsonProperty("y")
        private int y;
        @JsonProperty("width")
        private int width;
        @JsonProperty("height")
        private int height;
    }
    private static final JsonNode configsRoot = ConfigLoader.loadNode("/sprites/singleSprites.json");

    @Override
    public Sprite loadSprite(SingleSpriteKey uiSpriteKey) {
        JsonNode currentNode = configsRoot.get(uiSpriteKey.name());
        SpriteMetadata spriteMetadata = ConfigLoader.loadConfig(currentNode, SpriteMetadata.class);
        Image spriteAtlas = new Image(Objects.requireNonNull(SingleSpriteFactory.class.getResourceAsStream(spriteMetadata.filename)));
        return new SingleSprite(new WritableImage(spriteAtlas.getPixelReader(), spriteMetadata.x, spriteMetadata.y, spriteMetadata.width, spriteMetadata.height));
    }

    @Override
    public Class<SingleSpriteKey> getKeyType() {
        return SingleSpriteKey.class;
    }
}
