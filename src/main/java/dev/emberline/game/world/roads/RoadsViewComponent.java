package dev.emberline.game.world.roads;

import dev.emberline.core.GameLoop;
import dev.emberline.core.animations.OneShotAnimation;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A class that represents the Road's class view.
 */
class RoadsViewComponent implements Renderable, Updatable {

    private OneShotAnimation mapAnimation;

    /**
     * Creates a new instance of {@code RoadsRenderComponent}
     * @param wavePath represents the path of the files regarding the current wave
     */
    RoadsViewComponent(String wavePath) {
        loadMapAnimation(wavePath + "mapAnimation/");
    }

    private Image loadMapImage(String file) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(file)));
    }

    private void loadMapAnimation(String file) {
        List<Image> animationStates = new ArrayList<>();
        try {
            for (int i = 1; i <= 4; i++) {
                Image image = loadMapImage(file + "map" + i + ".png");
                animationStates.add(image);
            }
        } catch (NullPointerException e) {
            //nothing needed here, either 1 or 4 images are found in the mapAnimation folder
        }
        mapAnimation = new OneShotAnimation(animationStates, 1_000_000_000L);
    }

    /**
     * Adds the map png to the render queue, based on its current state.
     */
    @Override
    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        double screenX = cs.toScreenX(0);
        double screenY = cs.toScreenY(0);

        Image imageToRender = mapAnimation.getAnimationState();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(imageToRender, screenX, screenY, 32*cs.getScale(), 18*cs.getScale());
        }));
    }

    @Override
    public void update(long elapsed) {
        mapAnimation.update(elapsed);
    }
}
