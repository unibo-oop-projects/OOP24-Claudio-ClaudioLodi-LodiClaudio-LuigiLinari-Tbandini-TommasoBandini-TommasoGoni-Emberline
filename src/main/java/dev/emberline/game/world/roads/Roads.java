package dev.emberline.game.world.roads;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

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
import utility.Coordinate2D;
import utility.Vector2D;

public class Roads implements Renderable, Updatable {
    
    private final RoadsUpdateComponent roadsUpdateComponent;
    private final RoadsRenderComponent roadsRenderComponent;

    public Roads(String wavePath) {
        roadsRenderComponent = new RoadsRenderComponent(wavePath);
        roadsUpdateComponent = new RoadsUpdateComponent(wavePath);
    }

    /**
     * @param pos is the current position.
     * @return the next node of the graph based on the current state.
     */
    public Optional<Vector2D> getNextNode(Vector2D pos) {
        return roadsUpdateComponent.getNextNode(pos);
    }

    @Override
    public void render() {
        roadsRenderComponent.render();
    }

    /**
     * Updates any animation in the map.
     * @param elapsed
     */
    @Override
    public void update(long elapsed) {
        roadsRenderComponent.update(elapsed);
    }
}
