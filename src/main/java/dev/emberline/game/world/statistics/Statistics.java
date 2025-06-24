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
    private double totalDamage = 0;
    private double dps = 0;

    private final long unitOfTime = 1_000_000_000;
    private final long acc = 0;

    /**
     * this class is relative to
     *
     * @param world
     */
    public Statistics(final World world) {
        this.world = world;
    }

    /**
     * Sums the enemies that died in the current update
     * to all the other enemies already dead.
     *
     * @param enemiesKilled
     */
    public void updateEnemiesKilled(final int enemiesKilled) {
        this.enemiesKilled += enemiesKilled;
    }

    /**
     * Whenever the current wave finishes,
     * this method must increment by one the number of survived waves.
     */
    public void updateWavesSurvived() {
        this.wavesSurvived++;
    }

    /**
     * sums @param elapsed to the current time spent in game.
     */
    public void updateTimeInGame(final long elapsed) {
        this.timeInGame += elapsed;
    }

    /**
     * Sums @param damage to the total damage already dealt by the towers to the enemies.
     */
    public void updateTotalDamage(final double damage) {
        totalDamage += damage;
    }

    /**
     * Updates the dps by dividing total damage dealt by total time passed (in seconds).
     */
    private void updateDPS() {
        if (timeInGame > 0) {
            dps = totalDamage / (double) (timeInGame / unitOfTime);
        }
    }

    /**
     * @return the number of enemies killed
     */
    public int getEnemiesKilled() {
        return this.enemiesKilled;
    }

    /**
     * @return number of waves survived
     */
    public int getWavesSurvived() {
        return this.wavesSurvived;
    }

    /**
     * @return the time spent playing this series of waves.
     */
    public long getTimeInGame() {
        return this.timeInGame;
    }

    /**
     * @return average damage per second,
     * dealt by the towers to the enemies.
     */
    public double getDPS() {
        return this.dps;
    }

    private void printToTerminal() {
        System.out.println(enemiesKilled);
        System.out.println(wavesSurvived);
        System.out.println(dps);
        System.out.println(timeInGame);
    }

    /**
     * Should be used to update the time-dependent stats of the game
     *
     * @param elapsed the time elapsed since the last update in nanoseconds
     */
    @Override
    public void update(final long elapsed) {
        updateTimeInGame(elapsed);
        //updatePlayerHealth();
        updateDPS();
        //printToTerminal();
    }
}
