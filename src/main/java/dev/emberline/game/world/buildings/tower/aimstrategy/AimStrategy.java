package dev.emberline.game.world.buildings.tower.aimstrategy;

import dev.emberline.game.model.TowerInfoProvider;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstract strategy for determining the order in which a Tower should aim at enemies.
 * The specific order is defined by subclasses that implement the {@link #compare(TowerInfoProvider, IEnemy, IEnemy)} method.
 * <p>
 * This class provides a template method, {@link #getOrder(TowerInfoProvider, List)}, which takes a list of enemies
 * and sorts them based on the comparison logic of the concrete strategy implementation.
 * <p>
 * In case of a tie in the comparison result, a deterministic tiebreaking mechanism is used
 * based on the hash codes of the enemies to ensure a consistent order across runs.
 */
public abstract class AimStrategy implements Serializable {

    @Serial
    private static final long serialVersionUID = -5749181421601555493L;

    /**
     * Determines the order in which a given Tower should target a list of enemies.
     * The sorting logic is based on a strategy defined in subclasses by implementing
     * the {@link #compare(TowerInfoProvider, IEnemy, IEnemy)} method.
     * <p>
     * In the event of a tie during sorting, a deterministic tiebreaker based on the
     * hash codes of the enemies is used.
     *
     * @param towerInfoProvider the provider that gives information about the tower which is targeting the enemies.
     * @param enemies the list of enemies to be sorted in the preferred order.
     * @return a sorted list of enemies based on the targeting strategy.
     */
    public List<IEnemy> getOrder(final TowerInfoProvider towerInfoProvider, final List<IEnemy> enemies) {
        //mutable copy of enemies
        final List<IEnemy> enemiesCopy = new ArrayList<>(enemies);
        enemiesCopy.sort((enemy1, enemy2) -> compareWithDeterministicTieBreaker(towerInfoProvider, enemy1, enemy2));
        return enemiesCopy;
    }

    /**
     * Wraps {@link AimStrategy#compare(TowerInfoProvider, IEnemy, IEnemy)} to include a deterministic tiebreaker.
     *
     * @param towerInfoProvider the provider that gives information about the tower which is targeting the enemies.
     * @param enemy1 the first enemy to compare.
     * @param enemy2 the second enemy to compare.
     * @return a negative integer if {@code enemy1} should be targeted before {@code enemy2},
     *         a positive integer if {@code enemy2} should be targeted before {@code enemy1},
     *         or zero if they are considered equal based on the targeting strategy.
     */
    private int compareWithDeterministicTieBreaker(final TowerInfoProvider towerInfoProvider,
                                                   final IEnemy enemy1, final IEnemy enemy2) {
        final int comparison = compare(towerInfoProvider, enemy1, enemy2);
        // If comparison is 0 (equal), use hashCode to break the tie deterministically.
        if (comparison == 0) {
            return Integer.compare(enemy1.hashCode(), enemy2.hashCode());
        }
        return comparison;
    }

    /**
     * Compares two enemies to determine their relative aiming strategy order.
     * The specific comparison logic is defined by subclasses that implement this method.
     *
     * @param towerInfoProvider the provider that gives information about the tower which is targeting the enemies.
     * @param enemy1 the first enemy to compare.
     * @param enemy2 the second enemy to compare.
     * @return a negative integer if {@code enemy1} should be targeted before {@code enemy2},
     *         a positive integer if {@code enemy2} should be targeted before {@code enemy1},
     *         or zero if they are considered equal based on the targeting strategy.
     */
    protected abstract int compare(TowerInfoProvider towerInfoProvider, IEnemy enemy1, IEnemy enemy2);
}
