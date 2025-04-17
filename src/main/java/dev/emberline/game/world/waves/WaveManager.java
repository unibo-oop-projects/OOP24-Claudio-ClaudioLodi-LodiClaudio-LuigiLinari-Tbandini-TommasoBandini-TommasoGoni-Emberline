package dev.emberline.game.world.waves;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;
import dev.emberline.game.world.roads.ClosedRoads;

public class WaveManager implements Updatable {
    
    private World world;
    private ClosedRoads closedRoads = new ClosedRoads();
    //this will be one of the n waves contained in the list
    private Wave currentWave;
    
    public WaveManager(World world) {
        this.world = world;
        this.currentWave = new Wave(world);
    }
    
    public Wave getWave() {
        return this.currentWave;
    }

    @Override
    public void update(long elapsed) {
        currentWave.update(elapsed);
    }
}
