package dev.emberline.gui.topbar;

import dev.emberline.core.GameLoop;
import dev.emberline.core.config.ConfigLoader;
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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    private static final Layout LAYOUT = ConfigLoader.loadConfig("/gui/topbar/topbarLayout.json", Layout.class);

    private record Layout(
            @JsonProperty
            double bgWidth,
            @JsonProperty
            double bgHeight,
            @JsonProperty
            double bgY,
            @JsonProperty
            double bgX,
            @JsonProperty
            double btnOptionsHeight,
            @JsonProperty
            double btnOptionsWidth,
            @JsonProperty
            double btnOptionsX,
            @JsonProperty
            double btnOptionsY,
            @JsonProperty
            double statsHeight,
            @JsonProperty
            double statsXHealth,
            @JsonProperty
            double statsXGold,
            @JsonProperty
            double statsXWave
    ) {
    }

    /**
     * Initializes a new instance of the {@code Topbar} class and registers it as an event listener.
     *
     * @param world the {@code World} instance associated with this Topbar,
     *              providing access to game state and necessary data.
     * @see Topbar
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",   // May expose internal representation by
                                        // storing an externally mutable object into Renderer.canvas
            justification = "This is intended behavior,"
                    + " as this class needs a reference to world to get the stats it is supposed to show."
    )
    public Topbar(final World world) {
        super(LAYOUT.bgX, LAYOUT.bgY, LAYOUT.bgWidth, LAYOUT.bgHeight);

        registerEvents();
        this.world = world;
    }

    private void registerEvents() {
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
        final GuiButton optionsButton = new GuiButton(LAYOUT.btnOptionsX, LAYOUT.btnOptionsY,
                LAYOUT.btnOptionsWidth, LAYOUT.btnOptionsHeight,
                SpriteLoader.loadSprite(SingleSpriteKey.TOPBAR_OPTIONS_BUTTON).image(),
                SpriteLoader.loadSprite(SingleSpriteKey.TOPBAR_OPTIONS_BUTTON_HOVER).image());
        optionsButton.setOnClick(() -> throwEvent(new OpenOptionsEvent(optionsButton)));
        super.getButtons().add(optionsButton);
    }

    private void drawStats(final GraphicsContext gc, final CoordinateSystem cs,
                           final Image healtImage, final Image goldImage, final Image waveImage) {
        drawStatImage(gc, cs, healtImage, LAYOUT.statsXHealth, LAYOUT.statsHeight);
        drawStatImage(gc, cs, goldImage, LAYOUT.statsXGold, LAYOUT.statsHeight);
        drawStatImage(gc, cs, waveImage, LAYOUT.statsXWave, LAYOUT.statsHeight);
    }

    private void drawStatImage(final GraphicsContext gc, final CoordinateSystem cs,
                               final Image img, final double x, final double baseHeight) {
        final double ratio = img.getWidth() / img.getHeight();
        final double targetHeight = baseHeight * 0.8;
        final double targetWidth = targetHeight * ratio;
        Renderer.drawImage(img, gc, cs, x, (LAYOUT.bgHeight - targetHeight) / 2, targetWidth, targetHeight);
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

        final Image healthImageString = SpriteLoader.loadSprite(new StringSpriteKey("♥: " + health)).image();
        final Image goldImageString = SpriteLoader.loadSprite(new StringSpriteKey("$: " + gold)).image();
        final Image waveImageString = SpriteLoader.loadSprite(new StringSpriteKey("☠: " + wave)).image();

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            // Background
            Renderer.drawImage(SpriteLoader.loadSprite(SingleSpriteKey.TOPBAR_BACKGROUND).image(),
                    gc, guics, LAYOUT.bgX, LAYOUT.bgY, LAYOUT.bgWidth, LAYOUT.bgHeight);
            // Stats
            drawStats(gc, guics, healthImageString, goldImageString, waveImageString);
        }));

        super.render();
    }

    // TODO when TopBar will be Sereializable
    /*
    @Serial
    private void readObject(final ObjectInputStream e) throws IOException, ClassNotFoundException {
        e.defaultReadObject();
        registerEvents();
    }
    */
}
