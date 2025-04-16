package dev.emberline.game.world;

import dev.emberline.game.world.entities.enemy.EnemiesManager;

public class World {
    
    // Enemies
    private final EnemiesManager enemiesManager;
    // Towers
    // Waves

    public World() {
        this.enemiesManager = new EnemiesManager(this);
    }

    public EnemiesManager getEnemiesManager() {
        return enemiesManager;
    }
}
