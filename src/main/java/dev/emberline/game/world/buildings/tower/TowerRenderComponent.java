package dev.emberline.game.world.buildings.tower;

import dev.emberline.core.ConfigLoader;
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

    private static final double CRYSTAL_WIDTH = ConfigLoader.loadNode("/sprites/towerAssets/crystal.json").get("worldDimensions").get("width").asDouble();
    private static final double CRYSTAL_HEIGHT = ConfigLoader.loadNode("/sprites/towerAssets/crystal.json").get("worldDimensions").get("width").asDouble();
    private static final double CRYSTAL_SWING_PERIOD_NS = ConfigLoader.loadNode("/sprites/towerAssets/crystal.json").get("swingPeriodNs").asDouble();
    private static final double CRYSTAL_SWING_AMPLITUDE = ConfigLoader.loadNode("/sprites/towerAssets/crystal.json").get("swingAmplitude").asDouble();


    private long creationTimeNs = System.nanoTime();

    TowerRenderComponent(Tower tower) {
        this.tower = tower;
    }

    @Override
    public void render() {
        Image bodyImage = SpriteLoader.loadSprite(new TowerSpriteKey(tower.getProjectileInfo().type(), tower.getEnchantmentInfo().type())).image();
        AnimatedSprite crystalSprite = (AnimatedSprite) SpriteLoader.loadSprite(new CrystalSpriteKey(tower.getEnchantmentInfo().type()));

        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        int currentFrame = (int) ((System.nanoTime() - creationTimeNs) / crystalSprite.getFrameTimeNs()) % crystalSprite.getFrameCount();
        Image crystalImage = crystalSprite.image(currentFrame);

        double crystalSwingOffset = Math.sin((System.nanoTime() - creationTimeNs) * 2 * Math.PI * 1./CRYSTAL_SWING_PERIOD_NS) * CRYSTAL_SWING_AMPLITUDE * cs.getScale();

        double topLeftScreenX = cs.toScreenX(tower.getWorldTopLeft().getX());
        double topLeftScreenY = cs.toScreenY(tower.getWorldTopLeft().getY());
        double screenWidth = cs.getScale() * tower.getWorldWidth();
        double screenHeight = cs.getScale() * tower.getWorldHeight();

        double firingWorldCenterX = tower.firingWorldCenterLocation().getX();
        double firingWorldCenterY = tower.firingWorldCenterLocation().getY();

        double crystalScreenX = cs.toScreenX(firingWorldCenterX - CRYSTAL_WIDTH / 2);
        double crystalScreenY = cs.toScreenY(firingWorldCenterY - CRYSTAL_HEIGHT / 2) + crystalSwingOffset;

        renderer.addRenderTask(new RenderTask(RenderPriority.BUILDINGS, () -> {
            gc.drawImage(bodyImage, topLeftScreenX, topLeftScreenY, screenWidth, screenHeight);
            gc.drawImage(crystalImage, crystalScreenX, crystalScreenY, CRYSTAL_WIDTH * cs.getScale(), CRYSTAL_HEIGHT * cs.getScale());
        }).enableZOrder(tower.getWorldBottomRight().getY()));
    }
}
