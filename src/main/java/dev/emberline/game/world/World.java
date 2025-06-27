package dev.emberline.game.world;

import dev.emberline.core.event.EventDispatcher;
import dev.emberline.game.GameState;
import dev.emberline.game.world.buildings.TowersManager;
import dev.emberline.game.world.entities.enemies.EnemiesManagerWithStats;
import dev.emberline.game.world.entities.enemies.IEnemiesManager;
import dev.emberline.game.world.entities.player.Player;
import dev.emberline.game.world.entities.projectiles.ProjectilesManager;
import dev.emberline.game.world.entities.projectiles.events.ProjectileHitListener;
import dev.emberline.game.world.statistics.Statistics;
import dev.emberline.game.world.waves.IWaveManager;
import dev.emberline.game.world.waves.WaveManagerWithStats;
import dev.emberline.gui.event.OpenOptionsEvent;
import dev.emberline.gui.topbar.Topbar;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.Serializable;

/**
 * Represents the game world acting as the core container and coordinator
 * of the various elements such as enemies, towers, projectiles, waves, and player interactions.
 * Mainly it makes sure that each element is properly updated and rendered.
 * It also functions as a provider of the elements inside it.
 * <p>
 * Implements the {@link GameState} interface, allowing it to be part of the game loop.
 * Implements the {@link Serializable} interface, for saving the state of the game.
 */
public class World implements GameState, Serializable {

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

    private final Player player;
    private final Topbar topbar;

    /**
     * Creates a new instance of the World class and initializes the
     * various elements inside it.
     */
    public World() {
        this.statistics = new Statistics();
        this.towersManager = new TowersManager(this);
        this.enemiesManager = new EnemiesManagerWithStats(this);
        this.waveManager = new WaveManagerWithStats(this);
        this.projectilesManager = new ProjectilesManager(this);
        this.projectileHitListener = new ProjectileHitListener(enemiesManager);
        this.player = new Player(this);
        this.worldRenderComponent = new WorldRenderComponent(waveManager);
        this.topbar = new Topbar(this);
    }

    /**
     * Returns the {@code Player} instance associated with the World.
     * @return the {@code Player} instance associated with the World.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the {@code Topbar} instance associated with the World.
     * @return the {@code Topbar} instance associated with the World.
     */
    public Topbar getTopbar() {
        return topbar;
    }

    /**
     * Returns the {@code ProjectilesManager} instance associated with the World.
     * @return the {@code ProjectilesManager} instance associated with the World.
     */
    public ProjectilesManager getProjectilesManager() {
        return projectilesManager;
    }

    /**
     * Returns the {@code TowersManager} instance associated with the World.
     * @return the {@code TowersManager} instance associated with the World.
     */
    public TowersManager getTowersManager() {
        return towersManager;
    }

    /**
     * Returns the {@code IEnemiesManager} instance associated with the World.
     * @return the {@code IEnemiesManager} instance associated with the World.
     */
    public IEnemiesManager getEnemiesManager() {
        return enemiesManager;
    }

    /**
     * Returns the {@code ProjectileHitListener} instance associated with the World.
     * @return the {@code ProjectileHitListener} instance associated with the World.
     */
    public ProjectileHitListener getProjectileHitListener() {
        return projectileHitListener;
    }

    /**
     * Returns the {@code IWaveManager} instance associated with the World.
     * @return the {@code IWaveManager} instance associated with the World.
     */
    public IWaveManager getWaveManager() {
        return waveManager;
    }

    /**
     * Returns the {@code Statistics} instance associated with the World.
     * @return the {@code Statistics} instance associated with the World.
     */
    public Statistics getStatistics() {
        return statistics;
    }


    /**
     * Updates the state of the world and its various components.
     *
     * @param elapsed the time elapsed since the last update in nanoseconds
     */
    @Override
    public void update(final long elapsed) {
        projectilesManager.update(elapsed);
        towersManager.update(elapsed);
        waveManager.update(elapsed);
        statistics.update(elapsed);
        enemiesManager.update(elapsed);
        worldRenderComponent.update(elapsed);
    }

    /**
     * Renders the world by calling the render methods of
     * all the visual elements inside it.
     * @see dev.emberline.core.components.Renderable#render()
     */
    @Override
    public void render() {
        towersManager.render();
        enemiesManager.render();
        projectilesManager.render();
        worldRenderComponent.render();
        waveManager.render();
        topbar.render();
    }

    /**
     * Processes the input event received by the World instance.
     * All the {@code inputEvent} related to the world will be
     * dispatched to the elements inside it which have input logic.
     *
     * @param inputEvent the {@link InputEvent} to be processed
     */
    @Override
    public void processInput(final InputEvent inputEvent) {
        topbar.processInput(inputEvent);
        towersManager.processInput(inputEvent);

        if (
            inputEvent instanceof final KeyEvent keyEvent
            && keyEvent.getEventType() == KeyEvent.KEY_PRESSED
            && keyEvent.getCode() == KeyCode.ESCAPE
        ) {
                EventDispatcher.getInstance().dispatchEvent(new OpenOptionsEvent(this));
        }
    }
}
