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

    public WaveManagerWithStats(World world) {
        waveManager = new WaveManager(world);
        statistics = world.getStatistics();
    }

    @Override
    public Wave getWave() {
        return waveManager.getWave();
    }

    @Override
    public int getCurrentWaveIndex() {
        return waveManager.getCurrentWaveIndex();
    }

    /**
     * Updates the Wavemanager.
     * Keeps record of the last wave reached.
     *
     * @param elapsed
     */
    @Override
    public void update(long elapsed) {
        int nWavePreUpdate = getCurrentWaveIndex();
        waveManager.update(elapsed);
        int nWavePostUpdate = getCurrentWaveIndex();

        if (nWavePostUpdate - nWavePreUpdate > 0) statistics.updateWavesSurvived();
    }

    @Override
    public void render() {
        waveManager.render();
    }
}
