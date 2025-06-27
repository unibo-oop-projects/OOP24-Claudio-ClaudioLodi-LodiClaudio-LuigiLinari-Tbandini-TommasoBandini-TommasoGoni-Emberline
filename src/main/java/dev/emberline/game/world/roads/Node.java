package dev.emberline.game.world.roads;

import dev.emberline.utility.Pair;
import dev.emberline.utility.Vector2D;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a node in a graph-like structure.
 * <p>
 * Each node has a positional vector and a list of neighboring nodes with associated weights.
 * The weights are used to determine the path-choosing logic within the graph: each weight of the graph
 * indicates how many times that path is chosen before changing it.
 * <p>
 * This class provides methods for adding neighbors, retrieving the node's
 * position, and determining the next node to traverse based on edge weights.
 * The path chosen rotates cyclically through the list of neighbors.
 */
class Node implements Serializable {

    @Serial
    private static final long serialVersionUID = 6775258487774026022L;

    private final Vector2D pos;
    /**
     * List of Nodes this Node is connected to, with a given weight each.
     * The weight determines the number of enemies that go that way.
     */
    private final List<Pair<Node, Integer>> neighbours;

    private int cnt;
    private int currIdx;

    /**
     * Constructs a new Node with a specified position.
     *
     * @param pos the positional vector of this Node, represented as a {@code Vector2D}
     */
    public Node(final Vector2D pos) {
        this.pos = pos;
        this.neighbours = new ArrayList<>();

        /*
         * index of the current Node this Node is pointing to, rotates cyclically through the List
         */
        this.currIdx = -1;
        this.cnt = -1;
    }

    /**
     * Adds a neighboring node to this node along with the weight of the edge.
     *
     * @param neighbour the neighboring node to be added
     * @param weight    the weight of the edge connecting to the neighboring node
     */
    public void addNeighbour(final Node neighbour, final Integer weight) {
        neighbours.add(new Pair<>(neighbour, weight));
    }

    /**
     * Retrieves the next position in the sequence of neighbors, determining the path chosen in the graph.
     * The method rotates cyclically through the list of neighbors, decrementing a counter
     * for the current neighbor's weight until it reaches 0, at which point it advances to the next neighbor.
     *
     * @return an {@code Optional} containing the position of the next neighbor as a {@code Vector2D},
     * or {@code Optional.empty()} if the list of neighbors is empty.
     */
    public Optional<Vector2D> getNext() {
        if (neighbours.isEmpty()) {
            return Optional.empty();
        }

        while (cnt <= 0) {
            currIdx = (currIdx + 1) % neighbours.size();
            cnt = neighbours.get(currIdx).getY();
        }
        cnt--;

        return Optional.of(neighbours.get(currIdx).getX().getPosition());
    }

    /**
     * Retrieves the position of this node.
     *
     * @return the positional vector of this node as a {@code Vector2D}.
     */
    public Vector2D getPosition() {
        return pos;
    }
}
