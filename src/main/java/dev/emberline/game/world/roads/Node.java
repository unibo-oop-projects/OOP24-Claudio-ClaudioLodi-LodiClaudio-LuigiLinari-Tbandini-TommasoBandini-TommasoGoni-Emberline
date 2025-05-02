package dev.emberline.game.world.roads;

import utility.Pair;
import utility.Tile;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

class Node {
    private final Tile pos;
    private final List<Pair<Node, Integer>> neighbours;

    private int cnt;
    private int currIdx;

    public Node(Tile pos) {
        this.pos = pos;
        this.neighbours = new ArrayList<>();

        this.currIdx = -1;
        this.cnt = -1;
    }

    public void addNeighbour(Node neighbour, Integer weight) {
        neighbours.add(new Pair<>(neighbour, weight));
    }

    public Optional<Tile> getNext() {
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

    public Tile getPosition() {
        return pos;
    }
}