package dev.emberline.roads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    private Map<Pair<Integer, Integer>, Queue<Pair<Pair<Integer, Integer>, Pair<Integer,Integer>>>> roads = new HashMap<>();

    private Pair<Integer,Integer> _start = new Pair<Integer,Integer>(2, 6);
    private Pair<Integer,Integer> _end = new Pair<Integer,Integer>(2, 0);
    private Pair<Integer,Integer> _weight = new Pair<Integer,Integer>(1, 1);

    public Roads(String file) {
        //hardcoded road
        Queue<Pair<Pair<Integer, Integer>, Pair<Integer,Integer>>> queue =  new ConcurrentLinkedQueue<>();
        queue.offer(new Pair<>(_end, _weight));
        roads.put(_start, queue);

        loadGraph(file);
    }
    
    //if "weight" enemies were sent to the given node, place the node at the back of the queue
    //and reset the counter on it
    public Pair<Integer,Integer> getNextNode(Pair<Integer,Integer> pos) {
        if (!roads.get(pos).isEmpty()) {
            Pair<Pair<Integer,Integer>, Pair<Integer,Integer>> next;
            if (roads.get(pos).peek().getY().getX() > 0) {
                next = roads.get(pos).peek();
                //updates numbers of enemies to send this way (-1)
                roads.get(pos).peek().getY().setX(next.getY().getX()-1);
            } else {
                next = roads.get(pos).poll();
                //pushes at the end of the queue the current position, resetting the enemies counter
                roads.get(pos).offer(new Pair<>(next.getX(), new Pair<>(next.getY().getY(), next.getY().getY())));
            }
            return next.getX();
        } else {
            return pos;
        }
    }

    //file template: (from node) (to node) (weight)
    private void loadGraph(String file) {
        
    }
}
