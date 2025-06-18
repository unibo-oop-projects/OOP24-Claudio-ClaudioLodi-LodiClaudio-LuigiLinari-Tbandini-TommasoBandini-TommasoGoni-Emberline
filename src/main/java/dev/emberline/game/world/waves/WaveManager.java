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
public class WaveManager implements IWaveManager {
    
    private final World world;
    private final List<Wave> waves = new ArrayList<>();
    private Integer currentWave = 0;
    private final Integer nWaves = 2;

    public WaveManager(World world) {
        this.world = world;

        for (int i = 0; i < nWaves; i++) {
            waves.add(new Wave(world, "/loadingFiles/wave" + i + "/"));
        }
    }

    public Wave getWave() {
        return this.waves.get(currentWave);
    }

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
