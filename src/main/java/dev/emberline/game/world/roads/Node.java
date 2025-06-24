package dev.emberline.game.world.roads;

import dev.emberline.utility.Pair;
import dev.emberline.utility.Vector2D;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class Node implements Serializable {

    private final Vector2D pos;
    /*
     * List of Nodes this Node is connected to with a given weight each.
     * The weight determines the number of enemies that go that way.
     */
    private final List<Pair<Node, Integer>> neighbours;

    private int cnt;
    private int currIdx;

    public Node(final Vector2D pos) {
        this.pos = pos;
        this.neighbours = new ArrayList<>();

        /*
         * index of the current Node this Node is pointing to, rotates cyclically through the List
         */
        this.currIdx = -1;
        this.cnt = -1;
    }

    public void addNeighbour(final Node neighbour, final Integer weight) {
        neighbours.add(new Pair<>(neighbour, weight));
    }

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

    public Vector2D getPosition() {
        return pos;
    }
}