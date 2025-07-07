package dev.emberline.game.world.buildings.tower.aimstrategy.concrete;

import dev.emberline.game.model.TowerInfoProvider;
import dev.emberline.game.world.buildings.tower.aimstrategy.AimStrategy;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;

import java.io.Serial;
import java.io.Serializable;

/**
 * A concrete implementation of the {@code AimStrategy} that determines the targeting order
 * based on the distance between the tower and the enemies.
 * The enemy closer to the tower is prioritized.
 */
public final class CloseAimStrategy extends AimStrategy implements Serializable {
    @Serial
    private static final long serialVersionUID = -2850713378131297136L;

    /**
     * Compares two enemies based on their distance from the tower's current position.
     * The enemy closer to the tower is prioritized.
     *
     * @param towerInfoProvider the provider that gives information about the tower which is targeting the enemies.
     * @param enemy1 the first enemy to compare.
     * @param enemy2 the second enemy to compare.
     * @return a negative integer if {@code enemy1} should be targeted before {@code enemy2},
     *         a positive integer if {@code enemy2} should be targeted before {@code enemy1},
     *         or zero if they are considered equal based on the targeting strategy.
     *
     * @see AimStrategy#compare(TowerInfoProvider, IEnemy, IEnemy)
     */
    @Override
    protected int compare(final TowerInfoProvider towerInfoProvider, final IEnemy enemy1, final IEnemy enemy2) {
        final double distance1 = towerInfoProvider.getPosition().distance(enemy1.getPosition());
        final double distance2 = towerInfoProvider.getPosition().distance(enemy2.getPosition());
        return Double.compare(distance1, distance2);
    }
}
