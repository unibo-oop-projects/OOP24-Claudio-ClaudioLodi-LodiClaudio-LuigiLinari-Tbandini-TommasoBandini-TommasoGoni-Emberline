package dev.emberline.game.world.roads;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A class that represents the roads of the map, as weighted arches.
 */
public class Roads {

    /**
     * graph data structure, represents the walkable roads on the map
     */
    private final Map<Vector2D, Node> posToNode = new HashMap<>();
    private final Arch[] arches;

    private static final String ROADS_CONFIG_FILENAME = "roads.json";
    //single arch configuration
    private static final class Arch {
        @JsonProperty("fromX")
        private double fromX;
        @JsonProperty("fromY")
        private double fromY;
        @JsonProperty("toX")
        private double toX;
        @JsonProperty("toY")
        private double toY;
        @JsonProperty("weight")
        private int weight;
    }

    /**
     * @param wavePath represents the path of the files regarding the current wave
     */
    public Roads(final String wavePath) {
        arches = ConfigLoader.loadConfig(wavePath + ROADS_CONFIG_FILENAME, Arch[].class);
        parseGraph();
    }

    /**
     * @param pos is the current position.
     * @return the next node of the graph based on the current state.
     */
    public Optional<Vector2D> getNextNode(final Vector2D pos) {
        return posToNode.get(pos).getNext();
    }

    private void parseGraph() {
        for (final var arch : arches) {
            final Node fromNode = new Node(
                    new Coordinate2D(arch.fromX, arch.fromY).add(0.5, 0.5));
            final Node toNode = new Node(
                    new Coordinate2D(arch.toX, arch.toY).add(0.5, 0.5));
            final Integer weight = arch.weight;

            posToNode.putIfAbsent(fromNode.getPosition(), fromNode);
            posToNode.get(fromNode.getPosition()).addNeighbour(toNode, weight);

            posToNode.putIfAbsent(toNode.getPosition(), toNode);
        }
    }
}
