package dev.emberline.game.world.entities.enemies;

import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Vector2D;

import java.util.*;

/**
 * The SpatialHashGrid class is a data structure designed for efficient
 * spatial partitioning and querying of {@code IEnemy} objects in a two-dimensional space.
 * It divides the space into a grid of cells and assigns objects to cells based on
 * their positions.
 */
public class SpatialHashGrid implements Iterable<IEnemy> {

    private static final int CELL_SIZE = 1;

    private final int x_min, y_min;
    private final int x_max, y_max;

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
     * @param x_min the minimum x-coordinate of the grid's boundary
     * @param y_min the minimum y-coordinate of the grid's boundary
     * @param x_max the maximum x-coordinate of the grid's boundary
     * @param y_max the maximum y-coordinate of the grid's boundary
     */
    public SpatialHashGrid(final int x_min, final int y_min, final int x_max, final int y_max) {
        this.cols = (int) Math.ceil((double) (x_max - x_min) / CELL_SIZE) + 1;
        this.rows = (int) Math.ceil((double) (y_max - y_min) / CELL_SIZE) + 1;

        this.x_min = x_min;
        this.y_min = y_min;
        this.x_max = x_max;
        this.y_max = y_max;

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
        if (enemyLocation.getX() < x_min || enemyLocation.getX() > x_max ||
                enemyLocation.getY() < y_min || enemyLocation.getY() > y_max) {
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
     * @see SpatialHashGrid#update(IEnemy)
     */
    public void updateAll(final Collection<IEnemy> enemies) {
        for (final IEnemy enemy : enemies) {
            update(enemy);
        }
    }

    /**
     * @return an {@code Iterator<IEnemy>} over the enemies currently stored in the spatial hash grid
     */
    @Override
    public Iterator<IEnemy> iterator() {
        return enemyCell.keySet().iterator();
    }

    /**
     * @return the number of elements currently stored in the spatial hash grid
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
        return cellIdx.x() >= 0 && cellIdx.x() < cols &&
                cellIdx.y() >= 0 && cellIdx.y() < rows;
    }

    private CellIdx getCellIdx(final Vector2D location) {
        final int x = (int) Math.floor((location.getX() - x_min) / CELL_SIZE);
        final int y = (int) Math.floor((location.getY() - y_min) / CELL_SIZE);

        return new CellIdx(x, y);
    }
}