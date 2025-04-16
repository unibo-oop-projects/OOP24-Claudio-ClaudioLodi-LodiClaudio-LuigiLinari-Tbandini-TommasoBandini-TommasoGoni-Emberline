package dev.emberline.game.world.waves;

import dev.emberline.game.world.World;
import dev.emberline.game.world.roads.ClosedRoads;

public class WaveManager {
    
    private World world;
    private ClosedRoads closedRoads = new ClosedRoads();
    //this will be one of the n waves contained in the list
    private Wave currentWave = new Wave(world);
    
    public WaveManager(World world) {
        this.world = world;
    }
    
}
