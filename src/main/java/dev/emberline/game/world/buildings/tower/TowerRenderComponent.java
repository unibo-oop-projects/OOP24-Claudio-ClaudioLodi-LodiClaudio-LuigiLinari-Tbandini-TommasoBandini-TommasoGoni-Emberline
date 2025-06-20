package dev.emberline.game.world.buildings.tower;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.TowerSpriteKey;
import dev.emberline.core.graphics.spritekeys.CrystalSpriteKey;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

class TowerRenderComponent implements Renderable {
    private final Tower tower;

    TowerRenderComponent(Tower tower) {
        this.tower = tower;
    }

    @Override
    public void render() {
        Image bodyImage = SpriteLoader.loadSprite(new TowerSpriteKey(tower.getProjectileInfo().type(), tower.getEnchantmentInfo().type())).image();
        // TODO AnimatedSprite crystalSprite = (AnimatedSprite) SpriteLoader.loadSprite(new CrystalSpriteKey());

        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        double topLeftScreenX = cs.toScreenX(tower.getWorldTopLeft().getX());
        double topLeftScreenY = cs.toScreenY(tower.getWorldTopLeft().getY());
        double screenWidth = cs.getScale() * tower.getWorldWidth();
        double screenHeight = cs.getScale() * tower.getWorldHeight();

        renderer.addRenderTask(new RenderTask(RenderPriority.BUILDINGS, () -> {
            gc.drawImage(bodyImage, topLeftScreenX, topLeftScreenY, screenWidth, screenHeight);
        }).enableZOrder(tower.getWorldBottomRight().getY()));
    }
}
