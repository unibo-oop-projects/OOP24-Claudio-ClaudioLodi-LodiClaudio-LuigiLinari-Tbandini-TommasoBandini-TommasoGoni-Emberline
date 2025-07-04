package dev.emberline.game.world.statistics;

import dev.emberline.core.components.Updatable;

import java.io.Serial;
import java.io.Serializable;

/**
 * A class that keeps the statistics of the game.
 * It is made such that it does not directly use other classes,
 * but relies on decorators to gather data.
 */
public class Statistics implements Updatable, Serializable {

    @Serial
    private static final long serialVersionUID = 493178187508275976L;

    private int enemiesFought;
    private int wavesSurvived;
    private long timeInGame;
    private double totalDamage;

    /**
     * Sums the enemies that died in the current update
     * to all the other enemies already dead.
     *
     * @param enemiesFought number of enemies fought
     */
    public void updateEnemiesFought(final int enemiesFought) {
        this.enemiesFought += enemiesFought;
    }

    /**
     * Whenever the current wave finishes,
     * this method must increment by one the number of survived waves.
     */
    public void updateWavesSurvived() {
        this.wavesSurvived++;
    }

    /**
     * Keeps track of how much time is spent in the game.
     * @param elapsed the time spent in game since last call.
     */
    public void updateTimeInGame(final long elapsed) {
        this.timeInGame += elapsed;
    }

    /**
     * Keeps track of how much damage is dealt throughout the game.
     * @param damage the damage to be added to the total.
     */
    public void updateTotalDamage(final double damage) {
        totalDamage += damage;
    }

    /**
     * Returns the number of enemies killed.
     * @return the number of enemies killed
     */
    public int getEnemiesFought() {
        return this.enemiesFought;
    }

    /**
     * Returns the number of waves survived.
     * @return the number of waves survived
     */
    public int getWavesSurvived() {
        return this.wavesSurvived;
    }

    /**
     * Returns the time spent playing this series of waves.
     * @return the time spent playing this series of waves.
     */
    public long getTimeInGame() {
        return this.timeInGame;
    }

    /**
     * Returns total damage dealt by towers to enemies.
     * @return total damage dealt by towers to enemies
     */
    public double getTotalDamage() {
        return this.totalDamage;
    }

    /**
     * Should be used to update the time-dependent stats of the game.
     *
     * @param elapsed the time elapsed since the last update in nanoseconds
     */
    @Override
    public void update(final long elapsed) {
        updateTimeInGame(elapsed);
    }
}
