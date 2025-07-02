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
public final class GameOver extends GuiLayer implements GameState {

    private final GameOverBounds bounds;
    private final Layout layout;
    private Statistics statistics;
    private static final ColorAdjust OPTIONS_TEXT_COLOR = new ColorAdjust(0.15, 0.9, 0.5, 0);

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
            double statisticsBgHeight,
            @JsonProperty 
            double statisticsBgWidth,
            @JsonProperty 
            double statisticsBgX,
            @JsonProperty 
            double statisticsBgY,
            @JsonProperty 
            double statisticsMaxLabelWidth,
            @JsonProperty 
            double statisticsMaxLabelHeight,
            @JsonProperty 
            double statisticsMaxValueWidth,
            @JsonProperty 
            double statisticsMaxValueHeight,
            @JsonProperty 
            double statisticsLabelX,
            @JsonProperty 
            double statisticsValueX,
            @JsonProperty 
            double statisticsFirstRowY,
            @JsonProperty 
            double statisticsSecondRowY,
            @JsonProperty 
            double statisticsThirdRowY,
            @JsonProperty 
            double statisticsFourthRowY,
            @JsonProperty 
            double navBtnHeight,
            @JsonProperty 
            double navBtnWidth,
            @JsonProperty 
            double navBtnX,
            @JsonProperty 
            double btnMenuY,
            @JsonProperty 
            double btnExitY
    ) {
    }

    private record GameOverBounds(
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
        private GameOverBounds {
            if (topLeftX >= bottomRightX || topLeftY >= bottomRightY) {
                throw new IllegalArgumentException("Invalid game over bounds: " + this);
            }
        }
    }

    /**
     * Constructs an instance of the {@code GameOver} class using bounds defined in a configuration file.
     *
     * @throws RuntimeException if the configuration file cannot be loaded or deserialized.
     * @see GameOver
     */
    public GameOver() {
        this(ConfigLoader.loadConfig("/gui/guiBounds.json", GameOverBounds.class),
             ConfigLoader.loadConfig("/gui/gameOver/gameOverLayout.json", Layout.class));
    }

    private GameOver(final GameOverBounds bounds, final Layout layout) {
        super(bounds.topLeftX, bounds.topLeftY, 
             bounds.bottomRightX - bounds.topLeftX,
             bounds.bottomRightY - bounds.topLeftY);
        this.bounds = bounds;
        this.layout = layout;
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
        final GuiButton menuButton = new GuiButton(layout.navBtnX,
                layout.btnMenuY, layout.navBtnWidth, layout.navBtnHeight,
                SpriteLoader.loadSprite(SingleSpriteKey.MENU_SIGN_BUTTON).image(),
                SpriteLoader.loadSprite(SingleSpriteKey.MENU_SIGN_BUTTON_HOVER).image());
        menuButton.setOnClick(() -> throwEvent(new SetMainMenuEvent(this)));
        super.getButtons().add(menuButton);
    }

    // Exit button
    private void addExitButton() {
        final GuiButton exitButton = new GuiButton(layout.navBtnX,
                layout.btnExitY, layout.navBtnWidth, layout.navBtnHeight,
                SpriteLoader.loadSprite(SingleSpriteKey.EXIT_SIGN_BUTTON).image(),
                SpriteLoader.loadSprite(SingleSpriteKey.EXIT_SIGN_BUTTON_HOVER).image());
        exitButton.setOnClick(() -> throwEvent(new ExitGameEvent(exitButton)));
        super.getButtons().add(exitButton);
    }

    private void drawStringImage(final GraphicsContext gc, final CoordinateSystem cs, final Image img, 
                              final double x, final double y, final double maxWidth, final double maxHeight) {

        final double ratio = img.getWidth() / img.getHeight();
        double targetWidth = maxWidth;
        double targetHeight = maxHeight;

        // Adjust targetWidth and targetHeight to maintain aspect ratio
        if (targetWidth / targetHeight > ratio) {
            targetWidth = targetHeight * ratio;
        } else {
            targetHeight = targetWidth / ratio;
        }

        final double adjustedY = y - targetHeight;  // To allign text at the bottom of the image

        Renderer.drawImage(img, gc, cs, x, adjustedY, targetWidth, targetHeight);
    }

    private void drawStatisticsText(final GraphicsContext gc, final CoordinateSystem cs) {
        gc.save();
        gc.setEffect(OPTIONS_TEXT_COLOR);

        final int enemiesKilled = statistics.getEnemiesFought();
        final int wavesSurvived = statistics.getWavesSurvived();
        final double totalDamage = statistics.getTotalDamage();
        final int timeInGame = (int) (statistics.getTimeInGame() / 1_000_000_000.0);    // Convert nanoseconds to seconds

        final int secondsInHour = 3600;
        final int secondsInMinute = 60;
        final int hours = timeInGame / secondsInHour;
        final int minutes = timeInGame % secondsInHour / secondsInMinute;
        final int seconds = timeInGame % secondsInMinute;

        final String timeInGameFormatted = hours > 0
                ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);

        final Image enemiesKilledLabel = SpriteLoader.loadSprite(new StringSpriteKey("Enemies killed:")).image();
        final Image wavesSurvivedLabel = SpriteLoader.loadSprite(new StringSpriteKey("Waves survived:")).image();
        final Image totalDamageLabel = SpriteLoader.loadSprite(new StringSpriteKey("Tot tower damage:")).image();
        final Image timeInGameLabel = SpriteLoader.loadSprite(new StringSpriteKey("Time in game:")).image();

        final Image enemiesKilledValue = SpriteLoader.loadSprite(new StringSpriteKey(Integer.toString(enemiesKilled))).image();
        final Image wavesSurvivedValue = SpriteLoader.loadSprite(new StringSpriteKey(Integer.toString(wavesSurvived))).image();
        final Image totalDamageValue = SpriteLoader.loadSprite(new StringSpriteKey(String.format("%.2f", totalDamage))).image();
        final Image timeInGameValue = SpriteLoader.loadSprite(new StringSpriteKey(timeInGameFormatted)).image();

        // Row 1: Enemies killed
        drawStringImage(gc, cs, enemiesKilledLabel, layout.statisticsLabelX, layout.statisticsFirstRowY,
                layout.statisticsMaxLabelWidth, layout.statisticsMaxLabelHeight);
        drawStringImage(gc, cs, enemiesKilledValue, layout.statisticsValueX, layout.statisticsFirstRowY,
                layout.statisticsMaxValueWidth, layout.statisticsMaxValueHeight);

        // Row 2: Waves survived
        drawStringImage(gc, cs, wavesSurvivedLabel, layout.statisticsLabelX, layout.statisticsSecondRowY,
                layout.statisticsMaxLabelWidth, layout.statisticsMaxLabelHeight);
        drawStringImage(gc, cs, wavesSurvivedValue, layout.statisticsValueX, layout.statisticsSecondRowY,
                layout.statisticsMaxValueWidth, layout.statisticsMaxValueHeight);

        // Row 3: Total damage dealt
        drawStringImage(gc, cs, totalDamageLabel, layout.statisticsLabelX, layout.statisticsThirdRowY,
                layout.statisticsMaxLabelWidth, layout.statisticsMaxLabelHeight);
        drawStringImage(gc, cs, totalDamageValue, layout.statisticsValueX, layout.statisticsThirdRowY,
                layout.statisticsMaxValueWidth, layout.statisticsMaxValueHeight);

        // Row 4: Time in game
        drawStringImage(gc, cs, timeInGameLabel, layout.statisticsLabelX, layout.statisticsFourthRowY,
                layout.statisticsMaxLabelWidth, layout.statisticsMaxLabelHeight);
        drawStringImage(gc, cs, timeInGameValue, layout.statisticsValueX, layout.statisticsFourthRowY,
                layout.statisticsMaxValueWidth, layout.statisticsMaxValueHeight);

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

        final double gameOverScreenWidth = (bounds.bottomRightX - bounds.topLeftX) * cs.getScale();
        final double gameOverScreenHeight = (bounds.bottomRightY - bounds.topLeftY) * cs.getScale();
        final double gameOverScreenX = cs.toScreenX(bounds.topLeftX);
        final double gameOverScreenY = cs.toScreenY(bounds.topLeftY);

        final Image gameOverBackground = SpriteLoader.loadSprite(SingleSpriteKey.GAME_OVER_BACKGROUND).image();
        final Image gameOverImage = SpriteLoader.loadSprite(SingleSpriteKey.GAME_OVER).image();
        final Image statisticsImage = SpriteLoader.loadSprite(SingleSpriteKey.STATISTICS).image();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(gameOverBackground, gameOverScreenX, gameOverScreenY, gameOverScreenWidth, gameOverScreenHeight);
        }));

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            gc.drawImage(gameOverImage, cs.toScreenX(layout.titleX), cs.toScreenY(layout.titleY),
                    layout.titleWidth * cs.getScale(), layout.titleHeight * cs.getScale());
            gc.drawImage(statisticsImage, cs.toScreenX(layout.statisticsBgX), cs.toScreenY(layout.statisticsBgY),
                    layout.statisticsBgWidth * cs.getScale(), layout.statisticsBgHeight * cs.getScale());
            drawStatisticsText(gc, cs);
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
