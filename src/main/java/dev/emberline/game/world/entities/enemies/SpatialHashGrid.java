package dev.emberline.game.world.entities.enemies;

import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Vector2D;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The SpatialHashGrid class is a data structure designed for efficient
 * spatial partitioning and querying of {@code IEnemy} objects in a two-dimensional space.
 * It divides the space into a grid of cells and assigns objects to cells based on
 * their positions.
 */
public class SpatialHashGrid implements Iterable<IEnemy>, Serializable {

    @Serial
    private static final long serialVersionUID = -763266007228997533L;

    private static final int CELL_SIZE = 1;

    private final int xMin, yMin;
    private final int xMax, yMax;

    private final int cols;
    private final int rows;

    private final List<List<Set<IEnemy>>> spatialHashGrid;
    private final Map<IEnemy, CellIdx> enemyCell = new HashMap<>();

    private int size = 0;

    private record CellIdx(int x, int y) {
    }

    /**
     * Constructs a SpatialHashGrid given the bounds (inclusive) of the space the data structure must keep track.
     *
     * @param xMin the minimum x-coordinate of the grid's boundary
     * @param yMin the minimum y-coordinate of the grid's boundary
     * @param xMax the maximum x-coordinate of the grid's boundary
     * @param yMax the maximum y-coordinate of the grid's boundary
     */
    public SpatialHashGrid(final int xMin, final int yMin, final int xMax, final int yMax) {
        this.cols = (int) Math.ceil((double) (xMax - xMin) / CELL_SIZE) + 1;
        this.rows = (int) Math.ceil((double) (yMax - yMin) / CELL_SIZE) + 1;

        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;

        this.spatialHashGrid = new ArrayList<>();
        for (int x = 0; x < cols; x++) {
            this.spatialHashGrid.add(new ArrayList<>());
            for (int y = 0; y < rows; y++) {
                spatialHashGrid.get(x).add(new HashSet<>());
            }
        }
    }

    /**
     * Adds an enemy to the spatial hash grid. If the enemy's position
     * lies outside the defined bounds of the grid, an {@code IllegalStateException}
     * will be thrown.
     *
     * @param enemy the {@code IEnemy} object to be added to the spatial hash grid
     * @throws IllegalStateException if the position of the enemy is outside the
     *         bounds of the spatial hash grid
     */
    public void add(final IEnemy enemy) {
        final Vector2D enemyLocation = enemy.getPosition();
        if (enemyLocation.getX() < xMin || enemyLocation.getX() > xMax
                || enemyLocation.getY() < yMin || enemyLocation.getY() > yMax) {
            throw new IllegalStateException("Enemy is outside the bounds of the spatial hash grid");
        }

        final CellIdx cellIdx = getCellIdx(enemyLocation);
        spatialHashGrid.get(cellIdx.x()).get(cellIdx.y()).add(enemy);
        enemyCell.put(enemy, cellIdx);
        size++;
    }

    /**
     * Removes the specified {@code IEnemy} from the spatial hash grid.
     * If the given enemy is not present within the grid, an {@code IllegalArgumentException}
     * is thrown.
     *
     * @param enemy the {@code IEnemy} object to be removed from the spatial hash grid
     * @throws IllegalArgumentException if the enemy is not found in the spatial hash grid
     */
    public void remove(final IEnemy enemy) {
        final CellIdx cellIdx = enemyCell.get(enemy);
        if (cellIdx == null) {
            throw new IllegalArgumentException("Enemy isn't present in the spatial hash grid");
        }

        spatialHashGrid.get(cellIdx.x()).get(cellIdx.y()).remove(enemy);
        enemyCell.remove(enemy);
        size--;
    }

    /**
     * Updates in which cell the specified {@code IEnemy} lies within the spatial hash grid based on his position.
     *
     * @param enemy the {@code IEnemy} object to update within the spatial hash grid
     * @throws IllegalArgumentException if the enemy is not currently present in the spatial hash grid
     */
    public void update(final IEnemy enemy) {
        final CellIdx prevCellIdx = enemyCell.get(enemy);
        if (prevCellIdx == null) {
            throw new IllegalArgumentException("Enemy isn't present in the spatial hash grid");
        }
        final CellIdx currCellIdx = getCellIdx(enemy.getPosition());

        // Skip if the enemy is still in the same cell
        if (prevCellIdx.equals(currCellIdx)) {
            return;
        }

        remove(enemy);
        add(enemy);
    }

    /**
     * Removes all the specified {@code IEnemy} instances from the spatial hash grid.
     *
     * @param enemies the collection of {@code IEnemy} to be removed from the spatial hash grid.
     * @see SpatialHashGrid#remove(IEnemy)
     */
    public void removeAll(final Collection<IEnemy> enemies) {
        for (final IEnemy enemy : enemies) {
            remove(enemy);
        }
    }

    /**
     * Updates all the specified {@code IEnemy} instances from the spatial hash grid.
     *
     * @param enemies the collection of {@code IEnemy} to be updated from the spatial hash grid.
     * @see SpatialHashGrid#update(IEnemy)
     */
    public void updateAll(final Collection<IEnemy> enemies) {
        for (final IEnemy enemy : enemies) {
            update(enemy);
        }
    }

    /**
     * Retruns an {@code Iterator<IEnemy>} over the enemies currently stored in the spatial hash grid.
     * @return an {@code Iterator<IEnemy>} over the enemies currently stored in the spatial hash grid.
     */
    @Override
    public Iterator<IEnemy> iterator() {
        return enemyCell.keySet().iterator();
    }

    /**
     * Returns the number of elements currently stored in the spatial hash grid.
     * @return the number of elements currently stored in the spatial hash grid.
     */
    public int size() {
        return size;
    }

    /**
     * Retrieves a list of enemies within a specified radius of a given location.
     *
     * @param location the {@code Vector2D} representing the central point of the search
     * @param radius the radius within which to search for enemies
     * @return a {@code List<IEnemy>} containing all enemies located within the specified radius
     */
    public List<IEnemy> getNear(final Vector2D location, final double radius) {
        final CellIdx min = getCellIdx(location.subtract(radius, radius));
        final CellIdx max = getCellIdx(location.add(radius, radius));

        final List<IEnemy> inside = new LinkedList<>();
        for (int x = min.x(); x <= max.x(); x++) {
            for (int y = min.y(); y <= max.y(); y++) {
                final CellIdx cellIdx = new CellIdx(x, y);

                if (!isInside(cellIdx)) {
                    continue;
                }
                for (final IEnemy enemy : spatialHashGrid.get(cellIdx.x()).get(cellIdx.y())) {
                    final Vector2D pos = enemy.getPosition();
                    final double dstX = pos.getX() - location.getX();
                    final double dstY = pos.getY() - location.getY();
                    final double sqDst = dstX * dstX + dstY * dstY;

                    if (sqDst <= radius * radius) {
                        inside.add(enemy);
                    }
                }
            }
        }

        return inside;
    }

    private boolean isInside(final CellIdx cellIdx) {
        return cellIdx.x() >= 0 && cellIdx.x() < cols
                && cellIdx.y() >= 0 && cellIdx.y() < rows;
    }

    private CellIdx getCellIdx(final Vector2D location) {
        final int x = (int) Math.floor((location.getX() - xMin) / CELL_SIZE);
        final int y = (int) Math.floor((location.getY() - yMin) / CELL_SIZE);

        return new CellIdx(x, y);
    }
}
