package dev.emberline.core.graphics.spritefactories;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.graphics.SingleSprite;
import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys.UISpriteKey;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Objects;

public class UISpriteFactory implements SpriteFactory<UISpriteKey> {
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
    private static final JsonNode configsRoot = ConfigLoader.loadNode("/sprites/ui/ui.json");

    @Override
    public Sprite loadSprite(UISpriteKey uiSpriteKey) {
        JsonNode currentNode = configsRoot.get(uiSpriteKey.name());
        SpriteMetadata spriteMetadata = ConfigLoader.loadConfig(currentNode, SpriteMetadata.class);
        Image spriteAtlas = new Image(Objects.requireNonNull(UISpriteFactory.class.getResourceAsStream(spriteMetadata.filename)));
        return new SingleSprite(new WritableImage(spriteAtlas.getPixelReader(), spriteMetadata.x, spriteMetadata.y, spriteMetadata.width, spriteMetadata.height));
    }

    @Override
    public Class<UISpriteKey> getKeyType() {
        return UISpriteKey.class;
    }
}
