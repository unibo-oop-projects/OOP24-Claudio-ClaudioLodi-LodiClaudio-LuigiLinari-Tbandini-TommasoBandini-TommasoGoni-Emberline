package dev.emberline.game.world.buildings.towerPreBuild;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.GameLoop;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.world.Building;
import dev.emberline.game.world.buildings.TowersManager;
import dev.emberline.utility.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class TowerPreBuild extends Building {

    private static final String CONFIGS_PATH = "/sprites/towerAssets/towerPreBuild.json";

    private static class Metadata {
        @JsonProperty double worldDimensionWidth;
        @JsonProperty double worldDimensionHeight;
        @JsonProperty int newBuildCost;
    }
    private static Metadata metadata = ConfigLoader.loadConfig(CONFIGS_PATH, Metadata.class);

    private final Vector2D locationBottomLeft;
    private final TowersManager towersManager;

    public TowerPreBuild(final Vector2D locationBottomLeft, final TowersManager towersManager) {
        this.locationBottomLeft = locationBottomLeft;
        this.towersManager = towersManager;
    }

    @Override
    public Vector2D getWorldTopLeft() {
        return locationBottomLeft.subtract(0, metadata.worldDimensionHeight);
    }

    @Override
    public Vector2D getWorldBottomRight() {
        return locationBottomLeft.add(metadata.worldDimensionWidth, 0);
    }

    public int getNewBuildCost() {
        return metadata.newBuildCost;
    }

    @Override
    protected void clicked() {
        towersManager.closeTowerDialog();
        towersManager.openNewBuildDialog(this);
    }

    @Override
    public void render() {
        final Image image = SpriteLoader.loadSprite(SingleSpriteKey.TOWER_PRE_BUILD).image();

        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        final double topLeftScreenX = cs.toScreenX(getWorldTopLeft().getX());
        final double topLeftScreenY = cs.toScreenY(getWorldTopLeft().getY());
        final double screenWidth = cs.getScale() * metadata.worldDimensionWidth;
        final double screenHeight = cs.getScale() * metadata.worldDimensionHeight;

        renderer.addRenderTask(new RenderTask(RenderPriority.BUILDINGS, () -> {
            gc.drawImage(image, topLeftScreenX, topLeftScreenY, screenWidth, screenHeight);
        }));
    }

    @Override
    public void update(final long elapsed) {
    }
}
