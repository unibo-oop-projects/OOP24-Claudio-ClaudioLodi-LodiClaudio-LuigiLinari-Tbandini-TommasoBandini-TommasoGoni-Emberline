package dev.emberline.game.world.waves;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;

import java.io.Serializable;

/**
 * Interface that represents the manager of the waves.
 */
public interface IWaveManager extends Updatable, Serializable {

    /**
     * @return the current {@code Wave}
     */
    Wave getWave();

    /**
     * @return the number of the current wave
     */
    int getCurrentWaveIndex();

}
