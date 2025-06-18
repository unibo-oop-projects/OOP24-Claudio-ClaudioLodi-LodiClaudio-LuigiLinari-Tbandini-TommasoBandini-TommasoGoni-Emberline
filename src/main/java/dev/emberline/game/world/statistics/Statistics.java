package dev.emberline.game.world.statistics;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;

import java.io.Serializable;

/**
 * A class that keeps all the statistics of the game.
 */
public class Statistics implements Updatable, Serializable {

    private final World world;
    private int enemiesKilled = 0;
    private int wavesSurvived = 0;
    private long timeInGame = 0;
    private int playerHealth = 0;
    private double totalDamage = 0;
    private double dps = 0;

    private final long unitOfTime = 1_000_000_000;
    private long acc = 0;

    public Statistics(World world) {
        this.world = world;
    }

    public void updateEnemiesKilled(int enemiesKilled) {
        this.enemiesKilled = enemiesKilled;
    }

    public void updateWavesSurvived(int wavesSurvived) {
        this.wavesSurvived = wavesSurvived;
    }

    public void updateTimeInGame(long elapsed) {
        this.timeInGame += elapsed;
    }

    public void updatePlayerHealth(int playerHealth) {
        this.playerHealth = playerHealth;
    }

    public void updateTotalDamage(double damage) {
        totalDamage += damage;
    }

    public void updateDPS() {
        if (timeInGame > 0) {
            dps = totalDamage / (double) (timeInGame / unitOfTime);
        }
    }

    public int getEnemiesKilled() {
        return this.enemiesKilled;
    }

    public int getWavesSurvived() {
        return this.wavesSurvived;
    }

    public long getTimeInGame() {
        return this.timeInGame;
    }

    public int getPlayerHealth() {
        return this.playerHealth;
    }

    public double getTotalDamage() {
        return totalDamage;
    }

    public double getDPS() {
        return this.playerHealth;
    }

    @Override
    public void update(long elapsed) {
        //updateEnemiesKilled(world.getEnemiesManager().getEnemiesKilled());
        updateWavesSurvived(world.getWaveManager().getCurrentWave());
        updateTimeInGame(elapsed);
        //updatePlayerHealth();
        updateDPS();
    }
}
