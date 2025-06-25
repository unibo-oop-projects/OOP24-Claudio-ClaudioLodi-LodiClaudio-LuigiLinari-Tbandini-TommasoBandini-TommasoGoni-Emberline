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
import dev.emberline.gui.event.SetMainMenuEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class GameOver extends GuiLayer implements GameState {

    private final GameOverBounds gameOverBounds;

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
        private static final double SCALE_FACTOR = 2.5;
        private static final double BTN_START_HEIGHT = 1.5 * SCALE_FACTOR;
        private static final double BTN_START_WIDTH = 3.5 * SCALE_FACTOR;
        private static final double BTN_START_X = (BG_WIDTH - BTN_START_WIDTH) / 2;
        private static final double BTN_START_Y = (BG_HEIGHT - BTN_START_HEIGHT) / 2;
        // GameOver Button
        private static final double BTN_OPTIONS_HEIGHT = 1.5 * SCALE_FACTOR;
        private static final double BTN_OPTIONS_WIDTH = 3.5 * SCALE_FACTOR;
        private static final double BTN_OPTIONS_X = (BG_WIDTH - BTN_OPTIONS_WIDTH) / 2;
        private static final double BTN_OPTIONS_Y = BTN_START_Y + BTN_START_HEIGHT - 0.25;
    }

    // GameOver bounds
    private record Coordinate(
            @JsonProperty int x,
            @JsonProperty int y
    ) {
    }

    private record GameOverBounds(
            @JsonProperty Coordinate topLeftBound,
            @JsonProperty Coordinate bottomRightBound
    ) {
    }

    // TODO refactor these constructors 
    public GameOver() {
        this(ConfigLoader.loadConfig("/gui/gameOver/gameOverBounds.json", GameOverBounds.class));
    }

    // GameOver button
    private void addMainMenuButton() {
        final GuiButton optionsButton = new GuiButton(Layout.BTN_OPTIONS_X,
                Layout.BTN_OPTIONS_Y, Layout.BTN_OPTIONS_WIDTH,
                Layout.BTN_OPTIONS_HEIGHT, SpriteLoader.loadSprite(SingleSpriteKey.DEFAULT_SIGN_BUTTON).image());
        optionsButton.setOnClick(() -> throwEvent(new SetMainMenuEvent(this)));
        super.buttons.add(optionsButton);
    }

    private GameOver(final GameOverBounds gameOverBounds) {
        super(gameOverBounds.topLeftBound.x, gameOverBounds.topLeftBound.y, gameOverBounds.bottomRightBound.x - gameOverBounds.topLeftBound.x, gameOverBounds.bottomRightBound.y - gameOverBounds.topLeftBound.y);
        this.gameOverBounds = gameOverBounds;
    }

    @Override
    public void render() {
        // Render background
        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem cs = renderer.getGuiCoordinateSystem();

        addMainMenuButton();

        final double menuScreenWidth = gameOverBounds.bottomRightBound.x * cs.getScale();
        final double menuScreenHeight = gameOverBounds.bottomRightBound.y * cs.getScale();
        final double menuScreenX = cs.toScreenX(gameOverBounds.topLeftBound.x);
        final double menuScreenY = cs.toScreenY(gameOverBounds.topLeftBound.y);

        final Image gameOverBackground = SpriteLoader.loadSprite(SingleSpriteKey.GAME_OVER_BACKGROUND).image();
        final Image gameOverImage = SpriteLoader.loadSprite(SingleSpriteKey.GAME_OVER).image();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(gameOverBackground, menuScreenX, menuScreenY, menuScreenWidth, menuScreenHeight);
            gc.drawImage(gameOverImage, cs.toScreenX(Layout.TITLE_X), cs.toScreenY(Layout.TITLE_Y), Layout.TITLE_WIDTH * cs.getScale(), Layout.TITLE_HEIGHT * cs.getScale());
        }));

        super.render();
    }

    @Override
    public void update(final long elapsed) {
    }

}
