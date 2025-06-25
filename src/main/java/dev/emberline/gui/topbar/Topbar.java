package dev.emberline.gui.topbar;

import dev.emberline.core.GameLoop;
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

public class Topbar extends GuiLayer {
    private int healt;
    private int gold;
    private int wave;
    private World world;
    private Image healtImageString;
    private Image goldImageString;
    private Image waveImageString;

    private static class Layout {
        // Background
        private static final double BG_WIDTH = 11.97;
        private static final double BG_HEIGHT = 1.33;
        private static final double BG_Y = 0;
        private static final double BG_X = 32 - BG_WIDTH;
        // Options Button
        private static final double scale_factor = 0.9;
        private static final double BTN_OPTIONS_HEIGHT = 1 * scale_factor;
        private static final double BTN_OPTIONS_WIDTH = 1 * scale_factor;
        private static final double BTN_OPTIONS_X = BG_X + BG_WIDTH - BTN_OPTIONS_WIDTH * 2;
        private static final double BTN_OPTIONS_Y = BG_Y + (BG_HEIGHT - BTN_OPTIONS_HEIGHT) / 2;
        // Stats
        private static final double STARTX = BG_X + 1;
        private static final double ENDX = BTN_OPTIONS_X - 2;
        private static final double STATS_HEIGHT = 1;
        private static final double STATS_X_HEALT = STARTX;
        private static final double STATS_X_GOLD = STARTX + (ENDX - STARTX) / 2;
        private static final double STATS_X_WAVE = ENDX;
    }

    public Topbar(World world) {
        this(Layout.BG_X, Layout.BG_Y, Layout.BG_WIDTH, Layout.BG_HEIGHT, world);
    }

    protected Topbar(double x, double y, double width, double height, World world) {
        super(Layout.BG_X, Layout.BG_Y, Layout.BG_WIDTH, Layout.BG_HEIGHT);
        this.world = world;
        updateLayout();
    }

    private void updateLayout() {
        addOptionsButton();
        addStatsImages();
    }

    private void addStatsImages() {
        healt = world.getPlayer().getHealt();
        gold = world.getPlayer().getGold();
        wave = world.getWaveManager().getCurrentWaveIndex() + 1;
        healtImageString = SpriteLoader.loadSprite(new StringSpriteKey("♥:" + healt)).image();
        goldImageString = SpriteLoader.loadSprite(new StringSpriteKey("$:" + gold)).image();
        waveImageString = SpriteLoader.loadSprite(new StringSpriteKey("☠:" + wave)).image();
    }

    private void addOptionsButton() {
        GuiButton optionsButton = new GuiButton(Layout.BTN_OPTIONS_X, Layout.BTN_OPTIONS_Y,
                Layout.BTN_OPTIONS_WIDTH, Layout.BTN_OPTIONS_HEIGHT,
                SpriteLoader.loadSprite(SingleSpriteKey.TOPBAR_OPTIONS_BUTTON).image());
        optionsButton.setOnClick(() -> {
            throwEvent(new OpenOptionsEvent(optionsButton));
        });
        super.buttons.add(optionsButton);
    }

    private void drawStats(GraphicsContext gc, CoordinateSystem cs, Image healtImage, Image goldImage, Image waveImage) {
        drawStatImage(gc, cs, healtImage, Layout.STATS_X_HEALT, Layout.STATS_HEIGHT);
        drawStatImage(gc, cs, goldImage, Layout.STATS_X_GOLD, Layout.STATS_HEIGHT);
        drawStatImage(gc, cs, waveImage, Layout.STATS_X_WAVE, Layout.STATS_HEIGHT);
    }

    private void drawStatImage(GraphicsContext gc, CoordinateSystem cs, Image img, double x, double baseHeight) {
        double ratio = img.getWidth() / img.getHeight();
        double targetHeight = baseHeight * 0.65; // Scale down a bit for better appearance
        double targetWidth = targetHeight * ratio;
        Renderer.drawImage(img, gc, cs, x, (Layout.BG_HEIGHT - targetHeight) / 2, targetWidth, targetHeight);
    }

    @Override
    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem guics = renderer.getGuiCoordinateSystem();

        updateLayout();

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            // Background
            Renderer.drawImage(SpriteLoader.loadSprite(SingleSpriteKey.TOPBAR_BACKGROUND).image(), gc, guics, Layout.BG_X, Layout.BG_Y, Layout.BG_WIDTH, Layout.BG_HEIGHT);
            // Stats        
            drawStats(gc, guics, healtImageString, goldImageString, waveImageString);
        }));

        super.render();
    }
}
