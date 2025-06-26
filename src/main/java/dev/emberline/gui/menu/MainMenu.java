package dev.emberline.gui.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.GameLoop;
import dev.emberline.core.event.EventDispatcher;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.core.sounds.AudioController;
import dev.emberline.core.sounds.event.SfxSoundEvent;
import dev.emberline.core.sounds.event.SfxSoundEvent.SoundType;
import dev.emberline.game.GameState;
import dev.emberline.gui.GuiButton;
import dev.emberline.gui.GuiLayer;
import dev.emberline.gui.event.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Represents the main menu state of the game, which serves as the starting point
 * for user interaction upon launching.
 * <p>
 * The MainMenu class handles the rendering of the main menu scene, displaying background graphics,
 * the game title, and interactive buttons for starting the game, accessing options, or exiting.
 * <p>
 * It is initialized with predefined menu bounds, loaded from a configuration
 * file, defining the areas of the GUI dedicated to the main menu.
 * <p>
 * It is responsible for triggering appropriate events for game state transitions
 * (e.g., starting the game, opening options, or exiting).
 */
public class MainMenu extends GuiLayer implements GameState {

    private final MenuBounds menuBounds;

    private static class Layout {
        // Background
        private static final double BG_WIDTH = 32;
        private static final double BG_HEIGHT = 18;
        // Title
        private static final double TITLE_WIDTH = 17;
        private static final double TITLE_HEIGHT = 5;
        private static final double TITLE_X = (BG_WIDTH - TITLE_WIDTH) / 2;
        private static final double TITLE_Y = (BG_HEIGHT - TITLE_HEIGHT) / 2 - 3.5;

        // Start Button
        private static final double SCALE_FACTOR = 1.7;
        private static final double BTN_START_HEIGHT = 1.5 * SCALE_FACTOR;
        private static final double BTN_START_WIDTH = 3.5 * SCALE_FACTOR;
        private static final double BTN_START_X = (BG_WIDTH - BTN_START_WIDTH) / 2;
        private static final double BTN_START_Y = TITLE_Y + TITLE_HEIGHT - 0.05 * SCALE_FACTOR;
        // Options Button
        private static final double BTN_OPTIONS_HEIGHT = 1.5 * SCALE_FACTOR;
        private static final double BTN_OPTIONS_WIDTH = 3.5 * SCALE_FACTOR;
        private static final double BTN_OPTIONS_X = (BG_WIDTH - BTN_OPTIONS_WIDTH) / 2;
        private static final double BTN_OPTIONS_Y = BTN_START_Y + BTN_START_HEIGHT - 0.25;
        // Exit Button
        private static final double BTN_EXIT_HEIGHT = 1.5 * SCALE_FACTOR;
        private static final double BTN_EXIT_WIDTH = 3.5 * SCALE_FACTOR;
        private static final double BTN_EXIT_X = (BG_WIDTH - BTN_EXIT_WIDTH) / 2;
        private static final double BTN_EXIT_Y = BTN_OPTIONS_Y + BTN_OPTIONS_HEIGHT - 0.25;
    }

    // menu bounds
    private record Coordinate(
            @JsonProperty int x,
            @JsonProperty int y
    ) {
    }

    private record MenuBounds(
            @JsonProperty Coordinate topLeftBound,
            @JsonProperty Coordinate bottomRightBound
    ) {
    }


    // TODO refactor this constructors

    /**
     * Constructs a new instance of {@code MainMenu}.
     * This constructor initializes the main menu by loading its configuration from a JSON resource file.
     *
     * @throws RuntimeException if an error occurs during the configuration loading or deserialization process.
     * @see MainMenu
     */
    public MainMenu() {
        this(ConfigLoader.loadConfig("/gui/menu/menuBounds.json", MenuBounds.class));
    }

    private MainMenu(final MenuBounds menuBounds) {
        super(menuBounds.topLeftBound.x, menuBounds.topLeftBound.y, menuBounds.bottomRightBound.x - menuBounds.topLeftBound.x, menuBounds.bottomRightBound.y - menuBounds.topLeftBound.y);
        this.menuBounds = menuBounds;
    }

    // Start button
    private void addStartButton() {
        final GuiButton startButton = new GuiButton(Layout.BTN_START_X, Layout.BTN_START_Y, Layout.BTN_START_WIDTH, Layout.BTN_START_HEIGHT, SpriteLoader.loadSprite(SingleSpriteKey.START_SIGN_BUTTON).image(), SpriteLoader.loadSprite(SingleSpriteKey.START_SIGN_BUTTON_HOVER).image());
        startButton.setOnClick(() -> throwEvent(new SetStartEvent(startButton)));
        super.buttons.add(startButton);
    }

    // Options button
    private void addOptionsButton() {
        final GuiButton optionsButton = new GuiButton(Layout.BTN_OPTIONS_X, Layout.BTN_OPTIONS_Y, Layout.BTN_OPTIONS_WIDTH, Layout.BTN_OPTIONS_HEIGHT, SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_SIGN_BUTTON).image(),  SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_SIGN_BUTTON_HOVER).image());
        optionsButton.setOnClick(() -> throwEvent(new OpenOptionsEvent(optionsButton)));
        super.buttons.add(optionsButton);
    }

    // Exit button
    private void addExitButton() {
        final GuiButton exitButton = new GuiButton(Layout.BTN_EXIT_X, Layout.BTN_EXIT_Y, Layout.BTN_EXIT_WIDTH, Layout.BTN_EXIT_HEIGHT, SpriteLoader.loadSprite(SingleSpriteKey.EXIT_SIGN_BUTTON).image(), SpriteLoader.loadSprite(SingleSpriteKey.EXIT_SIGN_BUTTON_HOVER).image());
        exitButton.setOnClick(() -> throwEvent(new ExitGameEvent(exitButton)));
        super.buttons.add(exitButton);
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

        addStartButton();
        addOptionsButton();
        addExitButton();

        final double menuScreenWidth = menuBounds.bottomRightBound.x * cs.getScale();
        final double menuScreenHeight = menuBounds.bottomRightBound.y * cs.getScale();
        final double menuScreenX = cs.toScreenX(menuBounds.topLeftBound.x);
        final double menuScreenY = cs.toScreenY(menuBounds.topLeftBound.y);

        final Image menuBackground = SpriteLoader.loadSprite(SingleSpriteKey.MENU_BACKGROUND).image();
        final Image emberlineTitle = SpriteLoader.loadSprite(SingleSpriteKey.EMBERLINE_TITLE).image();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(menuBackground, menuScreenX, menuScreenY, menuScreenWidth, menuScreenHeight);
            gc.drawImage(emberlineTitle, cs.toScreenX(Layout.TITLE_X), cs.toScreenY(Layout.TITLE_Y), Layout.TITLE_WIDTH * cs.getScale(), Layout.TITLE_HEIGHT * cs.getScale());
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
