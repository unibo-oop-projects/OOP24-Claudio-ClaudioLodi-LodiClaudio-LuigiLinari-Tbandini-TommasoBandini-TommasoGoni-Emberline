package dev.emberline.game.world.buildings.tower.aimstrategy.concrete;

import dev.emberline.game.model.TowerInfoProvider;
import dev.emberline.game.world.buildings.tower.aimstrategy.AimStrategy;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;

import java.io.Serial;
import java.io.Serializable;

/**
 * A concrete implementation of the {@code AimStrategy} that determines the targeting order
 * based on the health of the enemies.
 * The enemy with the highest health is prioritized.
 */
public class StrongAimStrategy extends AimStrategy implements Serializable {

    @Serial
    private static final long serialVersionUID = 8321157139754033337L;

    /**
     * Compares two enemies based on their health.
     * The enemy with the highest health is prioritized.
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
        return Double.compare(enemy2.getHealth(), enemy1.getHealth());
    }
}
