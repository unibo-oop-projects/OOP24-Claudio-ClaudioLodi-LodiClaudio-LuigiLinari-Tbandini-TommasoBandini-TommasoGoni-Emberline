package dev.emberline.gui;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.util.*;

public enum _TemporarySpriteProvider {
    STATS_BACKGROUND(_TemporarySpriteProvider.TOWERDIALOG_PNG, 0, 3, 79, 51),
    TDL_BACKGROUND(_TemporarySpriteProvider.TOWERDIALOG_PNG, 80, 0, 96, 160),
    CANCEL_BUTTON(_TemporarySpriteProvider.TOWERDIALOG_PNG, 50, 55, 12, 19),
    GENERIC_BUTTON(_TemporarySpriteProvider.TOWERDIALOG_PNG, 7, 55, 35, 19),
    EMPTY_UPGRADE_LEVEL(_TemporarySpriteProvider.TOWERDIALOG_PNG, 57, 87, 8, 19),
    FULL_UPGRADE_LEVEL(_TemporarySpriteProvider.TOWERDIALOG_PNG, 39, 87, 8, 19),
    ADD_BUTTON(_TemporarySpriteProvider.TOWERDIALOG_PNG, 7, 87, 19, 19),
    TOWER_TEXT(_TemporarySpriteProvider.TOWERDIALOG_PNG, 2, 113, 71, 14),
    CHARS_ATLAS("/EmberlineFontEmbossed.png", 0, 0, 256, 96),
    ICE_BUTTON("/menu_assets/tempbuttons.png", 0, 0, 16, 16),
    FIRE_BUTTON("/menu_assets/tempbuttons.png", 16, 0, 16, 16),
    SMALL_BUTTON("/menu_assets/tempbuttons.png", 48, 0, 16, 16),
    BIG_BUTTON("/menu_assets/tempbuttons.png", 32, 0, 16, 16),
    UPGRADE_BUTTON("/menu_assets/tempbuttons.png", 0, 16, 16, 16),
    DISABLED_UPGRADE_BUTTON("/menu_assets/tempbuttons.png", 16, 16, 16, 16),
    SMALL_ICON(_TemporarySpriteProvider.ICON_PNG, 0, 0, 16, 16),
    BIG_ICON(_TemporarySpriteProvider.ICON_PNG, 16, 0, 16, 16),
    FIRE_ICON(_TemporarySpriteProvider.ICON_PNG, 32, 0, 16, 16),
    ICE_ICON(_TemporarySpriteProvider.ICON_PNG, 48, 0, 16, 16),
    // Tower dialog assets
    FIRE_RATE(_TemporarySpriteProvider.STATS_PNG, 0, 0, 16, 16),
    DAMAGE(_TemporarySpriteProvider.STATS_PNG, 16, 0, 16, 16),
    DAMAGE_AREA(_TemporarySpriteProvider.STATS_PNG, 32, 0, 16, 16),
    PROJECTILE_SPEED(_TemporarySpriteProvider.STATS_PNG, 48, 0, 16, 16),
    TOWER_RANGE(_TemporarySpriteProvider.STATS_PNG, 0, 16, 16, 16),
    EFFECT_DURATION(_TemporarySpriteProvider.STATS_PNG, 16, 16, 16, 16),
    BURN_EFFECT(_TemporarySpriteProvider.STATS_PNG, 32, 16, 16, 16),
    SLOW_EFFECT(_TemporarySpriteProvider.STATS_PNG, 48, 16, 16, 16);

