package dev.emberline.gui.menu;

import java.util.prefs.Preferences;

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
import dev.emberline.game.GameState;
import dev.emberline.gui.GuiButton;
import dev.emberline.gui.GuiLayer;
import dev.emberline.gui.event.CloseOptionsEvent;
import dev.emberline.gui.event.SetMainMenuEvent;
import dev.emberline.preferences.PreferenceKey;
import dev.emberline.preferences.PreferencesManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Represents the "Options" menu in the game, providing functionality for rendering
 * and interacting with the options interface of the GUI layer. This class controls
 * the layout of buttons and the display of the background for the options menu.
 * <p>
 * The class extends the {@link GuiLayer} to manage its graphical elements
 * and interactions within a defined layer, while implementing the {@link GameState}.
 * <p>
 * It is responsible to throw change of option related events.
 */
public class Options extends GuiLayer implements GameState {
    private final OptionsBounds bounds;
    private final boolean showMenuButton;
    private static final Preferences prefs = Preferences.userRoot().node("dev.emberline.audio");

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

        // Gui buttons dimensions
        private static final double btn_scale = 1.5;
        private static final double BTN_WIDTH = 1 * SCALE;
        private static final double BTN_HEIGHT = 1 * SCALE;

        // Music Control that uses dimensions from above
        private static final double VOLUME_CONTROL_X = (BG_WIDTH - BTN_WIDTH) / 2;
        private static final double VOLUME_CONTROL_Y = WINDOW_BG_Y + 0.5 * SCALE;
        
        // Music checkbox
        private static final double CHECKBOX_X = VOLUME_CONTROL_X + BTN_WIDTH + 0.5 * SCALE;
        private static final double CHECKBOX_Y = VOLUME_CONTROL_Y + 0.5 * SCALE;

        // Sfx Control that uses dimensions from above
        private static final double SFX_CONTROL_X = VOLUME_CONTROL_X;
        private static final double SFX_CONTROL_Y = VOLUME_CONTROL_Y + BTN_HEIGHT + 0.5 * SCALE;

        // Sfx checkbox
        private static final double SFX_CHECKBOX_X = CHECKBOX_X;
        private static final double SFX_CHECKBOX_Y = CHECKBOX_Y + BTN_HEIGHT + 0.5 * SCALE;

        // Fullscreen checkbox
        private static final double FULLSCREEN_CHECKBOX_X = CHECKBOX_X;
        private static final double FULLSCREEN_CHECKBOX_Y = CHECKBOX_Y + BTN_HEIGHT + 0.5 * SCALE;

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

    /**
     * Constructs an {@code Options} object by initializing it with the configuration
     * data loaded from a predefined JSON resource file. The configuration provides
     * the bounds necessary for setting up the {@code Options} screen in the GUI.
     *
     * @param showMenuButton a boolean representing whether the options menu should or should not
     *                       have a menu button
     * @throws RuntimeException if the configuration file cannot be loaded or parsed.
     * @see Options
     */
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
        final GuiButton musicVolumeControl = new GuiButton(Layout.VOLUME_CONTROL_X, Layout.VOLUME_CONTROL_Y, Layout.BTN_WIDTH, Layout.BTN_HEIGHT,
                SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_MINUS_BUTTON).image(),
                SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_MINUS_BUTTON_HOVER).image());
        musicVolumeControl.setOnClick(() -> {
           AudioController.requestDecreaseMusicVolume(this);
           PreferencesManager.getBooleanPreference(PreferenceKey.MUSIC_MUTE);
           //AudioController.requestIncreaseMusicVolume(this);
        });
        super.buttons.add(musicVolumeControl);
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
                Layout.BTN_BACK_HEIGHT, SpriteLoader.loadSprite(SingleSpriteKey.BACK_SIGN_BUTTON).image(), SpriteLoader.loadSprite(SingleSpriteKey.BACK_SIGN_BUTTON_HOVER).image());
        backButton.setOnClick(() -> throwEvent(new CloseOptionsEvent(this)));
        super.buttons.add(backButton);
    }

    // Menu navigation button
    private void addMenuOptionsButton() {
        final GuiButton menuButton = new GuiButton(Layout.BTN_MENU_X,
                Layout.BTN_MENU_Y, Layout.BTN_MENU_WIDTH,
                Layout.BTN_MENU_HEIGHT, SpriteLoader.loadSprite(SingleSpriteKey.MENU_SIGN_BUTTON).image(), SpriteLoader.loadSprite(SingleSpriteKey.MENU_SIGN_BUTTON_HOVER).image());
        menuButton.setOnClick(() -> throwEvent(new SetMainMenuEvent(this)));
        super.buttons.add(menuButton);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final long elapsed) {
    }

}
