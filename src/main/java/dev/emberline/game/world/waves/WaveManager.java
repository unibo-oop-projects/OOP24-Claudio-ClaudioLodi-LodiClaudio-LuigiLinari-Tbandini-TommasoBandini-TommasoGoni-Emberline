package dev.emberline.game.world.waves;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that keeps track of all the waves, and the current wave.
 */
public class WaveManager implements Updatable, Renderable, Serializable {
    
    private final World world;
    //this will be one of the n waves contained in the list
    private final List<Wave> waves = new ArrayList<>();
    private Integer currentWave = 0;
    private final Integer nWaves = 2;

    /**
     * Creates a new instance of {@code WaveManager}
     * @param world is the reference to the World
     */
    public WaveManager(World world) {
        this.world = world;

        for (int i = 0; i < nWaves; i++) {
            waves.add(new Wave(world, "/loadingFiles/wave" + i + "/"));
        }
    }

    /**
     * @return the current {@code Wave}
     */
    public Wave getWave() {
        return this.waves.get(currentWave);
    }

    /**
     * @return the number of the current wave
     */
    public int getCurrentWave() {
        return currentWave;
    }

    /**
     * Updates the current wave and check weather it is over.
     * @param elapsed
     */
    @Override
    public void update(long elapsed) {
        this.waves.get(currentWave).update(elapsed);

        if (waves.get(currentWave).isOver() && currentWave+1 < waves.size()) {
            currentWave++;
        }
    }

    @Override
    public void render() {
        waves.get(currentWave).render();
    }
}
