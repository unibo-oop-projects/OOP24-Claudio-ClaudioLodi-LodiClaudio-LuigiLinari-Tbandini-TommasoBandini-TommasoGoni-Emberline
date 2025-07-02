package dev.emberline.game.world.entities.enemies;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.config.ConfigLoader;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.game.world.entities.enemies.enemy.EnemyWithStats;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Vector2D;

import java.io.Serial;
import java.util.LinkedList;
import java.util.List;

/**
 * An implementation of the {@link IEnemiesManager}.
 * This class uses a spatial hash grid for efficient spatial queries.
 */
public class EnemiesManager implements IEnemiesManager {

    @Serial
    private static final long serialVersionUID = -413131806664877498L;

    private final EnemiesFactory enemiesFactory = new EnemiesFactory();

    private final SpatialHashGrid spatialHashGrid;

    private final World world;

    private record WorldBounds(
        @JsonProperty
        int topLeftX,
        @JsonProperty
        int topLeftY,
        @JsonProperty
        int bottomRightX,
        @JsonProperty
        int bottomRightY
    ) {
        // Data validation
        private WorldBounds {
            if (topLeftX >= bottomRightX || topLeftY >= bottomRightY) {
                throw new IllegalArgumentException("Invalid world bounds: " + this);
            }
        }
    }

    /**
     * Constructs an instance of the EnemiesManager.
     * This manager operates within the specified game world and initializes
     * a spatial hash grid with the bounds of the world.
     *
     * @param world the game world where this manager lives
     */
    public EnemiesManager(final World world) {
        this.world = world;

        final WorldBounds worldBounds = ConfigLoader.loadConfig("/world/worldBounds.json", WorldBounds.class);
        this.spatialHashGrid = new SpatialHashGrid(
                worldBounds.topLeftX, worldBounds.topLeftY,
                worldBounds.bottomRightX, worldBounds.bottomRightY
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEnemy(final Vector2D spawnPoint, final EnemyType type) {
        final IEnemy newEnemy = enemiesFactory.createEnemy(spawnPoint, type, world);
        final IEnemy newEnemyWrapper = new EnemyWithStats(newEnemy, world.getStatistics());
        spatialHashGrid.add(newEnemyWrapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IEnemy> getNear(final Vector2D location, final double radius) {
        final List<IEnemy> near = spatialHashGrid.getNear(location, radius);
        near.removeIf(iEnemy -> !iEnemy.isHittable());
        return near;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean areAllDead() {
        return spatialHashGrid.size() == 0;
    }

    /**
     * Returns the number of enemies currently inside the {@code EnemiesManager}.
     * @return the number of enemies currently inside the {@code EnemiesManager}.
     */
    int getAliveEnemiesNumber() {
        return spatialHashGrid.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final long elapsed) {
        final List<IEnemy> toUpdate = new LinkedList<>();
        final List<IEnemy> toRemove = new LinkedList<>();
        for (final IEnemy enemy : spatialHashGrid) {
            if (enemy.isDead()) {
                toRemove.add(enemy);
                if (enemy.getHealth() <= 0) {
                    world.getPlayer().earnGold(enemy.getGoldReward());
                }
            } else {
                enemy.update(elapsed);
                toUpdate.add(enemy);
            }
        }
        spatialHashGrid.updateAll(toUpdate);
        spatialHashGrid.removeAll(toRemove);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        for (final IEnemy enemy : spatialHashGrid) {
            enemy.render();
        }
    }
}
