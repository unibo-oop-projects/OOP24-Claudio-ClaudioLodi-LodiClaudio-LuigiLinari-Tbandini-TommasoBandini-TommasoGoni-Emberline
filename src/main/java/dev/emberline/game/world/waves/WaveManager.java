package dev.emberline.game.world.waves;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;

import java.util.ArrayList;
import java.util.List;

public class WaveManager implements Updatable {
    
    private final World world;
    //this will be one of the n waves contained in the list
    private final List<Wave> waves = new ArrayList<>();
    private Integer currentWave = 0;
    private final Integer nWaves = 2;

    public WaveManager(World world) {
        this.world = world;

        for (int i = 0; i < nWaves; i++) {
            waves.add(new Wave(world, "/loadingFiles/wave" + i + "/"));
        }
    }

    /**
     * @return the current wave.
     */
    public Wave getWave() {
        return this.waves.get(currentWave);
    }

    /**
     * @param elapsed
     * Updates the current wave and check weather it is over.
     */
    @Override
    public void update(long elapsed) {
        this.waves.get(currentWave).update(elapsed);

        if (waves.get(currentWave).isOver() && currentWave+1 < waves.size()) {
            currentWave++;
        }
    }
}
