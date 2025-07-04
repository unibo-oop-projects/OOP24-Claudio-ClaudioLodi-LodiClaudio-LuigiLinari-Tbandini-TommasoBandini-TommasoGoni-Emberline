package dev.emberline.game.world.waves;

import dev.emberline.game.world.World;
import dev.emberline.game.world.statistics.Statistics;

import java.io.Serial;

/**
 * This class is a decorator for the WaveManager.
 * Its purpose is to gather stats (ex: enemies killed)
 */
public class WaveManagerWithStats implements IWaveManager {

    @Serial
    private static final long serialVersionUID = -1311529313996617055L;

    private final IWaveManager waveManager;
    private final Statistics statistics;
    private int nWavePre;
    private boolean hasWon;

    /**
     * Constructs a new instance of {@code WaveManagerWithStats}.
     * @param world the {@code World} instance within which this wave manager operates.
     * @see WaveManagerWithStats
     */
    public WaveManagerWithStats(final World world) {
        waveManager = new WaveManager(world);
        statistics = world.getStatistics();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Wave getWave() {
        return waveManager.getWave();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCurrentWaveIndex() {
        return waveManager.getCurrentWaveIndex();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfWaves() {
        return waveManager.getNumberOfWaves();
    }

    /**
     * Updates the WaveManager.
     * Updates the statistics class with the last wave reached.
     *
     * @param elapsed the time elapsed in nanoseconds since the last update call
     */
    @Override
    public void update(final long elapsed) {
        final int nWavePost = getCurrentWaveIndex();

        if (nWavePost - nWavePre > 0 || (nWavePost + 1 == getNumberOfWaves() && getWave().isOver())) {
            statistics.updateWavesSurvived();
        }
        nWavePre = nWavePost;

        waveManager.update(elapsed);
    }

    /**
     * Renders the wave manager.
     * @see WaveManager#render()
     */
    @Override
    public void render() {
        waveManager.render();
    }
}
