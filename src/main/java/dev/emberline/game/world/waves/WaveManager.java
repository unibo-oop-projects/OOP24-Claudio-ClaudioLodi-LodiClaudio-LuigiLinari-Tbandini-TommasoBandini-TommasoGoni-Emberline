package dev.emberline.game.world.waves;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.game.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that keeps track of all the waves, and the current wave.
 */
public class WaveManager implements IWaveManager {

    private final List<Wave> waves = new ArrayList<>();
    private int currentWaveIndex;

    private static final String WAVES_CONFIG_PATH = "/world/waves/waves.json";
    private static final WavesConfig WAVES_CONFIG = ConfigLoader.loadConfig(WAVES_CONFIG_PATH, WavesConfig.class);

    // Loading waves from resources
    private static class WavesConfig {
        @JsonProperty
        String[] wavePaths;
    }

    /**
     * Creates a new instance of {@code WaveManager}
     *
     * @param world is the reference to the World
     */
    public WaveManager(final World world) {
        for (final String wavePath : WAVES_CONFIG.wavePaths) {
            if (wavePath == null || wavePath.isEmpty()) {
                throw new IllegalArgumentException("Wave path cannot be null or empty");
            }
            waves.add(new Wave(world, wavePath));
        }
    }

    /**
     * @return the current {@code Wave}
     */
    @Override
    public Wave getWave() {
        return this.waves.get(currentWaveIndex);
    }

    /**
     * @return the number of the current wave
     */
    @Override
    public int getCurrentWaveIndex() {
        return currentWaveIndex;
    }

    /**
     * Updates the current wave and check weather it is over.
     *
     * @param elapsed the time elapsed in nanoseconds since the last update call
     */
    @Override
    public void update(final long elapsed) {
        getWave().update(elapsed);

        if (getWave().isOver() && currentWaveIndex + 1 < waves.size()) {
            currentWaveIndex++;
        }
    }

    @Override
    public void render() {
        getWave().render();
    }
}
