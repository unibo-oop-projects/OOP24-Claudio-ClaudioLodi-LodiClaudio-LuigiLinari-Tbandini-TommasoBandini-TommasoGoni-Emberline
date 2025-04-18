package dev.emberline.game.world.roads;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import utility.pairs.Pair;

public class Roads {
    
    /*
     * graph data structure
     * the map associates values as follows:
     * every position (pair or coordinates) has a list of the other positions it leads to
     * this list is stored as a queue, because the enemies are sent to each other position in a cyclical way,
     * starting from the head of the list to the last element.
     * the second pair in each element of the queue represents the number of enemies that must be sent that way before switching position,
     * in particular (remaining enemies to send that way, tot enemies to send that way for this cycle) 
     * 
     * (from nome), pair((to node), (enemies left, tot enemies) to send)
     */
    private Map<Pair<Integer, Integer>, Node> posToNode = new HashMap<>();

    public Roads(String file) {
        // hardcoded
        Pair<Integer,Integer> _start = new Pair<Integer,Integer>(2, 5);
        Node start = new Node(_start);
        posToNode.put(_start, start);

        Pair<Integer, Integer> intersectionPos = new Pair<>(8, 5);
        Node intersection = new Node(intersectionPos);
        posToNode.put(intersectionPos, intersection);

        Pair<Integer, Integer> upPos = new Pair<>(8, 2);
        Node up = new Node(upPos);
        posToNode.put(upPos, up);

        Pair<Integer, Integer> intersection2Pos = new Pair<>(15, 5);
        Node intersection2 = new Node(intersection2Pos);
        posToNode.put(intersection2Pos, intersection2);

        Pair<Integer, Integer> downPos = new Pair<>(8, 8);
        Node down = new Node(downPos);
        posToNode.put(downPos, down);

        Pair<Integer, Integer> upRightPos = new Pair<>(15, 2);
        Node upRight = new Node(upRightPos);
        posToNode.put(upRightPos, upRight);

        Pair<Integer, Integer> downRightPos = new Pair<>(15, 8);
        Node downRight = new Node(downRightPos);
        posToNode.put(downRightPos, downRight);

        Pair<Integer, Integer> endPos = new Pair<>(22, 5);
        Node end = new Node(endPos);
        posToNode.put(endPos, end);
        
        start.addNeighbour(intersection, 1);

        intersection.addNeighbour(up, 1);
        intersection.addNeighbour(down, 1);

        up.addNeighbour(upRight, 1);
        down.addNeighbour(downRight, 1);

        upRight.addNeighbour(intersection2, 1);
        downRight.addNeighbour(intersection2, 1);

        intersection2.addNeighbour(end, 1);
        
        //loadGraph(file);
    }
    
    //if "weight" enemies were sent to the given node, place the node at the back of the queue
    //and reset the counter on it
    public Optional<Pair<Integer,Integer>> getNextNode(Pair<Integer,Integer> pos) {
        return posToNode.get(pos).getNext();
    }

    //TODO
    private void loadGraph(String file) {
        
    }
}
