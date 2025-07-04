package dev.emberline.game.world.waves;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;

import java.io.Serializable;

/**
 * Interface that represents the manager of the waves.
 */
public interface IWaveManager extends Updatable, Renderable, Serializable {

    /**
     * Returns the current {@code Wave}.
     * @return the current {@code Wave}
     */
    Wave getWave();

    /**
     * Returns the number of the current wave.
     * @return the number of the current wave
     */
    int getCurrentWaveIndex();

    /**
     * Returns the total number of waves.
     * @return the total number of waves.
     */
    int getNumberOfWaves();
}
