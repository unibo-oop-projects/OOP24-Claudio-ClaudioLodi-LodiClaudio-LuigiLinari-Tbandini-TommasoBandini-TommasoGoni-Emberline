package dev.emberline.gui.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.GameLoop;
import dev.emberline.core.config.ConfigLoader;
import dev.emberline.core.event.EventDispatcher;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.GameState;
import dev.emberline.game.Serializer;
import dev.emberline.game.world.World;
import dev.emberline.gui.GuiButton;
import dev.emberline.gui.GuiLayer;
import dev.emberline.gui.event.SetMainMenuEvent;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The {@code SaveSelection} class represents a GUI layer for selecting and managing game saves.
 * It allows players to create new saves, delete existing ones, and load saved games.
 * The saves are stored in a specified directory and can be serialized/deserialized using the {@link Serializer}.
 */
public class SaveSelection extends GuiLayer implements GameState {
    private final SaveSelectionBounds saveSelectionBounds;
    private final Serializer worldSerializer = new Serializer();
    private static final String SAVE_DIRECTORY = "saves/";

    /**
     * Enum representing the available save slots.
     * Each enum constant corresponds to a save slot and contains a display name used for identification.
     */
    public enum Saves {
        /**
         * First save slot.
         */
        SAVE1("save1"),
        /**
         * Second save slot.
         */
        SAVE2("save2"),
        /**
         * Third save slot.
         */
        SAVE3("save3");

        private final String displayName;

        Saves(final String displayName) {
            this.displayName = displayName;
        }

        /**
         * @return the display name of the save slot
         */
        public String getDisplayName() {
            return displayName;
        }
    }

    private final static class Layout {
        // Background
        private static final double BG_WIDTH = 32;
        private static final double BG_HEIGHT = 18;

        // Title and window
        private static final double SCALE = 1.8;
        private static final double WINDOW_BG_WIDTH = 7 * SCALE;
        private static final double WINDOW_BG_HEIGHT = 5 * SCALE;
        private static final double WINDOW_BG_X = (BG_WIDTH - WINDOW_BG_WIDTH) / 2;
        private static final double WINDOW_BG_Y = (BG_HEIGHT - WINDOW_BG_HEIGHT) / 2 - 2;

        // Button scaling
        private static final double BUTTON_SCALE_FACTOR = 1.6;
        private static final double NEW_SAVE_BUTTON_WIDTH = BUTTON_SCALE_FACTOR * 3.28;
        private static final double DELETE_BTN_WIDTH = BUTTON_SCALE_FACTOR;
        private static final double BTN_HEIGHT = BUTTON_SCALE_FACTOR;

        // Save buttons
        private static final double NEW_SAVE_BUTTON_X = WINDOW_BG_X + 2.7;
        private static final double NEW_SAVE_BUTTON_Y = WINDOW_BG_Y + 2.8;
        private static final double DELETE_BTN_X = WINDOW_BG_X + WINDOW_BG_WIDTH - DELETE_BTN_WIDTH - 2.5;

        // Navigation buttons
        private static final double NAV_SCALE_FACTOR = 1.7;
        private static final double BTN_BACK_HEIGHT = 1.5 * NAV_SCALE_FACTOR;
        private static final double BTN_BACK_WIDTH = 3.5 * NAV_SCALE_FACTOR;
        private static final double BTN_BACK_X = (BG_WIDTH - BTN_BACK_WIDTH) / 2;
        private static final double BTN_BACK_Y = WINDOW_BG_Y + WINDOW_BG_HEIGHT;
    }

    private record SaveSelectionBounds(
        @JsonProperty
        int topLeftX,
        @JsonProperty
        int topLeftY,
        @JsonProperty
        int bottomRightX,
        @JsonProperty
        int bottomRightY
    ) {
        // Data validation
        private SaveSelectionBounds {
            if (topLeftX >= bottomRightX || topLeftY >= bottomRightY) {
                throw new IllegalArgumentException("Invalid save selection saveSelectionBounds: " + this);
            }
        }
    }

    /**
     * Constructs a new {@code SaveSelection} instance using the default save selection bounds loaded from a configuration file.
     */
    public SaveSelection() {
        this(ConfigLoader.loadConfig("/gui/guiBounds.json", SaveSelectionBounds.class));
    }

    private SaveSelection(final SaveSelectionBounds saveSelectionBounds) {
        super(saveSelectionBounds.topLeftX, saveSelectionBounds.topLeftY, 
              saveSelectionBounds.bottomRightX - saveSelectionBounds.topLeftX, 
              saveSelectionBounds.bottomRightY - saveSelectionBounds.topLeftY);
        this.saveSelectionBounds = saveSelectionBounds;
    }

    private void updateLayout() {
        super.getButtons().clear();

        addSaveSlot(Layout.NEW_SAVE_BUTTON_Y, Saves.SAVE1);
        addSaveSlot(Layout.NEW_SAVE_BUTTON_Y + Layout.BTN_HEIGHT + 0.3, Saves.SAVE2);
        addSaveSlot(Layout.NEW_SAVE_BUTTON_Y + 2 * (Layout.BTN_HEIGHT + 0.3), Saves.SAVE3);

        addBackButton();
    }

