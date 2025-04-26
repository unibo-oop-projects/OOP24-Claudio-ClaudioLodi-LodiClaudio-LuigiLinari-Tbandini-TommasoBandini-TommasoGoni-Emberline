package dev.emberline.game.world.waves;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;
import dev.emberline.game.world.roads.Roads;
import dev.emberline.game.world.spawnpoints.Spawnpoints;
import javafx.geometry.Point2D;
import utility.pairs.Pair;

public class Wave implements Updatable, Renderable {
    
    private World world;
    //getting the file path should be done with methods such as
    //getResources() or similar
    private Roads roads = new Roads("./src/main/resources/loadingFiles/roads.txt");
    private Spawnpoints spawnpoints = new Spawnpoints("./src/main/resources/loadingFiles/spawnpoints.txt");
    
    private long acc = 0;

    public Wave(World world) {
        this.world = world;
    }

    public Optional<Pair<Integer,Integer>> getNext(Pair<Integer,Integer> pos) {
        return roads.getNextNode(pos);
    }

    @Override
    public void update(long elapsed) {
        acc += elapsed;

        sendEnemies();
    }

    public void sendEnemies() {
        List<Pair<Integer,Integer>> enemiesQueue = spawnpoints.getEnemies(acc);
        for (var enemy : enemiesQueue) {
            //will switch to a same type of pairs, and erase this since hence usless
            Point2D p2 = new Point2D(enemy.getX(), enemy.getY());
            world.getEnemiesManager().addEnemy(p2);
        }
    }

    @Override
    public void render() {
        roads.render();
    }
}