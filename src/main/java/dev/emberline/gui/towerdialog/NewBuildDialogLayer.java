package dev.emberline.gui.towerdialog;

import dev.emberline.core.GameLoop;
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
    private final GuiButton buildButton;

    private static final class Layout {
        // Background
        private static final double BG_WIDTH = 9.2;
        private static final double BG_HEIGHT = 5.84;
        private static final double BG_X = Renderer.GUICS_WIDTH * 0.98 - BG_WIDTH;
        private static final double BG_Y = 1.5;
        // Button
        private static final double BTN_HEIGHT = 1.8;
        private static final double BTN_WIDTH = 3.55;
        private static final double BTN_X = BG_X + (BG_WIDTH - BTN_WIDTH) / 2;
        private static final double BTN_Y = BG_Y + BG_HEIGHT - BTN_HEIGHT - 0.5;
    }

    /**
     * Constructs a new instance of {@code NewBuildDialogLayer} linked to a specific
     * {@code TowerPreBuild}.
     *
     * @param tower the {@code TowerPreBuild} instance associated with this dialog layer
     * @see NewBuildDialogLayer
     */
    public NewBuildDialogLayer(final TowerPreBuild tower) {
        super(Layout.BG_X, Layout.BG_Y, Layout.BG_WIDTH, Layout.BG_HEIGHT);
        this.tower = tower;
        buildButton = addBuildButton();
        buildButton.setOnClick(() -> throwEvent(new NewBuildEvent(buildButton, this.getTowerPreBuild())));
        super.getButtons().add(buildButton);
    }

    /**
     * Returns the associated {@code TowerPreBuild} object linked to this dialog layer.
     *
     * @return the associated {@code TowerPreBuild} object linked to this dialog layer.
     */
    public TowerPreBuild getTowerPreBuild() {
        return tower;
    }

    private GuiButton addBuildButton() {
        return new PricingGuiButton(
            Layout.BTN_X, Layout.BTN_Y,
            Layout.BTN_WIDTH, Layout.BTN_HEIGHT,
            SpriteLoader.loadSprite(SingleSpriteKey.GENERIC_BUTTON).image(),
            -tower.getNewBuildCost(), TextLayoutType.CENTER
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem guics = renderer.getGuiCoordinateSystem();

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI_HIGH, () -> {
            // Background
            Renderer.drawImage(SpriteLoader.loadSprite(SingleSpriteKey.NTDL_BACKGROUND).image(),
                    gc, guics, Layout.BG_X, Layout.BG_Y, Layout.BG_WIDTH, Layout.BG_HEIGHT);
        }));

        super.render();
    }
}
