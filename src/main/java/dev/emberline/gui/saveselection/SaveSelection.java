package dev.emberline.gui.saveselection;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.GameLoop;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.GameState;
import dev.emberline.gui.GuiButton;
import dev.emberline.gui.GuiLayer;
import dev.emberline.gui.event.SetMainMenuEvent;
import dev.emberline.gui.event.SetWorldEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;


public class SaveSelection extends GuiLayer implements GameState {
    private final SaveSelectionBounds bounds;

    private static class Layout {
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
        private static final double DELETE_BTN_WIDTH = BUTTON_SCALE_FACTOR ;
        private static final double BTN_HEIGHT = BUTTON_SCALE_FACTOR;

        // Save buttons
        private static final double NEW_SAVE_BUTTON_X = WINDOW_BG_X + 2.7;
        private static final double NEW_SAVE_BUTTON_Y = WINDOW_BG_Y + 2.8;
        private static final double DELETE_BTN_X = WINDOW_BG_X + WINDOW_BG_WIDTH - DELETE_BTN_WIDTH - 2.5;
        private static final double DELETE_BTN_Y = WINDOW_BG_Y + 2.7;

        // Navigation buttons
        private static final double NAV_SCALE_FACTOR = 1.7;
        private static final double BTN_BACK_HEIGHT = 1.5 * NAV_SCALE_FACTOR;
        private static final double BTN_BACK_WIDTH = 3.5 * NAV_SCALE_FACTOR;
        private static final double BTN_BACK_X = (BG_WIDTH - BTN_BACK_WIDTH) / 2;
        private static final double BTN_BACK_Y = WINDOW_BG_Y + WINDOW_BG_HEIGHT - 0.1;
    }

    private static final class Colors {
        private static final ColorAdjust SAVES_WRITINGS = new ColorAdjust(0.15, 0.9, -0.3, 0);
    }

    private record Coordinate(
        @JsonProperty int x,
        @JsonProperty int y
    ) {
    }

    private record SaveSelectionBounds(
        @JsonProperty Coordinate topLeftBound,
        @JsonProperty Coordinate bottomRightBound
    ) {
    }

    public SaveSelection() {
        this(ConfigLoader.loadConfig("/gui/guiBounds.json", SaveSelectionBounds.class));
    }

    private SaveSelection(final SaveSelectionBounds bounds) {
        super(bounds.topLeftBound.x, bounds.topLeftBound.y, bounds.bottomRightBound.x - bounds.topLeftBound.x, bounds.bottomRightBound.y - bounds.topLeftBound.y);
        this.bounds = bounds;
    }

    private void updateLayout(final GraphicsContext gc, final CoordinateSystem cs) {
        super.buttons.clear();

        addSaveSlot(gc, cs, Layout.NEW_SAVE_BUTTON_Y);
        addSaveSlot(gc, cs, Layout.NEW_SAVE_BUTTON_Y + Layout.BTN_HEIGHT + 0.3);
        addSaveSlot(gc, cs, Layout.NEW_SAVE_BUTTON_Y + 2 * (Layout.BTN_HEIGHT + 0.3));

        addBackButton();
    }

    private void addSaveSlot(final GraphicsContext gc, final CoordinateSystem cs, final double currY) {
        // TODO Get the save slot data somehow
        Image newSaveButtonImage = SpriteLoader.loadSprite(SingleSpriteKey.NEW_SAVE_BUTTON).image();
        Image newSaveButtonHoverImage = SpriteLoader.loadSprite(SingleSpriteKey.NEW_SAVE_BUTTON_HOVER).image();
        Image saveButtonImage = SpriteLoader.loadSprite(SingleSpriteKey.SAVE_BUTTON).image();
        Image saveButtonHoverImage = SpriteLoader.loadSprite(SingleSpriteKey.SAVE_BUTTON_HOVER).image();

        Image deleteSaveButtonImage = SpriteLoader.loadSprite(SingleSpriteKey.SAVES_DELETE_BUTTON).image();
        Image deleteSaveButtonHoverImage = SpriteLoader.loadSprite(SingleSpriteKey.SAVES_DELETE_BUTTON_HOVER).image();
        Image deleteSaveButtonDisabledImage = SpriteLoader.loadSprite(SingleSpriteKey.SAVES_DELETE_BUTTON_DISABLED).image();
 
        // Create the save slot button
        final GuiButton newSaveSlotButton = new GuiButton(
            Layout.NEW_SAVE_BUTTON_X, 
            currY, 
            Layout.NEW_SAVE_BUTTON_WIDTH, 
            Layout.BTN_HEIGHT, 
            newSaveButtonImage,
            newSaveButtonHoverImage
        );
        newSaveSlotButton.setOnClick(() -> {
            throwEvent(new SetWorldEvent(newSaveSlotButton));
        });
        super.buttons.add(newSaveSlotButton);

        // Create the delete save button
        final GuiButton deleteSaveButton = new GuiButton(
            Layout.DELETE_BTN_X, 
            currY, 
            Layout.DELETE_BTN_WIDTH, 
            Layout.BTN_HEIGHT, 
            deleteSaveButtonImage, 
            deleteSaveButtonHoverImage
        );
        deleteSaveButton.setOnClick(() -> {
        });
        super.buttons.add(deleteSaveButton);
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
        super.buttons.add(backButton);
    }

    // TODO add text to the save slots
    // private void drawSavesText(final GraphicsContext gc, final CoordinateSystem cs) {
    //     gc.save();
    //     gc.setEffect(Colors.OPTIONS_WRITINGS);
    //     gc.restore();
    // }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem cs = renderer.getGuiCoordinateSystem();

        final double guiScreenWidth = bounds.bottomRightBound.x * cs.getScale();
        final double guiScreenHeight = bounds.bottomRightBound.y * cs.getScale();
        final double guiScreenX = cs.toScreenX(bounds.topLeftBound.x);
        final double guiScreenY = cs.toScreenY(bounds.topLeftBound.y);

        updateLayout(gc, cs);

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