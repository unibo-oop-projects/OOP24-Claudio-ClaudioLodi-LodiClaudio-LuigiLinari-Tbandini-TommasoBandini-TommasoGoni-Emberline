package dev.emberline.gui.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.GameLoop;
import dev.emberline.core.config.ConfigLoader;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import dev.emberline.core.graphics.spritekeys.StringSpriteKey;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.GameState;
import dev.emberline.game.world.statistics.Statistics;
import dev.emberline.gui.GuiButton;
import dev.emberline.gui.GuiLayer;
import dev.emberline.gui.event.ExitGameEvent;
import dev.emberline.gui.event.SetMainMenuEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;

/**
 * Represents the "Game Over" state of the game. This class functions as a GUI layer
 * and implements the {@link GameState} interface to define behavior specific to when
 * the game has concluded.
 * <p>
 * The screen includes static graphics such as a background image and a "Game Over" title,
 * as well as interactive components like buttons for navigating to other game states.
 * <p>
 * It is responsible for triggering appropriate events for game state transitions
 * (e.g., exiting the game, returning to the main menu).
 */
public class GameOver extends GuiLayer implements GameState {

    private final GameOverBounds gameOverBounds;
    private Statistics statistics;

    private static class Layout {
        private static final double SCALE_FACTOR = 1.7;
        // Background
        private static final double BG_WIDTH = 32;
        private static final double BG_HEIGHT = 18;
        // Title
        private static final double TITLE_WIDTH = 17;
        private static final double TITLE_HEIGHT = 5;
        private static final double TITLE_X = (BG_WIDTH - TITLE_WIDTH) / 2;
        private static final double TITLE_Y = (BG_HEIGHT - TITLE_HEIGHT) / 2 - 3.5;
        // Statistics
        private static final double STATISTICS_REAL_HEIGHT = 116;
        private static final double STATISTICS_REAL_WIDTH = 141;
        private static final double STATISTICS_HEIGHT = 7;
        private static final double STATISTICS_WIDTH =  STATISTICS_HEIGHT * (STATISTICS_REAL_WIDTH / STATISTICS_REAL_HEIGHT);
        private static final double STATISTICS_X = (BG_WIDTH - STATISTICS_WIDTH) / 2.8 - 0.07;
        private static final double STATISTICS_Y = TITLE_Y + TITLE_HEIGHT - 0.05 * SCALE_FACTOR;

        private static final double STATISTICS_MAX_LABEL_WIDTH = STATISTICS_WIDTH / 2;
        private static final double STATISTICS_MAX_LABEL_HEIGHT = 1.5;
        private static final double STATISTICS_MAX_VALUE_WIDTH = 2.3;
        private static final double STATISTICS_MAX_VALUE_HEIGHT = 0.35;
        private static final double STATISTICS_LABEL_X = STATISTICS_X + STATISTICS_WIDTH / 10;
        private static final double STATISTICS_VALUE_X = STATISTICS_LABEL_X + STATISTICS_MAX_LABEL_WIDTH + 0.5;

        private static final double STATISTICS_Y_START = STATISTICS_Y * 1.39;
        private static final double STATISTICS_ROW_HEIGHT = STATISTICS_MAX_VALUE_HEIGHT * 3.0;

        // Menu Button
        private static final double BTN_REAL_HEIGHT = 48;
        private static final double BTN_REAL_WIDTH = 112;
        private static final double WITDH_RATIO = BTN_REAL_WIDTH / STATISTICS_REAL_WIDTH;
        private static final double HEIGHT_RATIO = BTN_REAL_HEIGHT / STATISTICS_REAL_HEIGHT;
        private static final double BTN_MENU_WIDTH = STATISTICS_WIDTH * WITDH_RATIO;
        private static final double BTN_MENU_HEIGHT = STATISTICS_HEIGHT * HEIGHT_RATIO;
        private static final double BTN_MENU_X = (BG_WIDTH - BTN_MENU_WIDTH) / 1.5 + 0.07;
        private static final double BTN_MENU_Y = TITLE_Y + TITLE_HEIGHT - 0.05 * SCALE_FACTOR;
        // Exit Button
        private static final double BTN_EXIT_HEIGHT = BTN_MENU_HEIGHT;
        private static final double BTN_EXIT_WIDTH = BTN_MENU_WIDTH;
        private static final double BTN_EXIT_X = BTN_MENU_X;
        private static final double BTN_EXIT_Y = BTN_MENU_Y + BTN_MENU_HEIGHT - 0.25;
    }

