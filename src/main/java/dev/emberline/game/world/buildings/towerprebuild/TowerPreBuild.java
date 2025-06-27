package dev.emberline.game.world.buildings.towerprebuild;

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

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a pre-build state for a tower in the game. This class is responsible for
 * handling its rendering, location, and interaction.
 * <p>
 * TowerPreBuild extends the functionality provided by the abstract {@code Building}
 * class to implement specific behavior.
 * <p>
 * Configuration data such as dimensions are retrieved from an external JSON file using {@code ConfigLoader}.
 */
public class TowerPreBuild extends Building implements Serializable {
    @Serial
    private static final long serialVersionUID = 6299522696067352043L;

    private static final String CONFIGS_PATH = "/sprites/towerAssets/towerPreBuild.json";

    private static final Metadata metadata = ConfigLoader.loadConfig(CONFIGS_PATH, Metadata.class);

    private final Vector2D locationBottomLeft;
    private final TowersManager towersManager;

    private record Metadata (
        @JsonProperty double worldDimensionWidth,
        @JsonProperty double worldDimensionHeight,
        @JsonProperty int newBuildCost
    ) { }

    /**
     * Constructs a {@link TowerPreBuild} instance with a specified bottom-left location and towers manager reference.
     *
     * @param locationBottomLeft the bottom-left location of the tower in world coordinates
     * @param towersManager the manager responsible for handling tower-related actions and dialogs
     */
    public TowerPreBuild(final Vector2D locationBottomLeft, final TowersManager towersManager) {
        this.locationBottomLeft = locationBottomLeft;
        this.towersManager = towersManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector2D getWorldTopLeft() {
        return locationBottomLeft.subtract(0, metadata.worldDimensionHeight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector2D getWorldBottomRight() {
        return locationBottomLeft.add(metadata.worldDimensionWidth, 0);
    }

    public int getNewBuildCost() {
        return metadata.newBuildCost;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void clicked() {
        towersManager.closeTowerDialog();
        towersManager.openNewBuildDialog(this);
    }

    /**
     * Renders the TowerPreBuild object using the {@code TOWER_PRE_BUILD} sprite.
     */
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

    /**
     * Updates the tower pre-build based on the elapsed time since the last update call.
     *
     * @param elapsed the time in nanoseconds since the last update
     */
    @Override
    public void update(final long elapsed) {
    }
}
