package dev.emberline.gui.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.GameLoop;
import dev.emberline.core.config.ConfigLoader;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.GameState;
import dev.emberline.gui.GuiButton;
import dev.emberline.gui.GuiLayer;
import dev.emberline.gui.event.ExitGameEvent;
import dev.emberline.gui.event.OpenOptionsEvent;
import dev.emberline.gui.event.SetStartEvent;
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

    private final MenuBounds bounds;
    private final Layout layout;

    private record Layout(
            @JsonProperty 
            double titleWidth,
            @JsonProperty 
            double titleHeight,
            @JsonProperty 
            double titleX,
            @JsonProperty 
            double titleY,
            @JsonProperty 
            double navBtnHeight,
            @JsonProperty 
            double navBtnWidth,
            @JsonProperty 
            double navBtnX,
            @JsonProperty 
            double navStartBtnY,
            @JsonProperty 
            double navOptionsBtnY,
            @JsonProperty 
            double navExitBtnY
    ) {
    }

    private record MenuBounds(
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
        private MenuBounds {
            if (topLeftX >= bottomRightX || topLeftY >= bottomRightY) {
                throw new IllegalArgumentException("Invalid menu bounds: " + this);
            }
        }
    }

    /**
     * Constructs a new instance of {@code MainMenu}.
     * This constructor initializes the main menu by loading its configuration from a JSON resource file.
     *
     * @throws RuntimeException if an error occurs during the configuration loading or deserialization process.
     * @see MainMenu
     */
    public MainMenu() {
        this(ConfigLoader.loadConfig("/gui/guiBounds.json", MenuBounds.class),
             ConfigLoader.loadConfig("/gui/menu/mainMenuLayout.json", Layout.class));
    }

    private MainMenu(final MenuBounds bounds, final Layout layout) {
        super(bounds.topLeftX, bounds.topLeftY,
        bounds.bottomRightX - bounds.topLeftX, bounds.bottomRightY - bounds.topLeftY);
        this.bounds = bounds;
        this.layout = layout;
    }

    // Start button
    private void addStartButton() {
        final GuiButton startButton = new GuiButton(layout.navBtnX, layout.navStartBtnY,
                layout.navBtnWidth, layout.navBtnHeight,
                SpriteLoader.loadSprite(SingleSpriteKey.START_SIGN_BUTTON).image(),
                SpriteLoader.loadSprite(SingleSpriteKey.START_SIGN_BUTTON_HOVER).image());
        startButton.setOnClick(() -> throwEvent(new SetStartEvent(startButton)));
        super.getButtons().add(startButton);
    }

    // Options button
    private void addOptionsButton() {
        final GuiButton optionsButton = new GuiButton(layout.navBtnX, layout.navOptionsBtnY,
                layout.navBtnWidth, layout.navBtnHeight,
                SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_SIGN_BUTTON).image(),
                SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_SIGN_BUTTON_HOVER).image());
        optionsButton.setOnClick(() -> throwEvent(new OpenOptionsEvent(optionsButton)));
        super.getButtons().add(optionsButton);
    }

    // Exit button
    private void addExitButton() {
        final GuiButton exitButton = new GuiButton(layout.navBtnX, layout.navExitBtnY,
                layout.navBtnWidth, layout.navBtnHeight,
                SpriteLoader.loadSprite(SingleSpriteKey.EXIT_SIGN_BUTTON).image(),
                SpriteLoader.loadSprite(SingleSpriteKey.EXIT_SIGN_BUTTON_HOVER).image());
        exitButton.setOnClick(() -> throwEvent(new ExitGameEvent(exitButton)));
        super.getButtons().add(exitButton);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem cs = renderer.getGuiCoordinateSystem();

        addStartButton();
        addOptionsButton();
        addExitButton();

        final double menuScreenWidth = (bounds.bottomRightX - bounds.topLeftX) * cs.getScale();
        final double menuScreenHeight = (bounds.bottomRightY - bounds.topLeftY) * cs.getScale();
        final double menuScreenX = cs.toScreenX(bounds.topLeftX);
        final double menuScreenY = cs.toScreenY(bounds.topLeftY);

        final Image menuBackground = SpriteLoader.loadSprite(SingleSpriteKey.GUI_BACKGROUND).image();
        final Image emberlineTitle = SpriteLoader.loadSprite(SingleSpriteKey.EMBERLINE_TITLE).image();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(menuBackground, menuScreenX, menuScreenY, menuScreenWidth, menuScreenHeight);
            gc.drawImage(emberlineTitle, cs.toScreenX(layout.titleX), cs.toScreenY(layout.titleY),
                    layout.titleWidth * cs.getScale(), layout.titleHeight * cs.getScale());
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
