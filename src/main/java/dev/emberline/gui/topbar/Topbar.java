package dev.emberline.gui.topbar;

import dev.emberline.core.GameLoop;
import dev.emberline.core.event.EventDispatcher;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import dev.emberline.core.graphics.spritekeys.StringSpriteKey;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.world.World;
import dev.emberline.gui.GuiButton;
import dev.emberline.gui.GuiLayer;
import dev.emberline.gui.event.OpenOptionsEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.EventListener;

/**
 * The {@code Topbar} class represents a {@link GuiLayer} for the top section of the screen
 * displaying player stats such as health, gold, and the current wave.
 * <p>
 * This class also provides an options button that allows users to access the options menu.
 */
public class Topbar extends GuiLayer implements EventListener {

    private int health;
    private int gold;
    private int wave;
    private final World world;

    private static final class Layout {
        // Background
        private static final double SCALE = 1.25;
        private static final double BG_WIDTH = 11.97 * SCALE;
        private static final double BG_HEIGHT = 1.33 * SCALE;
        private static final double BG_Y = 0;
        private static final double BG_X = 32 - BG_WIDTH;
        // Options Button
        private static final double SCALE_FACTOR = 0.95;
        private static final double BTN_OPTIONS_HEIGHT = 1 * SCALE_FACTOR;
        private static final double BTN_OPTIONS_WIDTH = 1 * SCALE_FACTOR;
        private static final double BTN_OPTIONS_X = BG_X + BG_WIDTH - BTN_OPTIONS_WIDTH * 2;
        private static final double BTN_OPTIONS_Y = BG_Y + (BG_HEIGHT - BTN_OPTIONS_HEIGHT) / 2;
        // Stats
        private static final double STARTX = BG_X + 1.3;
        private static final double ENDX = BTN_OPTIONS_X - 2.7;
        private static final double STATS_HEIGHT = 1;
        private static final double STATS_X_HEALT = STARTX;
        private static final double STATS_X_GOLD = STARTX + (ENDX - STARTX) / 2;
        private static final double STATS_X_WAVE = ENDX;
    }

    /**
     * Initializes a new instance of the {@code Topbar} class and registers it as an event listener.
     *
     * @param world the {@code World} instance associated with this Topbar,
     *              providing access to game state and necessary data.
     * @see Topbar
     */
    public Topbar(final World world) {
        super(Layout.BG_X, Layout.BG_Y, Layout.BG_WIDTH, Layout.BG_HEIGHT);
        this.world = world;
        EventDispatcher.getInstance().registerListener(this);
    }

    private void updateLayout() {
        addOptionsButton();
        addStatsImages();
    }

    private void addStatsImages() {
        health = world.getPlayer().getHealth();
        gold = world.getPlayer().getGold();
        wave = world.getWaveManager().getCurrentWaveIndex() + 1;

    }

    private void addOptionsButton() {
        final GuiButton optionsButton = new GuiButton(Layout.BTN_OPTIONS_X, Layout.BTN_OPTIONS_Y,
                Layout.BTN_OPTIONS_WIDTH, Layout.BTN_OPTIONS_HEIGHT,
                SpriteLoader.loadSprite(SingleSpriteKey.TOPBAR_OPTIONS_BUTTON).image(),
                SpriteLoader.loadSprite(SingleSpriteKey.TOPBAR_OPTIONS_BUTTON_HOVER).image());
        optionsButton.setOnClick(() -> throwEvent(new OpenOptionsEvent(optionsButton)));
        super.getButtons().add(optionsButton);
    }

    private void drawStats(final GraphicsContext gc, final CoordinateSystem cs,
                           final Image healtImage, final Image goldImage, final Image waveImage) {
        drawStatImage(gc, cs, healtImage, Layout.STATS_X_HEALT, Layout.STATS_HEIGHT);
        drawStatImage(gc, cs, goldImage, Layout.STATS_X_GOLD, Layout.STATS_HEIGHT);
        drawStatImage(gc, cs, waveImage, Layout.STATS_X_WAVE, Layout.STATS_HEIGHT);
    }

    private void drawStatImage(final GraphicsContext gc, final CoordinateSystem cs,
                               final Image img, final double x, final double baseHeight) {
        final double ratio = img.getWidth() / img.getHeight();
        final double targetHeight = baseHeight * 0.8;
        final double targetWidth = targetHeight * ratio;
        Renderer.drawImage(img, gc, cs, x, (Layout.BG_HEIGHT - targetHeight) / 2, targetWidth, targetHeight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem guics = renderer.getGuiCoordinateSystem();

        updateLayout();

        final Image healtImageString = SpriteLoader.loadSprite(new StringSpriteKey("♥: " + health)).image();
        final Image goldImageString = SpriteLoader.loadSprite(new StringSpriteKey("$: " + gold)).image();
        final Image waveImageString = SpriteLoader.loadSprite(new StringSpriteKey("☠: " + wave)).image();

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            // Background
            Renderer.drawImage(SpriteLoader.loadSprite(SingleSpriteKey.TOPBAR_BACKGROUND).image(),
                    gc, guics, Layout.BG_X, Layout.BG_Y, Layout.BG_WIDTH, Layout.BG_HEIGHT);
            // Stats
            drawStats(gc, guics, healtImageString, goldImageString, waveImageString);
        }));

        super.render();
    }
}
