package dev.emberline.game.world;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.entities.enemy.EnemiesManager;
import dev.emberline.game.world.waves.WaveManager;

public class World implements Updatable, Renderable {
    
    // Enemies
    private final EnemiesManager enemiesManager;
    // Towers
    // Waves
    private final WaveManager waveManager;

    public World() {
        this.enemiesManager = new EnemiesManager(this);
        this.waveManager = new WaveManager(this);
    }

    public EnemiesManager getEnemiesManager() {
        return enemiesManager;
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }

    @Override
    public void update(long elapsed) {
        enemiesManager.update(elapsed);
        waveManager.update(elapsed);
    }

    @Override
    public void render() {
        enemiesManager.render();
        waveManager.getWave().render();
    }
}