    private void addSaveSlot(final double currY, final Saves save) {
        final boolean saveExists = saveExists(save);

        final Image saveSlotImage = saveExists
            ? SpriteLoader.loadSprite(getSaveButtonSpriteKey(save, false)).image()
            : SpriteLoader.loadSprite(SingleSpriteKey.NEW_SAVE_SLOT_BUTTON).image();
        final Image saveSlotHoverImage = saveExists
            ? SpriteLoader.loadSprite(getSaveButtonSpriteKey(save, true)).image()
            : SpriteLoader.loadSprite(SingleSpriteKey.NEW_SAVE_SLOT_BUTTON_HOVER).image();

        final Image deleteSaveSlotImage = saveExists
            ? SpriteLoader.loadSprite(SingleSpriteKey.DELETE_SAVE_SLOT_BUTTON).image()
            : SpriteLoader.loadSprite(SingleSpriteKey.DELETE_SAVE_SLOT_BUTTON_DISABLED).image();
        final Image deleteSaveSlotHoverImage = saveExists
            ? SpriteLoader.loadSprite(SingleSpriteKey.DELETE_SAVE_SLOT_BUTTON_HOVER).image()
            : SpriteLoader.loadSprite(SingleSpriteKey.DELETE_SAVE_SLOT_BUTTON_DISABLED).image();

        final GuiButton saveSlotButton = new GuiButton(
            Layout.NEW_SAVE_BUTTON_X, 
            currY,
            Layout.NEW_SAVE_BUTTON_WIDTH, 
            Layout.BTN_HEIGHT,
            saveSlotImage,
            saveSlotHoverImage
        );
        saveSlotButton.setOnClick(() -> {
            EventDispatcher.getInstance().unregisterAllListeners();
            final World world = saveExists 
                ? worldSerializer.getDeserializedWorld(save.displayName)
                : new World();
            GameLoop.getInstance().getGameRoot().setWorld(world, save);
        });
        super.getButtons().add(saveSlotButton);

        final GuiButton deleteSaveSlotButton = new GuiButton(
            Layout.DELETE_BTN_X, 
            currY, 
            Layout.DELETE_BTN_WIDTH, 
            Layout.BTN_HEIGHT,
            deleteSaveSlotImage,
            deleteSaveSlotHoverImage
        );
        deleteSaveSlotButton.setOnClick(() -> {
            if (saveExists) {
                Platform.runLater(() -> {
                    try {
                        Files.deleteIfExists(Path.of(SAVE_DIRECTORY + save.displayName));
                        updateLayout();
                    } catch (IOException e) {
                        throw new UncheckedIOException("Failed to delete save file: " + save.displayName, e);
                    }
                });
            }
        });
        super.getButtons().add(deleteSaveSlotButton);
    }

    private void addBackButton() {
        final GuiButton backButton = new GuiButton(
            Layout.BTN_BACK_X,
            Layout.BTN_BACK_Y, 
            Layout.BTN_BACK_WIDTH,
            Layout.BTN_BACK_HEIGHT, 
            SpriteLoader.loadSprite(SingleSpriteKey.BACK_SIGN_BUTTON).image(), 
            SpriteLoader.loadSprite(SingleSpriteKey.BACK_SIGN_BUTTON_HOVER).image()
        );
        backButton.setOnClick(() -> throwEvent(new SetMainMenuEvent(this)));
        super.getButtons().add(backButton);
    }

    private boolean saveExists(final Saves slot) {
        return Files.exists(Path.of(SAVE_DIRECTORY + slot.displayName));
    }

    private SingleSpriteKey getSaveButtonSpriteKey(final Saves slot, final boolean hover) {
        return switch (slot) {
            case SAVE1 -> hover ? SingleSpriteKey.SAVE_SLOT_1_HOVER : SingleSpriteKey.SAVE_SLOT_1;
            case SAVE2 -> hover ? SingleSpriteKey.SAVE_SLOT_2_HOVER : SingleSpriteKey.SAVE_SLOT_2;
            case SAVE3 -> hover ? SingleSpriteKey.SAVE_SLOT_3_HOVER : SingleSpriteKey.SAVE_SLOT_3;
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem cs = renderer.getGuiCoordinateSystem();

        final double guiScreenWidth = saveSelectionBounds.bottomRightX * cs.getScale();
        final double guiScreenHeight = saveSelectionBounds.bottomRightY * cs.getScale();
        final double guiScreenX = cs.toScreenX(saveSelectionBounds.topLeftX);
        final double guiScreenY = cs.toScreenY(saveSelectionBounds.topLeftY);

        updateLayout();

        final Image guiBackground = SpriteLoader.loadSprite(SingleSpriteKey.GUI_BACKGROUND).image();
        final Image windowBackground = SpriteLoader.loadSprite(SingleSpriteKey.SAVES_WINDOW_BACKGROUND).image();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(guiBackground, guiScreenX, guiScreenY, guiScreenWidth, guiScreenHeight);
            gc.drawImage(windowBackground, cs.toScreenX(Layout.WINDOW_BG_X), cs.toScreenY(Layout.WINDOW_BG_Y), 
                        Layout.WINDOW_BG_WIDTH * cs.getScale(), Layout.WINDOW_BG_HEIGHT * cs.getScale());
        }));

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
        }));

        super.render();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final long elapsed) {
    }
}
