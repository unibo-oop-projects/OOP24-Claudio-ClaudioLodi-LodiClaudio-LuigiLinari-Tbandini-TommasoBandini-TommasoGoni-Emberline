package dev.emberline.game.world.statistics;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;

import java.io.Serializable;

/**
 * A class that keeps the statistics of the game.
 * It is made such that it does not directly use other classes,
 * but relies on decorators to gather data.
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

    /**
     * this class is relative to
     * @param world
     */
    public Statistics(World world) {
        this.world = world;
    }

    /**
     * Sums the enemies that died in the current update
     * to all the other enemies already dead.
     * @param enemiesKilled
     */
    public void updateEnemiesKilled(int enemiesKilled) {
        this.enemiesKilled += enemiesKilled;
    }

    /**
     * Whenever the current wave finishes this method increments by one the counter.
     */
    public void updateWavesSurvived() {
        this.wavesSurvived++;
    }

    /**
     * sums @param elapsed to the current time spent in game.
     */
    public void updateTimeInGame(long elapsed) {
        this.timeInGame += elapsed;
    }

    public void updatePlayerHealth(int playerHealth) {
        this.playerHealth = playerHealth;
    }

    /**
     * Sums @param damage to the total damage already dealt by the towers to the enemies.
     */
    public void updateTotalDamage(double damage) {
        totalDamage += damage;
    }

    private void updateDPS() {
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

    void printToTerminal() {
        System.out.println(enemiesKilled);
        System.out.println(wavesSurvived);
        System.out.println(dps);
        System.out.println(timeInGame);
        //System.out.println(enemiesKilled);
    }

    @Override
    public void update(long elapsed) {
        updateTimeInGame(elapsed);
        //updatePlayerHealth();
        updateDPS();
        printToTerminal();
    }
}
