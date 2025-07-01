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
    private final SaveSelectionBounds bounds;
    private final Layout layout;
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

    private record Layout(
            @JsonProperty 
            double windowBgWidth,
            @JsonProperty 
            double windowBgHeight,
            @JsonProperty 
            double windowBgX,
            @JsonProperty 
            double windowBgY,
            @JsonProperty 
            double saveSlotWidth,
            @JsonProperty 
            double deleteBtnWidth,
            @JsonProperty 
            double btnHeight,
            @JsonProperty 
            double saveSlotX,
            @JsonProperty 
            double firstSlotY,
            @JsonProperty 
            double secondSlotY,
            @JsonProperty 
            double thirdSlotY,
            @JsonProperty 
            double deleteBtnX,
            @JsonProperty 
            double btnBackHeight,
            @JsonProperty 
            double btnBackWidth,
            @JsonProperty 
            double btnBackX,
            @JsonProperty 
            double btnBackY
    ){
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
                throw new IllegalArgumentException("Invalid save selection bounds: " + this);
            }
        }
    }

    /**
     * Constructs a new {@code SaveSelection} instance using the default save selection bounds loaded from a configuration file.
     */
    public SaveSelection() {
        this(ConfigLoader.loadConfig("/gui/guiBounds.json", SaveSelectionBounds.class),
             ConfigLoader.loadConfig("/gui/saveSelection/saveSelectionLayout.json", Layout.class));
    }

    private SaveSelection(final SaveSelectionBounds bounds, final Layout layout) {
        super(bounds.topLeftX, bounds.topLeftY, 
              bounds.bottomRightX - bounds.topLeftX, 
              bounds.bottomRightY - bounds.topLeftY);
        this.bounds = bounds;
        this.layout = layout;
    }

    private void updateLayout() {
        super.getButtons().clear();

        addSaveSlot(layout.firstSlotY, Saves.SAVE1);
        addSaveSlot(layout.secondSlotY, Saves.SAVE2);
        addSaveSlot(layout.thirdSlotY, Saves.SAVE3);

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
            layout.saveSlotX, 
            currY,
            layout.saveSlotWidth, 
            layout.btnHeight,
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
            layout.deleteBtnX, 
            currY, 
            layout.deleteBtnWidth, 
            layout.btnHeight,
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
            layout.btnBackX,
            layout.btnBackY, 
            layout.btnBackWidth,
            layout.btnBackHeight, 
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

        final double guiScreenWidth = (bounds.bottomRightX - bounds.topLeftX) * cs.getScale();
        final double guiScreenHeight = (bounds.bottomRightY - bounds.topLeftY) * cs.getScale();
        final double guiScreenX = cs.toScreenX(bounds.topLeftX);
        final double guiScreenY = cs.toScreenY(bounds.topLeftY);

        updateLayout();

        final Image guiBackground = SpriteLoader.loadSprite(SingleSpriteKey.GUI_BACKGROUND).image();
        final Image windowBackground = SpriteLoader.loadSprite(SingleSpriteKey.SAVES_WINDOW_BACKGROUND).image();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(guiBackground, guiScreenX, guiScreenY, guiScreenWidth, guiScreenHeight);
            gc.drawImage(windowBackground, cs.toScreenX(layout.windowBgX), cs.toScreenY(layout.windowBgY), 
                        layout.windowBgWidth * cs.getScale(), layout.windowBgHeight * cs.getScale());
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
