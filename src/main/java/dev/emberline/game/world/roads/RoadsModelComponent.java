package dev.emberline.game.world.roads;

import utility.Coordinate2D;
import utility.Vector2D;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A class that represents the Road's class logic.
 */
class RoadsModelComponent {

    /**
     * graph data structure, represents the walkable roads on the map
     */
    private final Map<Vector2D, Node> posToNode = new HashMap<>();

    /**
     * Creates a new instance of {@code RoadsUpdateComponent}
     * @param wavePath represents the path of the files regarding the current wave
     */
    RoadsModelComponent(String wavePath) {
        loadGraph(wavePath + "roads.txt");
    }

    /**
     * @param pos is the current position.
     * @return the next node of the graph based on the current state.
     */
    Optional<Vector2D> getNextNode(Vector2D pos) {
        return posToNode.get(pos).getNext();
    }

    private void loadGraph(String file) {
        try {
            URL fileURL = Objects.requireNonNull(getClass().getResource(file));
            final BufferedReader r = new BufferedReader(new FileReader(fileURL.getPath()));
            String line = null;
            while ((line = r.readLine()) != null) {

                String[] numbers = line.split(" ");
                //summing (0.5, 0.5) to center
                Node fromNode = new Node(
                        new Coordinate2D(Double.parseDouble(numbers[0]) + 0.5, Double.parseDouble(numbers[1]) + 0.5));
                Node toNode = new Node(
                        new Coordinate2D(Double.parseDouble(numbers[2]) + 0.5, Double.parseDouble(numbers[3]) + 0.5));
                Integer weight = Integer.parseInt(numbers[4]);

                posToNode.putIfAbsent(fromNode.getPosition(), fromNode);
                posToNode.get(fromNode.getPosition()).addNeighbour(toNode, weight);

                posToNode.putIfAbsent(toNode.getPosition(), toNode);
            }
            r.close();
        } catch (IOException e) {
            System.out.println("error loading file: " + file);
        }
    }
}
