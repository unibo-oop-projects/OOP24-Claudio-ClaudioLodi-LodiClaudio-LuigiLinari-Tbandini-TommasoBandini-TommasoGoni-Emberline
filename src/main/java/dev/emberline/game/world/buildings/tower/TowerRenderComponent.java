package dev.emberline.game.world.buildings.tower;

import com.fasterxml.jackson.databind.JsonNode;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.CrystalSpriteKey;
import dev.emberline.core.graphics.spritekeys.TowerSpriteKey;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

class TowerRenderComponent implements Renderable {
    private static final JsonNode configsNode = ConfigLoader.loadNode("/sprites/towerAssets/crystal.json");

    private static class Metadata {
        private static final double CRYSTAL_WIDTH = configsNode.get("worldDimensions").get("width").asDouble();
        private static final double CRYSTAL_HEIGHT = configsNode.get("worldDimensions").get("width").asDouble();
        private static final double CRYSTAL_SWING_PERIOD_NS = configsNode.get("swingPeriodNs").asDouble();
        private static final double CRYSTAL_SWING_AMPLITUDE = configsNode.get("swingAmplitude").asDouble();
        private static final double CRYSTAL_TRANSPARENCY = configsNode.get("transparency").asDouble();
        private static final double CRYSTAL_BLOOM_THRESHOLD = configsNode.get("bloomThreshold").asDouble();
    }

    private final Tower tower;
    private final long creationTimeNs = System.nanoTime();

    TowerRenderComponent(final Tower tower) {
        this.tower = tower;
    }

    @Override
    public void render() {
        final Image bodyImage = SpriteLoader.loadSprite(new TowerSpriteKey(tower.getProjectileInfo().type(), tower.getEnchantmentInfo().type())).image();
        final AnimatedSprite crystalSprite = (AnimatedSprite) SpriteLoader.loadSprite(new CrystalSpriteKey(tower.getEnchantmentInfo().type()));

        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        final int currentFrame = (int) ((System.nanoTime() - creationTimeNs) / crystalSprite.getFrameTimeNs()) % crystalSprite.getFrameCount();
        final Image crystalImage = crystalSprite.image(currentFrame);

        final double crystalSwingOffset = Math.sin((System.nanoTime() - creationTimeNs) * 2 * Math.PI * 1. / Metadata.CRYSTAL_SWING_PERIOD_NS) * Metadata.CRYSTAL_SWING_AMPLITUDE * cs.getScale();

        final double topLeftScreenX = cs.toScreenX(tower.getWorldTopLeft().getX());
        final double topLeftScreenY = cs.toScreenY(tower.getWorldTopLeft().getY());
        final double screenWidth = cs.getScale() * tower.getWorldWidth();
        final double screenHeight = cs.getScale() * tower.getWorldHeight();

        final double firingWorldCenterX = tower.firingWorldCenterLocation().getX();
        final double firingWorldCenterY = tower.firingWorldCenterLocation().getY();

        final double crystalScreenX = cs.toScreenX(firingWorldCenterX - Metadata.CRYSTAL_WIDTH / 2);
        final double crystalScreenY = cs.toScreenY(firingWorldCenterY - Metadata.CRYSTAL_HEIGHT / 2) + crystalSwingOffset;

        renderer.addRenderTask(new RenderTask(RenderPriority.BUILDINGS, () -> {
            gc.save();
            final Rotate r = new Rotate(2 * Math.sin((System.nanoTime() - creationTimeNs) / 3e8), crystalScreenX + cs.getScale() * (Metadata.CRYSTAL_WIDTH / 2), crystalScreenY + cs.getScale() * (Metadata.CRYSTAL_HEIGHT / 2));
            gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
            gc.setEffect(new Bloom(Metadata.CRYSTAL_BLOOM_THRESHOLD));
            gc.setGlobalAlpha(Metadata.CRYSTAL_TRANSPARENCY);
            gc.drawImage(crystalImage, crystalScreenX, crystalScreenY, Metadata.CRYSTAL_WIDTH * cs.getScale(), Metadata.CRYSTAL_HEIGHT * cs.getScale());
            gc.restore();
            gc.drawImage(bodyImage, topLeftScreenX, topLeftScreenY, screenWidth, screenHeight);
        }).enableZOrder(tower.getWorldBottomRight().getY()));
    }
}
