package dev.emberline.game.world.waves;

import dev.emberline.game.world.World;
import dev.emberline.game.world.statistics.Statistics;

/**
 * This class is a decorator for the Wavemanager.
 * Its purpose is to gather stats (ex: enemies killed)
 */
public class WaveManagerWithStats implements IWaveManager {

    private final IWaveManager waveManager;
    private final Statistics statistics;

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
     * Updates the Wavemanager.
     * Keeps record of the last wave reached.
     *
     * @param elapsed the time elapsed in nanoseconds since the last update call
     */
    @Override
    public void update(final long elapsed) {
        final int nWavePreUpdate = getCurrentWaveIndex();
        waveManager.update(elapsed);
        final int nWavePostUpdate = getCurrentWaveIndex();

        if (nWavePostUpdate - nWavePreUpdate > 0) {
            statistics.updateWavesSurvived();
        }
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
