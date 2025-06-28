package dev.emberline.game.world.buildings.tower;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.IEnemiesManager;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

class TowerUpdateComponent implements Updatable, Serializable {
    @Serial
    private static final long serialVersionUID = 5273964472174671968L;

    private long accumulatedTimeNs;

    private final World world;
    private final Tower tower;

    TowerUpdateComponent(final World world, final Tower tower) {
        this.world = world;
        this.tower = tower;
    }

    /**
     * Updates the tower by handling the logic for projectile shooting based on elapsed time
     * and nearby enemies within the tower's range.
     * <p>
     * This method checks whether the tower is ready to shoot (based on its fire rate and the
     * elapsed time since the last shot) and attempts to fire at enemies within its range.
     * If a projectile is successfully created and fired, the accumulated time for shooting resets.
     *
     * @param elapsed the elapsed time in nanoseconds since the last update
     */
    @Override
    public void update(final long elapsed) {
        final long shootingInterval = (long) (1e9 / tower.getProjectileInfo().getFireRate());

        accumulatedTimeNs += elapsed;
        if (accumulatedTimeNs < shootingInterval) {
            return;
        }

        // Shooting
        final IEnemiesManager enemiesManager = world.getEnemiesManager();

        final List<IEnemy> nearEnemies = enemiesManager.getNear(
                tower.getPosition(),
                tower.getProjectileInfo().getTowerRange()
        );
        final List<IEnemy> aimOrder = tower.getAimType().getAimStrategy().getOrder(tower, nearEnemies);

        for (final IEnemy enemyToShoot : aimOrder) {
            final boolean creationSucceeded = world.getProjectilesManager().addProjectile(
                    tower.getFiringWorldCenterLocation(), enemyToShoot, tower.getProjectileInfo(), tower.getEnchantmentInfo()
            );

            if (creationSucceeded) {
                accumulatedTimeNs = 0;
                break;
            }
        }
    }

    public void setAccumulatedTimeNs(final long accumulatedTimeNs) {
        this.accumulatedTimeNs = accumulatedTimeNs;
    }
}
