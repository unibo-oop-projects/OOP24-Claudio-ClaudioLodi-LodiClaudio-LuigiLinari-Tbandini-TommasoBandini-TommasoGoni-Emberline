package dev.emberline.game.world;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.entities.enemies.EnemiesManager;
import dev.emberline.game.world.entities.projectiles.ProjectilesManager;
import dev.emberline.game.world.entities.projectiles.projectile.ProjectileHitListener;
import dev.emberline.game.world.waves.WaveManager;

public class World implements Updatable, Renderable {
    
    // Enemies
    private final EnemiesManager enemiesManager;
    // Towers
    // Projectiles
    private final ProjectilesManager projectilesManager;
    // Waves
    private final WaveManager waveManager;
    // HitListener
    private final ProjectileHitListener projectileHitListener;

    public World() {
        this.enemiesManager = new EnemiesManager(this);
        this.waveManager = new WaveManager(this);
        this.projectilesManager = new ProjectilesManager();
        projectileHitListener = new ProjectileHitListener(enemiesManager);
    }

    public EnemiesManager getEnemiesManager() {
        return enemiesManager;
    }

    public ProjectileHitListener getProjectileHitListener() {
        return projectileHitListener;
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }

    @Override
    public void update(long elapsed) {
        enemiesManager.update(elapsed);
        waveManager.update(elapsed);
        projectilesManager.update(elapsed);
    }

    @Override
    public void render() {
        enemiesManager.render();
        projectilesManager.render();
        waveManager.getWave().render(); // TODO (the world should only call on managers)
    }
}
