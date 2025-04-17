package dev.emberline.game.world.waves;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;
import dev.emberline.game.world.roads.Roads;
import javafx.geometry.Point2D;
import utility.pairs.Pair;

public class Wave implements Updatable {
    
    private World world;
    private Roads roads = new Roads("filename");
    private List<Queue<Pair<Pair<Integer,Integer>,Long>>> enemiesToSpawn;
    
    //temp variables to delete later
    private long timeFromStart = 5_000_000_000L;
    private long c = 2_000_000_000L;
    //(x,y) coords for example road (check roads, (2,6) should be the spawnpoint)
    private int a = 2;
    private int b = 10;

    private long acc = 0;

    public Wave(World world) {
        this.world = world;
        
        enemiesToSpawn = new ArrayList<>(1);
        Queue<Pair<Pair<Integer,Integer>,Long>> tempQueue = new LinkedList<>(); 
        tempQueue.add(new Pair<>(new Pair<>(a, b), timeFromStart));
        tempQueue.add(new Pair<>(new Pair<>(a, b), timeFromStart+c));
        tempQueue.add(new Pair<>(new Pair<>(a, b), timeFromStart+2*c));
        tempQueue.add(new Pair<>(new Pair<>(a, b), timeFromStart+3*c));
        tempQueue.add(new Pair<>(new Pair<>(a, b), timeFromStart+4*c));

        enemiesToSpawn.add(tempQueue);
    }


    public Pair<Integer,Integer> getNext(Pair<Integer,Integer> pos) {
        return roads.getNextNode(pos);
    }

    @Override
    public void update(long elapsed) {
        acc += elapsed;

        sendEnemies();
    }

    public void sendEnemies() {
        for (int i = 0; i < enemiesToSpawn.size(); i++) {
            Queue<Pair<Pair<Integer,Integer>,Long>> queue = enemiesToSpawn.get(i); 
            while (!queue.isEmpty() && queue.peek().getY() <= acc) {
                //will switch to a same type of pairs, and erase this since hence usless
                Pair<Integer,Integer> p = queue.poll().getX();
                Point2D p2 = new Point2D(p.getX(), p.getY());
                world.getEnemiesManager().addEnemy(p2);
            }
        }
    }
}
