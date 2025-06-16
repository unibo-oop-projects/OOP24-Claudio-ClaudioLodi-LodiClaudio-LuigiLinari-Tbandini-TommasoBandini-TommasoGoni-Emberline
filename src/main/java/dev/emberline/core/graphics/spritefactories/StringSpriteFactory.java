package dev.emberline.core.graphics.spritefactories;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.graphics.SingleSprite;
import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys.StringSpriteKey;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StringSpriteFactory implements SpriteFactory<StringSpriteKey> {
    private static class Metadata {
        @JsonProperty("filename")
        private String filename;
        @JsonProperty("atlasHeight")
        private int atlasHeight;
        @JsonProperty("atlasWidth")
        private int atlasWidth;
        @JsonProperty("rows")
        private int rows;
        @JsonProperty("columns")
        private int columns;
        @JsonProperty("charOrder")
        private String charOrder;
    }
    private final static Metadata metadata = ConfigLoader.loadConfig("/fonts/font.json", Metadata.class);

    private final static Map<Character, Image> CHAR_CACHE = Collections.synchronizedMap(new HashMap<>());

    @Override
    public Sprite loadSprite(StringSpriteKey key) {
        return new SingleSprite(getStringImage(key.string()));
    }

    public static Image getCharImage(Character c) {
        if (c == null) return getCharImage(' '); // If the character is null, return a space character

        final int charWidth = metadata.atlasWidth / metadata.columns; // Width of a character in pixels
        final int charHeight = metadata.atlasHeight / metadata.rows; // Height of a character in pixels

        Image result = CHAR_CACHE.computeIfAbsent(c, key -> {
            Image atlas = getCharAtlas();
            PixelReader atlasReader = atlas.getPixelReader();

            // Locating the character in the atlas
            int charIndex = metadata.charOrder.indexOf(c);
            if (charIndex == -1) {
                return null; // Character not found
            }
            int charX = charIndex % metadata.columns * charWidth; //pixel coordinate of the char inside the atlas
            int charY = charIndex / metadata.columns * charHeight;

            // Truncate left and right columns of only transparent pixels (ignoring the space character)
            if (c == ' ') return new WritableImage(atlasReader, charX, charY, charWidth/2, charHeight);
            int mutableCharWidth = charWidth;

            // Truncate left
            int transparentColumns = -1; // First let's count how many transparent columns are on the left (of the first non-transparent pixel)
            boolean transparent = true;
            for (int x = charX; x < charX + mutableCharWidth && transparent; x++, transparentColumns++) {
                for (int y = charY; y < charY + charHeight; y++) {
                    transparent = !(atlasReader.getColor(x, y).getOpacity() > 0) && transparent;
                }
            }
            if (transparentColumns == mutableCharWidth) return null; // If the whole character is transparent
            charX += transparentColumns;
            mutableCharWidth -= transparentColumns;
            // Truncate right
            transparentColumns = -1; // Now how many transparent columns are on the right (of the last non-transparent pixel)
            transparent = true;
            for (int x = charX + mutableCharWidth - 1; x >= charX && transparent; x--, transparentColumns++) {
                for (int y = charY; y < charY + charHeight; y++) {
                    transparent = !(atlasReader.getColor(x, y).getOpacity() > 0) && transparent;
                }
            }
            if (transparentColumns == mutableCharWidth) return null; // If the whole character is transparent
            mutableCharWidth -= transparentColumns;

            return new WritableImage(atlasReader, charX, charY, mutableCharWidth, charHeight);
        });

        // If the character is not found or is transparent, memoize it as a space character
        if (result == null) CHAR_CACHE.put(c, getCharImage(' '));
        return CHAR_CACHE.get(c);
    }

    private static Image getStringImage(String string) {
        final int charHeight = metadata.atlasHeight / metadata.rows;

        // If the string is null or empty, return a space character
        if (string == null || string.isEmpty()) return getCharImage(' ');
        // Precalculate the width of the string (in pixels)
        int stringWidth = 0;
        for (Character c : string.toCharArray()) {
            stringWidth += (int) getCharImage(c).getWidth() + 1; // +1 for the space between characters
        }
        // Build the image for the string
        WritableImage stringImage = new WritableImage(stringWidth, charHeight);
        PixelWriter writer = stringImage.getPixelWriter();
        int x = 0; // Current x position in the string image
        for (Character c : string.toCharArray()) {
            Image charImage = getCharImage(c);
            int currWidth = (int) charImage.getWidth();
            int currHeight = (int) charImage.getHeight();
            PixelReader charReader = charImage.getPixelReader();
            for (int i = 0; i < currWidth; i++) {
                for (int j = 0; j < currHeight; j++) {
                    writer.setColor(x + i, j, charReader.getColor(i, j));
                }
            }
            x += currWidth + 1; // Add 1 pixel of space between characters
        }

        return stringImage;
    }

    private static Image getCharAtlas() {
        return new Image(Objects.requireNonNull(StringSpriteFactory.class.getResourceAsStream(metadata.filename)));
    }

    @Override
    public Class<StringSpriteKey> getKeyType() {
        return StringSpriteKey.class;
    }
}
