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

/**
 * The WorldRenderComponent class is responsible for rendering the game world, specifically
 * it's underlying map. That also means keeping track of how the map changes due to its animation.
 */
public class WorldRenderComponent implements Renderable, Updatable {

    private final WorldBounds worldBounds;
    private final MapAnimation mapAnimation;

    //world bounds
    private record Coordinate(
            @JsonProperty int x,
            @JsonProperty int y
    ) {
    }

    private record WorldBounds(
            @JsonProperty Coordinate topLeftBound,
            @JsonProperty Coordinate bottomRightBound
    ) {
    }

    WorldRenderComponent(final IWaveManager waveManager) {
        worldBounds = ConfigLoader.loadConfig("/world/worldBounds.json", WorldBounds.class);
        this.mapAnimation = new MapAnimation(waveManager);
    }

    @Override
    public void render() {
        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        final double mapScreenWidth = worldBounds.bottomRightBound.x * cs.getScale();
        final double mapScreenHeight = worldBounds.bottomRightBound.y * cs.getScale();
        final double mapScreenX = cs.toScreenX(worldBounds.topLeftBound.x);
        final double mapScreenY = cs.toScreenY(worldBounds.topLeftBound.y);

        final Image currentFrame = mapAnimation.getImage();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(currentFrame, mapScreenX, mapScreenY, mapScreenWidth, mapScreenHeight);
        }));
    }

    @Override
    public void update(final long elapsed) {
        mapAnimation.update(elapsed);
    }
}
