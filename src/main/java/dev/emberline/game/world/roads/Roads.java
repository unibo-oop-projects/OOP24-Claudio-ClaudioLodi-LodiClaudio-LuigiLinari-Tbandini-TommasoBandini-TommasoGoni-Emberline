package dev.emberline.game.world.roads;

import java.util.*;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.utility.Vector2D;

/**
 * A class that represents the roads of the map, as weighted arches.
 */
public class Roads implements Renderable, Updatable {
    
    private final RoadsModelComponent roadsModelComponent;
    private final RoadsViewComponent roadsViewComponent;

    /**
     * @param wavePath represents the path of the files regarding the current wave
     */
    public Roads(String wavePath) {
        roadsViewComponent = new RoadsViewComponent(wavePath);
        roadsModelComponent = new RoadsModelComponent(wavePath);
    }

    /**
     * @param pos is the current position
     * @return the next node of the graph based on the current state.
     */
    public Optional<Vector2D> getNextNode(Vector2D pos) {
        return roadsModelComponent.getNextNode(pos);
    }

    @Override
    public void render() {
        roadsViewComponent.render();
    }

    /**
     * Updates any animation in the map.
     * @param elapsed
     */
    @Override
    public void update(long elapsed) {
        roadsViewComponent.update(elapsed);
    }
}
