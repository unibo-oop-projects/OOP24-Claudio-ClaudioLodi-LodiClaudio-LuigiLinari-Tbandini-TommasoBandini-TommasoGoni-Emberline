package dev.emberline.game.world;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.entities.enemies.EnemiesManager;
import dev.emberline.game.world.entities.enemies.EnemiesManagerWithStats;
import dev.emberline.game.world.entities.enemies.IEnemiesManager;
import dev.emberline.game.world.statistics.Statistics;
import dev.emberline.game.world.entities.projectiles.ProjectilesManager;
import dev.emberline.game.world.entities.projectiles.projectile.ProjectileHitListener;
import dev.emberline.game.world.waves.IWaveManager;
import dev.emberline.game.world.waves.WaveManager;
import dev.emberline.game.world.waves.WaveManagerWithStats;

import java.io.Serializable;

public class World implements Updatable, Renderable, Serializable {

    // Enemies
    private final IEnemiesManager enemiesManager;
    // Towers
    // Projectiles
    private final ProjectilesManager projectilesManager;
    // Waves
    private final IWaveManager waveManager;

    private final Statistics statistics;
    // HitListener
    private final ProjectileHitListener projectileHitListener;

    public World() {
        this.enemiesManager = new EnemiesManagerWithStats(this);
        this.waveManager = new WaveManagerWithStats(this);
        this.statistics = new Statistics(this);
        this.projectilesManager = new ProjectilesManager();
        this.projectileHitListener = new ProjectileHitListener(enemiesManager);
    }

    public IEnemiesManager getEnemiesManager() {
        return enemiesManager;
    }

    public ProjectileHitListener getProjectileHitListener() {
        return projectileHitListener;
    }

    public IWaveManager getWaveManager() {
        return waveManager;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    @Override
    public void update(long elapsed) {
        projectilesManager.update(elapsed);
        waveManager.update(elapsed);
        statistics.update(elapsed);
        enemiesManager.update(elapsed);
    }

    @Override
    public void render() {
        enemiesManager.render();
        projectilesManager.render();
        waveManager.render();
    }
}
