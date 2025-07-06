package dev.emberline.game.world.buildings.tower;

import com.fasterxml.jackson.databind.JsonNode;
import dev.emberline.core.GameLoop;
import dev.emberline.core.components.RenderComponent;
import dev.emberline.core.config.ConfigLoader;
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
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.io.Serial;
import java.io.Serializable;

class TowerRenderComponent implements RenderComponent, Serializable {

    @Serial
    private static final long serialVersionUID = 3616851823744803302L;

    private final Tower tower;
    private final long creationTimeNs = System.nanoTime();

    private static final JsonNode CONFIGS_NODE = ConfigLoader.loadNode("/sprites/towerAssets/crystal.json");

    private static final class Metadata {
        private static final double CRYSTAL_WIDTH = CONFIGS_NODE.get("worldDimensions").get("width").asDouble();
        private static final double CRYSTAL_HEIGHT = CONFIGS_NODE.get("worldDimensions").get("width").asDouble();
        private static final double CRYSTAL_SWING_PERIOD_NS = CONFIGS_NODE.get("swingPeriodNs").asDouble();
        private static final double CRYSTAL_SWING_AMPLITUDE = CONFIGS_NODE.get("swingAmplitude").asDouble();
        private static final double CRYSTAL_TRANSPARENCY = CONFIGS_NODE.get("transparency").asDouble();
        private static final double CRYSTAL_BLOOM_THRESHOLD = CONFIGS_NODE.get("bloomThreshold").asDouble();
    }

    TowerRenderComponent(final Tower tower) {
        this.tower = tower;
    }

    /**
     * Renders the tower body and its crystal animation, onto the game screen.
     * It translates locations and dimensions to screen space and draws the appropriate sprites.
     * <p>
     * The tower body is drawn at the calculated screen position based on its world coordinates, and the crystal,
     * which oscillates and rotates slightly, is drawn with a bloom visual effect and transparency applied.
     * <p>
     * Rendering tasks are queued and executed with z-order using the bottom y coordinate.
     */
    @Override
    public void render() {
        final Image bodyImage = SpriteLoader.loadSprite(new TowerSpriteKey(
                tower.getProjectileInfo().type(), tower.getEnchantmentInfo().type())).image();
        final AnimatedSprite crystalSprite = (AnimatedSprite) SpriteLoader.loadSprite(new CrystalSpriteKey(
                tower.getEnchantmentInfo().type()));

        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        final int currentFrame = (int) (
                (System.nanoTime() - creationTimeNs) / crystalSprite.getFrameTimeNs()) % crystalSprite.getFrameCount();
        final Image crystalImage = crystalSprite.image(currentFrame);

        final double crystalSwingOffset =
                Math.sin((System.nanoTime() - creationTimeNs) * 2 * Math.PI * 1. / Metadata.CRYSTAL_SWING_PERIOD_NS)
                * Metadata.CRYSTAL_SWING_AMPLITUDE * cs.getScale();

        final double topLeftScreenX = cs.toScreenX(tower.getWorldTopLeft().getX());
        final double topLeftScreenY = cs.toScreenY(tower.getWorldTopLeft().getY());
        final double screenWidth = cs.getScale() * tower.getWorldWidth();
        final double screenHeight = cs.getScale() * tower.getWorldHeight();

        final double firingWorldCenterX = tower.getFiringWorldCenterLocation().getX();
        final double firingWorldCenterY = tower.getFiringWorldCenterLocation().getY();

        final double crystalScreenX = cs.toScreenX(firingWorldCenterX - Metadata.CRYSTAL_WIDTH / 2);
        final double crystalScreenY = cs.toScreenY(firingWorldCenterY - Metadata.CRYSTAL_HEIGHT / 2)
                + crystalSwingOffset;

        final double worldTowerRange = tower.getProjectileInfo().getTowerRange();
        final double ovalScreenX = cs.toScreenX(tower.getPosition().getX() - worldTowerRange / 2);
        final double ovalScreenY = cs.toScreenY(tower.getPosition().getY() - worldTowerRange / 2);

        renderer.addRenderTask(new RenderTask(RenderPriority.BUILDINGS, () -> {
            gc.save();
            final Rotate r = new Rotate(2 * Math.sin((System.nanoTime() - creationTimeNs) / 3e8),
                    crystalScreenX + cs.getScale() * (Metadata.CRYSTAL_WIDTH / 2),
                    crystalScreenY + cs.getScale() * (Metadata.CRYSTAL_HEIGHT / 2));
            gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
            gc.setEffect(new Bloom(Metadata.CRYSTAL_BLOOM_THRESHOLD));
            gc.setGlobalAlpha(Metadata.CRYSTAL_TRANSPARENCY);
            gc.drawImage(crystalImage, crystalScreenX, crystalScreenY,
                    Metadata.CRYSTAL_WIDTH * cs.getScale(), Metadata.CRYSTAL_HEIGHT * cs.getScale());
            gc.restore();

            gc.setStroke(Color.BLACK);
            gc.strokeOval(ovalScreenX, ovalScreenY, worldTowerRange * cs.getScale(), worldTowerRange * cs.getScale());

            gc.drawImage(bodyImage, topLeftScreenX, topLeftScreenY, screenWidth, screenHeight);
        }).enableZOrder(tower.getWorldBottomRight().getY()));
    }
}
