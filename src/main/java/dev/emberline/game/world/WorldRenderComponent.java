package dev.emberline.game.world;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.config.ConfigLoader;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.world.waves.IWaveManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.Serial;
import java.io.Serializable;

/**
 * The WorldRenderComponent class is responsible for rendering the game world, specifically
 * it's underlying map. That also means keeping track of how the map changes due to its animation.
 */
public class WorldRenderComponent implements Renderable, Updatable, Serializable {

    @Serial
    private static final long serialVersionUID = 8505789831229582267L;

    private final WorldBounds worldBounds;
    private final MapAnimation mapAnimation;

    private record WorldBounds(
            @JsonProperty
            int topLeftX,
            @JsonProperty
            int topLeftY,
            @JsonProperty
            int bottomRightX,
            @JsonProperty
            int bottomRightY
    ) implements Serializable {
        // Data validation
        private WorldBounds {
            if (topLeftX >= bottomRightX || topLeftY >= bottomRightY) {
                throw new IllegalArgumentException("Invalid world bounds: " + this);
            }
        }
    }

    WorldRenderComponent(final IWaveManager waveManager) {
        worldBounds = ConfigLoader.loadConfig("/world/worldBounds.json", WorldBounds.class);
        this.mapAnimation = new MapAnimation(waveManager);
    }

    /**
     * Renders the current state of the map.
     * @see Renderable#render()
     */
    @Override
    public void render() {
        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        final double mapScreenWidth = (worldBounds.bottomRightX - worldBounds.topLeftX) * cs.getScale();
        final double mapScreenHeight = (worldBounds.bottomRightY - worldBounds.topLeftY) * cs.getScale();
        final double mapScreenX = cs.toScreenX(worldBounds.topLeftX);
        final double mapScreenY = cs.toScreenY(worldBounds.topLeftY);

        final Image currentFrame = mapAnimation.getImage();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(currentFrame, mapScreenX, mapScreenY, mapScreenWidth, mapScreenHeight);
        }));
    }

    /**
     * Updates the map animation based on the time elapsed since the last update.
     *
     * @param elapsed the time in nanoseconds since the last update
     */
    @Override
    public void update(final long elapsed) {
        mapAnimation.update(elapsed);
    }
}