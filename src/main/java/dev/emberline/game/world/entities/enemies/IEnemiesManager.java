package dev.emberline.game.world.entities.enemies;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Vector2D;

import java.io.Serializable;
import java.util.List;

/**
 * This interface represents the pool of enemies within a game world, providing functionality for adding an enemy to the pool,
 * updating and rendering the entire pool and also querying which enemies are in a given area.
 * It also provides methods to check if all enemies are dead.
 */
public interface IEnemiesManager extends Updatable, Renderable, Serializable {

    /**
     * Adds a new enemy to the spatial hash grid in the game world.
     * The enemy is then wrapped with additional statistics functionality and added to the
     * spatial hash grid for management.
     *
     * @param spawnPoint the location in the game world where the enemy will be spawned
     * @param type the type of the enemy to be created
     */
    void addEnemy(Vector2D spawnPoint, EnemyType type);

    /**
     * Retrieves a list of enemies that are within a specified radius of a given location
     * and are in a hittable state.
     *
     * @param location the central location from where to search for nearby enemies
     * @param radius the radius within which to search for nearby enemies
     * @return a list of hittable enemies within the given radius of the specified location
     */
    List<IEnemy> getNear(Vector2D location, double radius);

    /**
     * Returns whether all enemies have been killed.
     * @return whether all enemies have been killed
     */
    boolean areAllDead();

    /**
     * Updates all enemy entities managed by the {@code EnemiesManager}.
     * Active enemies are marked for an update in the spatial hash grid, while dead enemies are marked for removal.
     *
     * @param elapsed the time elapsed since the last update in nanoseconds
     */
    @Override
    void update(long elapsed);

    /**
     * Renders all active enemy entities managed by this {@code EnemiesManager}.
     *
     * @see IEnemy#render()
     */
    @Override
    void render();
}
