package dev.emberline.game.world.waves;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WaveManager implements Updatable {
    
    private World world;
    //this will be one of the n waves contained in the list
    private List<Wave> waves = new ArrayList<>();
    private Integer currentWave = 0;
    private Integer nWaves = 2;

    public WaveManager(World world) {
        this.world = world;

        for (int i = 0; i < nWaves; i++) {
            waves.add(new Wave(world, "/loadingFiles/wave" + i + "/"));
        }
    }
    
    public Wave getWave() {
        return this.waves.get(currentWave);
    }

    @Override
    public void update(long elapsed) {
        this.waves.get(currentWave).update(elapsed);

        if (waves.get(currentWave).isOver() && currentWave+1 < waves.size()) {
            currentWave++;
        }
    }
}
