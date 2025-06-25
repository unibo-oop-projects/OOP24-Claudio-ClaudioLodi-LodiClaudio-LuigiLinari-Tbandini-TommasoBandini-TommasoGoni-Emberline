package dev.emberline.gui.towerdialog;

import dev.emberline.core.GameLoop;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.world.buildings.towerPreBuild.TowerPreBuild;
import dev.emberline.gui.GuiButton;
import dev.emberline.gui.GuiLayer;
import dev.emberline.gui.event.NewBuildEvent;
import dev.emberline.gui.towerdialog.TextGuiButton.TextLayoutType;
import javafx.scene.canvas.GraphicsContext;

public class NewBuildDialogLayer extends GuiLayer {

    private static class Layout {
        // Background
        private static final double BG_WIDTH = 9.2;
        private static final double BG_HEIGHT = 5.84;
        private static final double BG_X = Renderer.GUICS_WIDTH * 0.98 - BG_WIDTH;
        private static final double BG_Y = 1.2;
        // Button
        private static final double BTN_HEIGHT = 1.8;
        private static final double BTN_WIDTH = 3.55;
        private static final double BTN_X = BG_X + (BG_WIDTH - BTN_WIDTH) / 2;
        private static final double BTN_Y = BG_Y + BG_HEIGHT - BTN_HEIGHT - 0.5;
    }

    // The Tower pre build linked to this dialog layer
    private final TowerPreBuild tower;
    private final GuiButton buildButton;

    public NewBuildDialogLayer(final TowerPreBuild tower) {
        super(Layout.BG_X, Layout.BG_Y, Layout.BG_WIDTH, Layout.BG_HEIGHT);
        this.tower = tower;
        buildButton = addBuildButton();
        buildButton.setOnClick(() -> throwEvent(new NewBuildEvent(buildButton, this.getTowerPreBuild())));
        super.buttons.add(buildButton);
    }

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

    @Override
    public void render() {
        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem guics = renderer.getGuiCoordinateSystem();

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI_HIGH, () -> {
            // Background
            Renderer.drawImage(SpriteLoader.loadSprite(SingleSpriteKey.NTDL_BACKGROUND).image(), gc, guics, Layout.BG_X, Layout.BG_Y, Layout.BG_WIDTH, Layout.BG_HEIGHT);
        }));

        if (buildButton != null) {
            buildButton.render();
        }

        super.render();
    }
}
