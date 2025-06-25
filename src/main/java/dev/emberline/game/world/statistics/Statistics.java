package dev.emberline.game.world.statistics;

import dev.emberline.core.components.Updatable;

import java.io.Serializable;

/**
 * A class that keeps the statistics of the game.
 * It is made such that it does not directly use other classes,
 * but relies on decorators to gather data.
 */
public class Statistics implements Updatable, Serializable {

    private int enemiesKilled;
    private int wavesSurvived;
    private long timeInGame;
    private double totalDamage;

    /**
     * Sums the enemies that died in the current update
     * to all the other enemies already dead.
     *
     * @param enemiesKilled number of enemies killed
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
     * @return total damage dealt by towers to enemies
     */
    public double getTotalDamage() {
        return this.totalDamage;
    }

    private void printToTerminal() {
        System.out.println(enemiesKilled);
        System.out.println(wavesSurvived);
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
        //printToTerminal();
    }
}
