package dev.emberline.gui.towerdialog;

import dev.emberline.core.GameLoop;
import dev.emberline.core.config.ConfigLoader;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.world.buildings.towerprebuild.TowerPreBuild;
import dev.emberline.gui.GuiButton;
import dev.emberline.gui.GuiLayer;
import dev.emberline.gui.event.NewBuildEvent;
import dev.emberline.gui.towerdialog.TextGuiButton.TextLayoutType;
import javafx.scene.canvas.GraphicsContext;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a dialog layer in the graphical user interface for initiating
 * the construction of a new tower. This dialog provides a build button to trigger
 * the construction process.
 * <p>
 * The {@code NewBuildDialogLayer} is linked to a specific {@code TowerPreBuild}
 * instance, which holds the necessary information related to the tower being
 * constructed. The dialog layer renders the background and the build button,
 * and dispatches a build event when the button is clicked.
 */
public class NewBuildDialogLayer extends GuiLayer {
    // The Tower pre build linked to this dialog layer
    private final TowerPreBuild tower;
    private static final Layout layout = ConfigLoader.loadConfig("/sprites/ui/newBuildDialogLayer.json", Layout.class);

    private record Layout(
            @JsonProperty
            double bgWidth,
            @JsonProperty
            double bgHeight,
            @JsonProperty
            double bgX,
            @JsonProperty
            double bgY,
            @JsonProperty
            double btnWidth,
            @JsonProperty
            double btnHeight,
            @JsonProperty
            double btnX,
            @JsonProperty
            double btnY
    ) {
    }

    /**
     * Constructs a new instance of {@code NewBuildDialogLayer} linked to a specific
     * {@code TowerPreBuild}.
     *
     * @param tower the {@code TowerPreBuild} instance associated with this dialog layer
     * @see NewBuildDialogLayer
     */
    public NewBuildDialogLayer(final TowerPreBuild tower) {
        super(layout.bgX, layout.bgY, layout.bgWidth, layout.bgHeight);
        this.tower = tower;
    }

    /**
     * Returns the associated {@code TowerPreBuild} object linked to this dialog layer.
     *
     * @return the associated {@code TowerPreBuild} object linked to this dialog layer.
     */
    public final TowerPreBuild getTowerPreBuild() {
        return tower;
    }

    private void addBuildButton() {
        GuiButton buildButton = new PricingGuiButton(
            layout.btnX, layout.btnY,
            layout.btnWidth, layout.btnHeight,
            SpriteLoader.loadSprite(SingleSpriteKey.GENERIC_BUTTON).image(),
            -tower.getNewBuildCost(), TextLayoutType.CENTER
        );
        buildButton.setOnClick(() -> throwEvent(new NewBuildEvent(buildButton, this.getTowerPreBuild())));
        super.getButtons().add(buildButton);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem guics = renderer.getGuiCoordinateSystem();

        addBuildButton();

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI_HIGH, () -> {
            // Background
            Renderer.drawImage(SpriteLoader.loadSprite(SingleSpriteKey.NTDL_BACKGROUND).image(),
                    gc, guics, layout.bgX, layout.bgY, layout.bgWidth, layout.bgHeight);
        }));

        super.render();
    }
}
