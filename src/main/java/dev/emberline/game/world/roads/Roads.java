package dev.emberline.game.world.roads;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;

/**
 * A class that represents the roads of the map, as weighted arches.
 */
public class Roads {
    //single arch configuration
    private static final String roadsConfigFilename = "roads.json";
    private static class Arch {
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

    private final Arch[] arches;
    /**
     * graph data structure, represents the walkable roads on the map
     */
    private final Map<Vector2D, Node> posToNode = new HashMap<>();

    /**
     * @param wavePath represents the path of the files regarding the current wave
     */
    public Roads(String wavePath) {
        arches = ConfigLoader.loadConfig(wavePath + roadsConfigFilename, Arch[].class);
        parseGraph();
    }

    /**
     * @param pos is the current position.
     * @return the next node of the graph based on the current state.
     */
    public Optional<Vector2D> getNextNode(Vector2D pos) {
        return posToNode.get(pos).getNext();
    }

    private void parseGraph() {
        for (var arch : arches) {
            Node fromNode = new Node(
                    new Coordinate2D(arch.fromX, arch.fromY).add(0.5, 0.5));
            Node toNode = new Node(
                    new Coordinate2D(arch.toX, arch.toY).add(0.5, 0.5));
            Integer weight = arch.weight;

            posToNode.putIfAbsent(fromNode.getPosition(), fromNode);
            posToNode.get(fromNode.getPosition()).addNeighbour(toNode, weight);

            posToNode.putIfAbsent(toNode.getPosition(), toNode);
        }
    }
}
