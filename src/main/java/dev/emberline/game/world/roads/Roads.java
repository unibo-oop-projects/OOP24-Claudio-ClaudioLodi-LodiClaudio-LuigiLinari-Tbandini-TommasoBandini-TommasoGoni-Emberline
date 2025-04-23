package dev.emberline.game.world.roads;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
        loadGraph(file);
    }
    
    //if "weight" enemies were sent to the given node, place the node at the back of the queue
    //and reset the counter on it
    public Optional<Pair<Integer,Integer>> getNextNode(Pair<Integer,Integer> pos) {
        return posToNode.get(pos).getNext();
    }

    private void loadGraph(String file) {
        try {
            final BufferedReader r = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = r.readLine()) != null) {
                
                String[] numbers = line.split(" ");
                
                Node fromNode = new Node(new Pair<>(Integer.parseInt(numbers[1]), Integer.parseInt(numbers[0])));
                Node toNode = new Node(new Pair<>(Integer.parseInt(numbers[3]), Integer.parseInt(numbers[2])));
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
