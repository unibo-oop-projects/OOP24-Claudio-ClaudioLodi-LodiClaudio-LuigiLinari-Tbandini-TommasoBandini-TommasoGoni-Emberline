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
    private String wavesPath = "/loadingFiles/wave";

    public WaveManager(World world) {
        this.world = world;
        //maybe get number of waves from file
        for (int i = 0; i < 2; i++) {
            waves.add(new Wave(world, wavesPath + i + "/"));
        }
    }
    
    public Wave getWave() {
        return this.waves.get(currentWave);
    }

    @Override
    public void update(long elapsed) {
        this.waves.get(currentWave).update(elapsed);
    }
}
