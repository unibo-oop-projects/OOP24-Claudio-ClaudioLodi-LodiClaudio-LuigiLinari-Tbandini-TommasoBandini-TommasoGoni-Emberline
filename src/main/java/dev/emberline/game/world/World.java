package dev.emberline.game.world;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.entities.enemies.EnemiesManagerWithStats;
import dev.emberline.game.world.entities.enemies.IEnemiesManager;
import dev.emberline.game.world.entities.projectiles.ProjectilesManager;
import dev.emberline.game.world.entities.projectiles.events.ProjectileHitListener;
import dev.emberline.game.world.statistics.Statistics;
import dev.emberline.game.world.buildings.TowersManager;
import dev.emberline.game.world.waves.IWaveManager;
import dev.emberline.game.world.waves.WaveManagerWithStats;
import javafx.scene.input.InputEvent;

import java.io.Serializable;

public class World implements Updatable, Renderable, Inputable, Serializable {

    private final WorldRenderComponent worldRenderComponent;
    // Enemies
    private final IEnemiesManager enemiesManager;
    // Towers
    private final TowersManager towersManager;
    // Projectiles
    private final ProjectilesManager projectilesManager;
    // Waves
    private final IWaveManager waveManager;

    private final Statistics statistics;
    // HitListener
    private final ProjectileHitListener projectileHitListener;

    public World() {
        this.statistics = new Statistics(this);
        this.towersManager = new TowersManager(this);
        this.enemiesManager = new EnemiesManagerWithStats(this);
        this.waveManager = new WaveManagerWithStats(this);
        this.projectilesManager = new ProjectilesManager(this);
        this.projectileHitListener = new ProjectileHitListener(enemiesManager);
        this.worldRenderComponent = new WorldRenderComponent(waveManager);
    }

    public ProjectilesManager getProjectilesManager() {
        return projectilesManager;
    }

    public TowersManager getTowersManager() {
        return towersManager;
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
        towersManager.update(elapsed);
        waveManager.update(elapsed);
        statistics.update(elapsed);
        enemiesManager.update(elapsed);
        worldRenderComponent.update(elapsed);
    }

    @Override
    public void render() {
        towersManager.render();
        enemiesManager.render();
        projectilesManager.render();
        worldRenderComponent.render();
    }

    @Override
    public void processInput(InputEvent inputEvent) {
        towersManager.processInput(inputEvent);
    }
}
