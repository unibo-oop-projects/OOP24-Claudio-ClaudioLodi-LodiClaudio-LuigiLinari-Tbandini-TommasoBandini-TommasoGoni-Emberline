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
import dev.emberline.game.GameState;
import dev.emberline.gui.GuiButton;
import dev.emberline.gui.GuiLayer;
import dev.emberline.gui.event.CloseOptionsEvent;
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

    private static class Layout {
        // Background
        private static final double BG_WIDTH = 32;
        private static final double BG_HEIGHT = 18;
        // Start Button
        private static final double SCALE_FACTOR = 2.5;
        private static final double BTN_START_HEIGHT = 1.5 * SCALE_FACTOR;
        private static final double BTN_START_WIDTH = 3.5 * SCALE_FACTOR;
        private static final double BTN_START_X = (BG_WIDTH - BTN_START_WIDTH) / 2;
        private static final double BTN_START_Y = (BG_HEIGHT - BTN_START_HEIGHT) / 2;
        // Options Button
        private static final double BTN_OPTIONS_HEIGHT = 1.5 * SCALE_FACTOR;
        private static final double BTN_OPTIONS_WIDTH = 3.5 * SCALE_FACTOR;
        private static final double BTN_OPTIONS_X = (BG_WIDTH - BTN_OPTIONS_WIDTH) / 2;
        private static final double BTN_OPTIONS_Y = BTN_START_Y + BTN_START_HEIGHT - 0.25;
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

    private final OptionsBounds optionsBounds;

    // TODO refactor these constructors

    /**
     * Constructs an {@code Options} object by initializing it with the configuration
     * data loaded from a predefined JSON resource file. The configuration provides
     * the bounds necessary for setting up the {@code Options} screen in the GUI.
     *
     * @throws RuntimeException if the configuration file cannot be loaded or parsed.
     * @see Options
     */
    public Options() {
        this(ConfigLoader.loadConfig("/gui/options/optionsBounds.json", OptionsBounds.class));
    }

    // Options button
    private void addCloseOptionsButton() {
        final GuiButton optionsButton = new GuiButton(Layout.BTN_OPTIONS_X,
                Layout.BTN_OPTIONS_Y, Layout.BTN_OPTIONS_WIDTH,
                Layout.BTN_OPTIONS_HEIGHT, SpriteLoader.loadSprite(SingleSpriteKey.DEFAULT_SIGN_BUTTON).image());
        optionsButton.setOnClick(() -> throwEvent(new CloseOptionsEvent(this)));
        super.buttons.add(optionsButton);
    }

    private Options(final OptionsBounds optionsBounds) {
        super(optionsBounds.topLeftBound.x, optionsBounds.topLeftBound.y, optionsBounds.bottomRightBound.x - optionsBounds.topLeftBound.x, optionsBounds.bottomRightBound.y - optionsBounds.topLeftBound.y);
        this.optionsBounds = optionsBounds;
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

        addCloseOptionsButton();

        final double menuScreenWidth = optionsBounds.bottomRightBound.x * cs.getScale();
        final double menuScreenHeight = optionsBounds.bottomRightBound.y * cs.getScale();
        final double menuScreenX = cs.toScreenX(optionsBounds.topLeftBound.x);
        final double menuScreenY = cs.toScreenY(optionsBounds.topLeftBound.y);

        final Image menuBackground = SpriteLoader.loadSprite(SingleSpriteKey.MENU_BACKGROUND).image();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(menuBackground, menuScreenX, menuScreenY, menuScreenWidth, menuScreenHeight);
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
