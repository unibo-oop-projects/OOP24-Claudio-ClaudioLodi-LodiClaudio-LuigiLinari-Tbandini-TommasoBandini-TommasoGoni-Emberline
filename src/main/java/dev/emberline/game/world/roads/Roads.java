package dev.emberline.game.world.roads;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The Roads class represents the navigable road network in a map, structured as a graph of nodes and edges.
 * It allows for determining the next road node from a given position and is initialized using a configuration file.
 */
public class Roads {

    /**
     * graph data structure, represents the walkable roads on the map
     */
    private final Map<Vector2D, Node> posToNode = new HashMap<>();
    private final Arch[] arches;

    private static final String ROADS_CONFIG_FILENAME = "roads.json";

    //single arch configuration
    private record Arch (
        @JsonProperty
        double fromX,
        @JsonProperty
        double fromY,
        @JsonProperty
        double toX,
        @JsonProperty
        double toY,
        @JsonProperty
        int weight
    ) {}

    /**
     * Creates a new Roads object and determines the road network based on the configuration file.
     *
     * @param wavePath represents the path of the files regarding the current wave
     */
    public Roads(final String wavePath) {
        arches = ConfigLoader.loadConfig(wavePath + ROADS_CONFIG_FILENAME, Arch[].class);
        parseGraph();
    }

    /**
     * Returns the next node of the chosen path.
     *
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