    private final static String TOWERDIALOG_PNG = "/menu_assets/towerdialog.png";
    private final static String ICON_PNG = "/menu_assets/icons.png";
    private final static String STATS_PNG = "/menu_assets/statsIcons.png";
    private final static Map<Character, Image> CHAR_TO_IMAGE = Collections.synchronizedMap(new HashMap<>());
    private final static Map<String, Image> STRING_TO_IMAGE_CACHE = Collections.synchronizedMap(new LinkedHashMap<>() {
        private static final int MAX_ENTRIES = 100;
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Image> eldest) {
            return size() > MAX_ENTRIES;
        }
    });
    private final Image image;

    _TemporarySpriteProvider(String path, int x, int y, int width, int height) {
        // Obtaining image
        Image originalImage = new Image(Objects.requireNonNull(_TemporarySpriteProvider.class.getResourceAsStream(path)));
        image = new WritableImage(originalImage.getPixelReader(), x, y, width, height);
    }

    public Image getImage() {
        return image;
    }

    private static final int ATLAS_COLUMNS = 16;
    private static final int ATLAS_ROWS = 6;
    private static final int CHAR_WIDTH =  (int) (CHARS_ATLAS.getImage().getWidth() / ATLAS_COLUMNS);
    private static final int CHAR_HEIGHT = (int) (CHARS_ATLAS.getImage().getHeight() / ATLAS_ROWS);
    private static final String CHARS_ATLAS_ORDER = "@ABCDEFGHIJKLMNOPQRSTUVWXYZ[£]♥→€!\"#$%&`()*+'-.^0123456789:;<=>?/abcdefghijklmnopqrstuvwxyz ";

    public static Image getCharImage(Character c) {
        if (c == null) return getCharImage(' '); // If the character is null, return a space character

        Image result = CHAR_TO_IMAGE.computeIfAbsent(c, key -> {
            Image atlas = CHARS_ATLAS.getImage();

            // Locating the character in the atlas
            int charIndex = CHARS_ATLAS_ORDER.indexOf(c);
            if (charIndex == -1) {
                return null; // Character not found
            }
            int charX = charIndex % ATLAS_COLUMNS * CHAR_WIDTH; //pixel coordinate of the char inside the atlas
            int charY = charIndex / ATLAS_COLUMNS * CHAR_HEIGHT;

            PixelReader atlasReader = atlas.getPixelReader();

            // Truncate left and right columns of only transparent pixels (ignoring the space character)
            if (c == ' ') return new WritableImage(atlasReader, charX, charY, CHAR_WIDTH/2, CHAR_HEIGHT);
            int charWidth = CHAR_WIDTH;

            // Truncate left
            int transparentColumns = -1; // First let's count how many transparent columns are on the left (of the first non-transparent pixel)
            boolean transparent = true;
            for (int x = charX; x < charX + charWidth && transparent; x++, transparentColumns++) {
                for (int y = charY; y < charY + CHAR_HEIGHT; y++) {
                    transparent = !(atlasReader.getColor(x, y).getOpacity() > 0) && transparent;
                }
            }
            if (transparentColumns == charWidth) return null; // If the whole character is transparent
            charX += transparentColumns;
            charWidth -= transparentColumns;
            // Truncate right
            transparentColumns = -1; // Now how many transparent columns are on the right (of the last non-transparent pixel)
            transparent = true;
            for (int x = charX + charWidth - 1; x >= charX && transparent; x--, transparentColumns++) {
                for (int y = charY; y < charY + CHAR_HEIGHT; y++) {
                    transparent = !(atlasReader.getColor(x, y).getOpacity() > 0) && transparent;
                }
            }
            if (transparentColumns == charWidth) return null; // If the whole character is transparent
            charWidth -= transparentColumns;

            return new WritableImage(atlasReader, charX, charY, charWidth, CHAR_HEIGHT);
        });

        // If the character is not found or is transparent, memoize it as a space character
        if (result == null) CHAR_TO_IMAGE.put(c, getCharImage(' '));

        return CHAR_TO_IMAGE.get(c);
    }

    public static Image getStringImage(String string) {
        // If the string is null or empty, return a space character
        if (string == null || string.isEmpty()) return getCharImage(' ');
        // Check if the string is already cached
        if (STRING_TO_IMAGE_CACHE.containsKey(string)) {
            return STRING_TO_IMAGE_CACHE.get(string);
        }
        // Precalculate the width of the string (in pixels)
        int width = 0;
        for (Character c : string.toCharArray()) {
            width += (int) getCharImage(c).getWidth() + 1; // +1 for the space between characters
        }
        WritableImage image = new WritableImage(width, CHAR_HEIGHT);
        PixelWriter writer = image.getPixelWriter();
        int x = 0;
        for (Character c : string.toCharArray()) {
            Image charImage = getCharImage(c);
            int charWidth = (int) charImage.getWidth();
            int charHeight = (int) charImage.getHeight();
            PixelReader charReader = charImage.getPixelReader();
            for (int i = 0; i < charWidth; i++) {
                for (int j = 0; j < charHeight; j++) {
                    writer.setColor(x + i, j, charReader.getColor(i, j));
                }
            }
            x += charWidth + 1; // Add 1 pixel of space between characters
        }
        // Cache the image
        STRING_TO_IMAGE_CACHE.put(string, image);

        return image;
    }
}