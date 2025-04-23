package dev.emberline.game.world.roads;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

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

    private Pair<Integer,Integer> _start = new Pair<Integer,Integer>(2, 10);
    private Pair<Integer,Integer> _end = new Pair<Integer,Integer>(2, 0);//sopra
    private Pair<Integer,Integer> _end1 = new Pair<Integer,Integer>(0, 0);//left
    private Pair<Integer,Integer> _end2 = new Pair<Integer,Integer>(5, 0);//right
    private Pair<Integer,Integer> _weight = new Pair<Integer,Integer>(1, 1);
    private Pair<Integer,Integer> _weight1 = new Pair<Integer,Integer>(0, 0);


    public Roads(String file) {
        // hardcoded
        
        /* Node start = new Node(_start);
        posToNode.put(_start, start);

        Node intersection = new Node(_end);
        posToNode.put(_end, intersection);

        Node left = new Node(_end1);
        posToNode.put(_end1, left);

        Node right = new Node(_end2);
        posToNode.put(_end2, right);
        
        start.addNeighbour(intersection, 1);
        intersection.addNeighbour(left, 1);
        intersection.addNeighbour(right, 2);
         */
        loadGraph(file);
    }
    
    //if "weight" enemies were sent to the given node, place the node at the back of the queue
    //and reset the counter on it
    public Pair<Integer,Integer> getNextNode(Pair<Integer,Integer> pos) {
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
