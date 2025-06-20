package dev.emberline.game.world.buildings.towerPreBuild;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.GameLoop;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.UISpriteKey;
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

    private static String configsPath = "/sprites/towerAssets/towerPreBuild.json";
    private static class Metadata {
        @JsonProperty("width")
        private double worldWidth;
        @JsonProperty("height")
        private double worldHeight;
    }
    private static Metadata metadata = ConfigLoader.loadConfig(ConfigLoader.loadNode(configsPath).get("worldDimensions"), Metadata.class);

    private final Vector2D locationBottomLeft;
    private final TowersManager towersManager;

    public TowerPreBuild(Vector2D locationBottomLeft, TowersManager towersManager) {
        this.locationBottomLeft = locationBottomLeft;
        this.towersManager = towersManager;
    }

    @Override
    public Vector2D getWorldTopLeft() {
        return locationBottomLeft.subtract(0, metadata.worldHeight);
    }

    @Override
    public Vector2D getWorldBottomRight() {
        return locationBottomLeft.add(metadata.worldWidth, 0);
    }

    @Override
    protected void clicked() {
        towersManager.buildTower(this);
    }

    @Override
    public void render() {
        Image image = SpriteLoader.loadSprite(UISpriteKey.TOWER_PRE_BUILD).image();

        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        double topLeftScreenX = cs.toScreenX(getWorldTopLeft().getX());
        double topLeftScreenY = cs.toScreenY(getWorldTopLeft().getY());
        double screenWidth = cs.getScale() * metadata.worldWidth;
        double screenHeight = cs.getScale() * metadata.worldHeight;

        renderer.addRenderTask(new RenderTask(RenderPriority.BUILDINGS, () -> {
            gc.drawImage(image, topLeftScreenX, topLeftScreenY, screenWidth, screenHeight);
        }));
    }

    @Override
    public void update(long elapsed) {}
}