    private static final class Colors {
        private static final ColorAdjust OPTIONS_WRITINGS = new ColorAdjust(0.15, 0.9, 0.5, 0);
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

    /**
     * Constructs an instance of the {@code GameOver} class using bounds defined in a configuration file.
     *
     * @throws RuntimeException if the configuration file cannot be loaded or deserialized.
     * @see GameOver
     */
    public GameOver() {
        this(ConfigLoader.loadConfig("/gui/gameOver/gameOverBounds.json", GameOverBounds.class));
    }

    /**
     * Sets the statistics for the game over screen.
     *
     * @param statistics the {@link Statistics} object containing game statistics.
     * @throws IllegalArgumentException if the provided statistics are null.
     */
    public void setStatistics(final Statistics statistics) {
        if (statistics == null) {
            throw new IllegalArgumentException("Statistics cannot be null");
        }
        this.statistics = statistics;
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

    private void drawStringImage(final GraphicsContext gc, final CoordinateSystem cs, final Image img, 
                              final double x, final double y, final double maxWidth, final double maxHeight) {
        if (img == null) return;
        
        final double ratio = img.getWidth() / img.getHeight();
        double targetWidth = maxWidth;
        double targetHeight = maxHeight;
        
        // Adjust targetWidth and targetHeight to maintain aspect ratio
        if (targetWidth / targetHeight > ratio) {
            targetWidth = targetHeight * ratio;
        } else {
            targetHeight = targetWidth / ratio;
        }

        double adjustedY = y - targetHeight;  // To allign to the bottom of the image

        Renderer.drawImage(img, gc, cs, x, adjustedY, targetWidth, targetHeight);
    }

    private void drawStatisticText(final GraphicsContext gc, final CoordinateSystem cs) {
        gc.save();
        gc.setEffect(Colors.OPTIONS_WRITINGS);
        
        final int enemiesKilled = statistics.getEnemiesKilled();
        final int wavesSurvived = statistics.getWavesSurvived();
        final double totalDamage = statistics.getTotalDamage();
        final double timeInGame = statistics.getTimeInGame() / 1_000_000_000.0; 
            
        int hours = (int) timeInGame / 3600;
        int minutes = (int) (timeInGame % 3600) / 60;
        int seconds = (int) timeInGame % 60;

        String timeInGameFormatted = hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
        
        final Image enemiesKilledLabel = SpriteLoader.loadSprite(new StringSpriteKey("Enemies killed:")).image();
        final Image wavesSurvivedLabel = SpriteLoader.loadSprite(new StringSpriteKey("Waves survived:")).image();
        final Image totalDamageLabel = SpriteLoader.loadSprite(new StringSpriteKey("Total damage dealt:")).image();
        final Image timeInGameLabel = SpriteLoader.loadSprite(new StringSpriteKey("Time in game:")).image();

        final Image enemiesKilledValue = SpriteLoader.loadSprite(new StringSpriteKey("" + enemiesKilled)).image();
        final Image wavesSurvivedValue = SpriteLoader.loadSprite(new StringSpriteKey("" + wavesSurvived)).image();
        final Image totalDamageValue = SpriteLoader.loadSprite(new StringSpriteKey(String.format("%.2f", totalDamage))).image();
        final Image timeInGameValue = SpriteLoader.loadSprite(new StringSpriteKey(timeInGameFormatted)).image();

        // Row 1: Enemies killed
        double currentY = Layout.STATISTICS_Y_START;
        drawStringImage(gc, cs, enemiesKilledLabel, Layout.STATISTICS_LABEL_X, currentY, Layout.STATISTICS_MAX_LABEL_WIDTH, Layout.STATISTICS_MAX_LABEL_HEIGHT);
        drawStringImage(gc, cs, enemiesKilledValue, Layout.STATISTICS_VALUE_X, currentY, Layout.STATISTICS_MAX_VALUE_WIDTH, Layout.STATISTICS_MAX_VALUE_HEIGHT);

        // Row 2: Waves survived
        currentY += Layout.STATISTICS_ROW_HEIGHT;
        drawStringImage(gc, cs, wavesSurvivedLabel, Layout.STATISTICS_LABEL_X, currentY, Layout.STATISTICS_MAX_LABEL_WIDTH, Layout.STATISTICS_MAX_LABEL_HEIGHT);
        drawStringImage(gc, cs, wavesSurvivedValue, Layout.STATISTICS_VALUE_X, currentY, Layout.STATISTICS_MAX_VALUE_WIDTH, Layout.STATISTICS_MAX_VALUE_HEIGHT);

        
        // Row 3: Total damage dealt
        currentY += Layout.STATISTICS_ROW_HEIGHT;
        drawStringImage(gc, cs, totalDamageLabel, Layout.STATISTICS_LABEL_X, currentY, Layout.STATISTICS_MAX_LABEL_WIDTH, Layout.STATISTICS_MAX_LABEL_HEIGHT);
        drawStringImage(gc, cs, totalDamageValue, Layout.STATISTICS_VALUE_X, currentY, Layout.STATISTICS_MAX_VALUE_WIDTH, Layout.STATISTICS_MAX_VALUE_HEIGHT);
        
        // Row 4: Time in game
        currentY += Layout.STATISTICS_ROW_HEIGHT;
        drawStringImage(gc, cs, timeInGameLabel, Layout.STATISTICS_LABEL_X, currentY, Layout.STATISTICS_MAX_LABEL_WIDTH, Layout.STATISTICS_MAX_LABEL_HEIGHT);
        drawStringImage(gc, cs, timeInGameValue, Layout.STATISTICS_VALUE_X, currentY, Layout.STATISTICS_MAX_VALUE_WIDTH, Layout.STATISTICS_MAX_VALUE_HEIGHT);

        gc.restore();
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

        addMainMenuButton();
        addExitButton();

        final double menuScreenWidth = gameOverBounds.bottomRightBound.x * cs.getScale();
        final double menuScreenHeight = gameOverBounds.bottomRightBound.y * cs.getScale();
        final double menuScreenX = cs.toScreenX(gameOverBounds.topLeftBound.x);
        final double menuScreenY = cs.toScreenY(gameOverBounds.topLeftBound.y);

        final Image gameOverBackground = SpriteLoader.loadSprite(SingleSpriteKey.GAME_OVER_BACKGROUND).image();
        final Image gameOverImage = SpriteLoader.loadSprite(SingleSpriteKey.GAME_OVER).image();
        final Image statisticsImage = SpriteLoader.loadSprite(SingleSpriteKey.STATISTICS).image();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(gameOverBackground, menuScreenX, menuScreenY, menuScreenWidth, menuScreenHeight);
        }));

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            gc.drawImage(gameOverImage, cs.toScreenX(Layout.TITLE_X), cs.toScreenY(Layout.TITLE_Y), Layout.TITLE_WIDTH * cs.getScale(), Layout.TITLE_HEIGHT * cs.getScale());
            gc.drawImage(statisticsImage, cs.toScreenX(Layout.STATISTICS_X), cs.toScreenY(Layout.STATISTICS_Y), Layout.STATISTICS_WIDTH * cs.getScale(), Layout.STATISTICS_HEIGHT * cs.getScale());
            drawStatisticText(gc, cs);
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
