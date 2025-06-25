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

/**
 * An implementation of {@link SpriteFactory} that generates sprites from strings.
 * This factory is responsible for creating {@link Sprite} instances containing images
 * of string representations. The images are built using a character atlas and can
 * dynamically remove transparent pixels to optimize sprite display.
 * <p>
 * This class supports loading and caching character images for efficient rendering.
 * <p>
 * If a character image is unavailable, a default space character image is used.
 */
public class StringSpriteFactory implements SpriteFactory<StringSpriteKey> {

    private final static Metadata METADATA = ConfigLoader.loadConfig("/font/font.json", Metadata.class);

    private final static Map<Character, Image> CHAR_CACHE = Collections.synchronizedMap(new HashMap<>());

    private static class Metadata {
        @JsonProperty
        String filename;
        @JsonProperty
        int atlasHeight;
        @JsonProperty
        int atlasWidth;
        @JsonProperty
        int rows;
        @JsonProperty
        int columns;
        @JsonProperty
        String charOrder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sprite loadSprite(final StringSpriteKey key) {
        return new SingleSprite(getStringImage(key.string()));
    }

    /**
     * Generates and returns an image for the specified character by retrieving it from the character atlas and trimming
     * unnecessary transparent pixels. If the character is not found or is transparent, a default image for a space
     * character is returned and cached.
     *
     * @param c the character for which the corresponding image is to be created. If null is provided, the method
     *          will process it as a space character.
     * @return the image representing the specified character with trimmed transparent pixels. Returns an image of a space
     *         character if the input is null, not found, or fully transparent.
     */
    public static Image getCharImage(final Character c) {
        if (c == null) {
            return getCharImage(' '); // If the character is null, return a space character
        }

        final int charWidth = METADATA.atlasWidth / METADATA.columns; // Width of a character in pixels
        final int charHeight = METADATA.atlasHeight / METADATA.rows; // Height of a character in pixels

        final Image result = CHAR_CACHE.computeIfAbsent(c, key -> {
            final Image atlas = getCharAtlas();

            // Locating the character in the atlas
            final int charIndex = METADATA.charOrder.indexOf(c);
            if (charIndex == -1) {
                return null; // Character not found
            }
            int charX = charIndex % METADATA.columns * charWidth; //pixel coordinate of the char inside the atlas
            final int charY = charIndex / METADATA.columns * charHeight;
            final PixelReader atlasReader = atlas.getPixelReader();

            // Truncate left and right columns of only transparent pixels (ignoring the space character)
            if (c == ' ') {
                return new WritableImage(atlasReader, charX, charY, charWidth / 2, charHeight);
            }
            int mutableCharWidth = charWidth;

            // Truncate left
            int transparentColumns = -1; // First let's count how many transparent columns are on the left (of the first non-transparent pixel)
            boolean transparent = true;
            for (int x = charX; x < charX + mutableCharWidth && transparent; x++, transparentColumns++) {
                for (int y = charY; y < charY + charHeight; y++) {
                    transparent = atlasReader.getColor(x, y).getOpacity() <= 0d && transparent;
                }
            }
            if (transparentColumns == mutableCharWidth) {
                return null; // If the whole character is transparent
            }
            charX += transparentColumns;
            mutableCharWidth -= transparentColumns;
            // Truncate right
            transparentColumns = -1; // Now how many transparent columns are on the right (of the last non-transparent pixel)
            transparent = true;
            for (int x = charX + mutableCharWidth - 1; x >= charX && transparent; x--, transparentColumns++) {
                for (int y = charY; y < charY + charHeight; y++) {
                    transparent = atlasReader.getColor(x, y).getOpacity() <= 0d && transparent;
                }
            }
            if (transparentColumns == mutableCharWidth) {
                return null; // If the whole character is transparent
            }
            mutableCharWidth -= transparentColumns;

            return new WritableImage(atlasReader, charX, charY, mutableCharWidth, charHeight);
        });

        // If the character is not found or is transparent, memoize it as a space character
        if (result == null) {
            CHAR_CACHE.put(c, getCharImage(' '));
        }
        return CHAR_CACHE.get(c);
    }

    private static Image getStringImage(final String string) {
        final int charHeight = METADATA.atlasHeight / METADATA.rows;

        // If the string is null or empty, return a space character
        if (string == null || string.isEmpty()) {
            return getCharImage(' ');
        }
        // Precalculate the width of the string (in pixels)
        int stringWidth = 0;
        for (final Character c : string.toCharArray()) {
            stringWidth += (int) getCharImage(c).getWidth() + 1; // +1 for the space between characters
        }
        // Build the image for the string
        final WritableImage stringImage = new WritableImage(stringWidth, charHeight);
        final PixelWriter writer = stringImage.getPixelWriter();
        int x = 0; // Current x position in the string image
        for (final Character c : string.toCharArray()) {
            final Image charImage = getCharImage(c);
            final int currWidth = (int) charImage.getWidth();
            final int currHeight = (int) charImage.getHeight();
            final PixelReader charReader = charImage.getPixelReader();
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
        return new Image(Objects.requireNonNull(StringSpriteFactory.class.getResourceAsStream(METADATA.filename)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<StringSpriteKey> getKeyType() {
        return StringSpriteKey.class;
    }
}
