package dev.emberline.game.world;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.world.waves.IWaveManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class WorldRenderComponent implements Renderable, Updatable {
    //world bounds
    private record Coordinate(
            @JsonProperty("x") int x,
            @JsonProperty("y") int y
    ) {}
    private record WorldBounds(
            @JsonProperty("topLeftBound") Coordinate topLeftBound,
            @JsonProperty("bottomRightBound") Coordinate bottomRightBound
    ) {}

    private final WorldBounds worldBounds;

    private final MapAnimation mapAnimation;

    WorldRenderComponent(IWaveManager waveManager) {
        worldBounds = ConfigLoader.loadConfig("/world/worldBounds.json", WorldBounds.class);
        this.mapAnimation = new MapAnimation(waveManager);
    }

    @Override
    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        double mapScreenWidth = worldBounds.bottomRightBound.x * cs.getScale();
        double mapScreenHeight = worldBounds.bottomRightBound.y * cs.getScale();
        double mapScreenX = cs.toScreenX(worldBounds.topLeftBound.x);
        double mapScreenY = cs.toScreenY(worldBounds.topLeftBound.y);

        Image currentFrame = mapAnimation.getImage();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(currentFrame, mapScreenX, mapScreenY, mapScreenWidth, mapScreenHeight);
        }));
    }

    @Override
    public void update(long elapsed) {
        mapAnimation.update(elapsed);
    }
}
