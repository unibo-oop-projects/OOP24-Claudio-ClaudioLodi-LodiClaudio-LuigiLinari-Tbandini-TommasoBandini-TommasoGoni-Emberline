package dev.emberline.game.world;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.entities.enemy.EnemiesManager;

public class World implements Updatable, Renderable {
    
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

    @Override
    public void update(long elapsed) {
        enemiesManager.update(elapsed);
    }

    @Override
    public void render() {
        enemiesManager.render();
    }
}
