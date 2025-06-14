package dev.emberline.game.world.spawnpoints;

import utility.Coordinate2D;
import utility.Pair;
import utility.Vector2D;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

/**
 * This class represents spawnpoints as containers of enemies to spawn at a given time
 */
public class Spawnpoints {
    /**
     * Data structure that for each spawnpoint, keeps a queue of enemies to spawn at a given time
     */
    private final List<Pair<Vector2D,Queue<Pair<String, Long>>>> enemiesToSpawn = new ArrayList<>();
    //temp variables to delete later
    private long timeFromStart = 5_000_000_000L;

    /**
     * Creates a new instance of {@code Spawnpoints}
     * @param wavePath represents the path of the files regarding the current wave
     */
    public Spawnpoints(String wavePath) {
        loadSpawnpoints(wavePath + "spawnpoints.txt");
    }

    /**
     * @return true if there are more enemies to spawn
     * in the current wave, false otherwise.
     */
    public boolean hasMoreToSpawn() {
        for (var spawnpoint : enemiesToSpawn) {
            if (!spawnpoint.getY().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method returns the list of enemies that must be spawned at the current time.
     * note that right now the enemies are all the same type
     * this may be changed later
     */
    public List<Pair<Vector2D, String>> getEnemies(Long time) {
        List<Pair<Vector2D, String>> enemiesList = new LinkedList<>();
        for (int i = 0; i < enemiesToSpawn.size(); i++) {
            Queue<Pair<String, Long>> queue = enemiesToSpawn.get(i).getY();
            var position = enemiesToSpawn.get(i).getX();
            while (!queue.isEmpty() && queue.peek().getY() <= time) {
                enemiesList.add(new Pair<>(position, queue.poll().getX()));
            }
        }
        return enemiesList;
    }

    private void loadSpawnpoints(String file) {
        try {
            URL fileURL = Objects.requireNonNull(getClass().getResource(file));
            final BufferedReader r = new BufferedReader( new FileReader(fileURL.getPath()));
            String line = null;
            while ((line = r.readLine()) != null) {
                
                String[] coordinates = line.split(" ");
                Queue<Pair<String, Long>> enemiesQueue = new LinkedList<>();

                while ((line = r.readLine()) != null && !line.equals("/")) {
                    String[] enemyGroup = line.split(" ");

                    double groupSpawnTime = Double.parseDouble(enemyGroup[0]) + timeFromStart;
                    double deltaT = Double.parseDouble(enemyGroup[1]);

                    for (int i = 2; i < enemyGroup.length; i++) {
                        long spawnTime = (long) (i * deltaT * 1_000_000_000 + groupSpawnTime);
                        String type = enemyGroup[i];
                        enemiesQueue.add(new Pair<>(type, spawnTime));
                    }
                }
                //summing (0.5, 0.5) to center
                enemiesToSpawn.add(new Pair<>(
                        new Coordinate2D(Double.parseDouble(coordinates[0]) + 0.5, Double.parseDouble(coordinates[1]) + 0.5), enemiesQueue));
            }
            r.close();
        } catch (IOException e) {
            System.out.println("error loading file: " + file);
        }
    }
}
