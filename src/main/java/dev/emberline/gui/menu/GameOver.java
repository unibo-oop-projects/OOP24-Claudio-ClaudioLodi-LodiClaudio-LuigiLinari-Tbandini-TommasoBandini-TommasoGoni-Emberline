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
import dev.emberline.gui.event.ExitGameEvent;
import dev.emberline.gui.event.SetMainMenuEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class GameOver extends GuiLayer implements GameState {

    private static class Layout {
        // Background
        private static final double BG_WIDTH = 32;
        private static final double BG_HEIGHT = 18;
        // Title
        private static final double TITLE_WIDTH = 17;
        private static final double TITLE_HEIGHT = 5;
        private static final double TITLE_X = (BG_WIDTH - TITLE_WIDTH) / 2;
        private static final double TITLE_Y = (BG_HEIGHT - TITLE_HEIGHT) / 2 - 3.5;
        // Menu Button
        private static final double SCALE_FACTOR = 1.7;
        private static final double BTN_MENU_HEIGHT = 1.5 * SCALE_FACTOR;
        private static final double BTN_MENU_WIDTH = 3.5 * SCALE_FACTOR;
        private static final double BTN_MENU_X = (BG_WIDTH - BTN_MENU_WIDTH) / 2;
        private static final double BTN_MENU_Y = TITLE_Y + TITLE_HEIGHT - 0.05 * SCALE_FACTOR;
        // Exit Button
        private static final double BTN_EXIT_HEIGHT = 1.5 * SCALE_FACTOR;
        private static final double BTN_EXIT_WIDTH = 3.5 * SCALE_FACTOR;
        private static final double BTN_EXIT_X = (BG_WIDTH - BTN_EXIT_WIDTH) / 2;
        private static final double BTN_EXIT_Y = BTN_MENU_Y + BTN_MENU_HEIGHT - 0.25;
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

    private final GameOverBounds gameOverBounds;

    // TODO refactor these constructors 
    public GameOver() {
        this(ConfigLoader.loadConfig("/gui/gameOver/gameOverBounds.json", GameOverBounds.class));
    }

    // Menu button
    private void addMainMenuButton() {
        final GuiButton menuButton = new GuiButton(Layout.BTN_MENU_X,
                Layout.BTN_MENU_Y, Layout.BTN_MENU_WIDTH, Layout.BTN_MENU_HEIGHT,
                SpriteLoader.loadSprite(SingleSpriteKey.MENU_SIGN_BUTTON).image(),
                SpriteLoader.loadSprite(SingleSpriteKey.MENU_SIGN_BUTTON_HOVER).image());
        menuButton.setOnClick(() -> throwEvent(new SetMainMenuEvent(this)));
        super.buttons.add(menuButton);
    }

    // Exit button
    private void addExitButton() {
        final GuiButton exitButton = new GuiButton(Layout.BTN_EXIT_X,
                Layout.BTN_EXIT_Y, Layout.BTN_EXIT_WIDTH, Layout.BTN_EXIT_HEIGHT,
                SpriteLoader.loadSprite(SingleSpriteKey.EXIT_SIGN_BUTTON).image(),
                SpriteLoader.loadSprite(SingleSpriteKey.EXIT_SIGN_BUTTON_HOVER).image());
        exitButton.setOnClick(() -> throwEvent(new ExitGameEvent(exitButton)));
        super.buttons.add(exitButton);
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
        addExitButton();

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
