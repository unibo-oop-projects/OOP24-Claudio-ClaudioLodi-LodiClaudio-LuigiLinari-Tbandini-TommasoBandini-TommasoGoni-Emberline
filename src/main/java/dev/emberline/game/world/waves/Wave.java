package dev.emberline.game.world.waves;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import dev.emberline.game.world.World;
import dev.emberline.game.world.roads.Roads;
import javafx.geometry.Point2D;
import utility.pairs.Pair;

public class Wave {
    
    private final Long waveStartTime = System.currentTimeMillis();

    private World world;
    private Roads roads = new Roads("filename");
    private List<Queue<Pair<Pair<Integer,Integer>,Long>>> enemiesToSpawn;
    
    //temp variables to delete later
    private long timeFromStart = 10000; //10s
    //(x,y) coords for example road (check roads, (2,6) should be the spawnpoint)
    private int a = 2;
    private int b = 6;

    public Wave(World world) {
        this.world = world;
        
        enemiesToSpawn = new ArrayList<>(1);
        Queue<Pair<Pair<Integer,Integer>,Long>> tempQueue = new ConcurrentLinkedQueue<>();
        tempQueue.add(new Pair<>(new Pair<>(a, b), timeFromStart));
        enemiesToSpawn.add(tempQueue);
    }


    public Pair<Integer,Integer> getNext(Pair<Integer,Integer> pos) {
        return roads.getNextNode(pos);
    }

    public void sendEnemies() {
        for (int i = 0; i < enemiesToSpawn.size(); i++) {
            Queue<Pair<Pair<Integer,Integer>,Long>> queue = enemiesToSpawn.get(i); 
            while (!queue.isEmpty() && queue.peek().getY() >= System.currentTimeMillis() - waveStartTime) {
                //will switch to a same type of pairs, and erase this since hence usless
                Pair<Integer,Integer> p = queue.poll().getX();
                Point2D p2 = new Point2D(p.getX(), p.getY());
                world.getEnemiesManager().addEnemy(p2);
            }
        }
    }
}
