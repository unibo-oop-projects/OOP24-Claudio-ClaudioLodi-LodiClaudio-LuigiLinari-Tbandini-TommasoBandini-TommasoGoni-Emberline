package dev.emberline.waves;

import dev.emberline.roads.Roads;
import utility.pairs.Pair;

public class Wave {
    
    Roads roads = new Roads("filename");
    //enemies to spawn

    
    public Pair<Integer,Integer> getNext(Pair<Integer,Integer> pos) {
        return roads.getNextNode(pos);
    }
    
}
