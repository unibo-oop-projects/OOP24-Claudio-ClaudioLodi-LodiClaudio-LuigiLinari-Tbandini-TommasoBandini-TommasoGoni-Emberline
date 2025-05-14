package dev.emberline.game.world.spawnpoints;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import utility.IntegerPoint2D;
import utility.Pair;

public class Spawnpoints {
    //for each spawnpoint there's a queue of enemies to spawn at a given time
    private final List<Pair<IntegerPoint2D,Queue<Long>>> enemiesToSpawn = new ArrayList<>();
    //temp variables to delete later
    private long timeFromStart = 5_000_000_000L;
    private long c = 2_000_000_000L;

    public Spawnpoints(String wavePath) {
        loadSpawnpoints(wavePath + "spawnpoints.txt");
    }

    /*
     * this method returns the list of enemies that must be spawned at the current time
     * note that right now the enemies are all the same type
     * this may be changed later
     */
    public List<IntegerPoint2D> getEnemies(Long time) {
        List<IntegerPoint2D> enemiesList = new LinkedList<>();
        for (int i = 0; i < enemiesToSpawn.size(); i++) {
            Queue<Long> queue = enemiesToSpawn.get(i).getY();
            while (!queue.isEmpty() && queue.peek() <= time) {
                enemiesList.add(enemiesToSpawn.get(i).getX());
                queue.poll();
            }
        }
        return enemiesList;
    }

    private void loadSpawnpoints(String file) {
        try {
            final BufferedReader r = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = r.readLine()) != null) {
                
                String[] numbers = line.split(" ");
                //update these lines in case of new enemy types
                Queue<Long> enemiesQueue = new LinkedList<>();
                for (int i = 0; i < Integer.parseInt(numbers[2]); i++) {
                    enemiesQueue.add(timeFromStart + c*i);
                }
                enemiesToSpawn.add(new Pair<>(new IntegerPoint2D(Integer.parseInt(numbers[1]), Integer.parseInt(numbers[0])), enemiesQueue));
                //end
            }
            r.close();
        } catch (IOException e) {
            System.out.println("error loading file: " + file);
        }
    }
}
