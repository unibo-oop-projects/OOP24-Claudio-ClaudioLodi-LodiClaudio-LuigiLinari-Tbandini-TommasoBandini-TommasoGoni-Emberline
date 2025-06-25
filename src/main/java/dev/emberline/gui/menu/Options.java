package dev.emberline.gui.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.GameLoop;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.core.sounds.AudioController;
import dev.emberline.core.sounds.event.SfxSoundEvent.SoundType;
import dev.emberline.game.GameState;
import dev.emberline.gui.GuiButton;
import dev.emberline.gui.GuiLayer;
import dev.emberline.gui.event.CloseOptionsEvent;
import dev.emberline.gui.event.SetMainMenuEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Options extends GuiLayer implements GameState {
    private final OptionsBounds bounds;
    private final boolean showMenuButton;

    private static class Layout {
        // Background
        private static final double BG_WIDTH = 32;
        private static final double BG_HEIGHT = 18;
        // Title
        private static final double SCALE = 1.4;
        private static final double WINDOW_BG_WIDTH = 7 * SCALE;
        private static final double WINDOW_BG_HEIGHT = 5 * SCALE;
        private static final double WINDOW_BG_X = (BG_WIDTH - WINDOW_BG_WIDTH) / 2;
        private static final double WINDOW_BG_Y = (BG_HEIGHT - WINDOW_BG_HEIGHT) / 2 - 2;

        // Back Button
        private static final double SCALE_FACTOR = 1.7;
        private static final double BTN_BACK_HEIGHT = 1.5 * SCALE_FACTOR;
        private static final double BTN_BACK_WIDTH = 3.5 * SCALE_FACTOR;
        private static final double BTN_BACK_X = (BG_WIDTH - BTN_BACK_WIDTH) / 2;
        private static final double BTN_BACK_Y = WINDOW_BG_Y + WINDOW_BG_HEIGHT - 0.05 * SCALE_FACTOR;
        // Menu Button
        private static final double BTN_MENU_HEIGHT = 1.5 * SCALE_FACTOR;
        private static final double BTN_MENU_WIDTH = 3.5 * SCALE_FACTOR;
        private static final double BTN_MENU_X = (BG_WIDTH - BTN_MENU_WIDTH) / 2;
        private static final double BTN_MENU_Y = BTN_BACK_Y + BTN_BACK_HEIGHT - 0.25;
    }

    // Options bounds
    private record Coordinate(
        @JsonProperty int x,
        @JsonProperty int y
    ) {
    }

    private record OptionsBounds(
        @JsonProperty Coordinate topLeftBound,
        @JsonProperty Coordinate bottomRightBound
    ) {
    }

    public Options(final boolean showMenuButton) {
        this(ConfigLoader.loadConfig("/gui/options/optionsBounds.json", OptionsBounds.class), showMenuButton);
    }

    private Options(final OptionsBounds bounds, final boolean showMenuButton) {
        super(bounds.topLeftBound.x, bounds.topLeftBound.y, bounds.bottomRightBound.x - bounds.topLeftBound.x, bounds.bottomRightBound.y - bounds.topLeftBound.y);
        this.bounds = bounds;
        this.showMenuButton = showMenuButton;
    }

    // Update the layout of the options menu
    private void updateLayout() {
        // Clear existing buttons
        super.buttons.clear();

        addMusicVolumeControl();
        addMusicCheckbox();
        addSfxVolumeControl();
        addSfxCheckbox();
        addFullScreenCheckbox();
        
        // Add navigation buttons
        addCloseOptionsButton();
        
        if (showMenuButton) {
            addMenuOptionsButton();
        }
    }

    // Music volume control
    private void addMusicVolumeControl() {
        
    }
    
    // Music checkbox
    private void addMusicCheckbox() {
        
    }
    
    // SFX volume control
    private void addSfxVolumeControl() {
        
    }

    // SFX checkbox
    private void addSfxCheckbox() {
        
    }

    // Full screen checkbox
    private void addFullScreenCheckbox() {

    }

    // Back navigation button
    private void addCloseOptionsButton() {
        final GuiButton backButton = new GuiButton(Layout.BTN_BACK_X,
                Layout.BTN_BACK_Y, Layout.BTN_BACK_WIDTH,
                Layout.BTN_BACK_HEIGHT, SpriteLoader.loadSprite(SingleSpriteKey.BACK_SIGN_BUTTON_1).image(), SpriteLoader.loadSprite(SingleSpriteKey.BACK_SIGN_BUTTON_2).image());
        backButton.setOnClick(() -> throwEvent(new CloseOptionsEvent(this)));
        super.buttons.add(backButton);
    }

    // Menu navigation button
    private void addMenuOptionsButton() {
        final GuiButton menuButton = new GuiButton(Layout.BTN_MENU_X,
                Layout.BTN_MENU_Y, Layout.BTN_MENU_WIDTH,
                Layout.BTN_MENU_HEIGHT, SpriteLoader.loadSprite(SingleSpriteKey.MENU_SIGN_BUTTON_1).image(), SpriteLoader.loadSprite(SingleSpriteKey.MENU_SIGN_BUTTON_2).image());
        menuButton.setOnClick(() -> throwEvent(new SetMainMenuEvent(this)));
        super.buttons.add(menuButton);
    }

    @Override
    public void render() {
        // Render background
        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem cs = renderer.getGuiCoordinateSystem();

        updateLayout();

        final double menuScreenWidth = bounds.bottomRightBound.x * cs.getScale();
        final double menuScreenHeight = bounds.bottomRightBound.y * cs.getScale();
        final double menuScreenX = cs.toScreenX(bounds.topLeftBound.x);
        final double menuScreenY = cs.toScreenY(bounds.topLeftBound.y);

        final Image menuBackground = SpriteLoader.loadSprite(SingleSpriteKey.MENU_BACKGROUND).image();
        final Image windowBackground = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_WINDOW_BACKGROUND).image();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(menuBackground, menuScreenX, menuScreenY, menuScreenWidth, menuScreenHeight);
            gc.drawImage(windowBackground, cs.toScreenX(Layout.WINDOW_BG_X), cs.toScreenY(Layout.WINDOW_BG_Y), Layout.WINDOW_BG_WIDTH * cs.getScale(), Layout.WINDOW_BG_HEIGHT * cs.getScale());
        }));

        super.render();
    }

    @Override
    public void update(final long elapsed) {
    }

}
