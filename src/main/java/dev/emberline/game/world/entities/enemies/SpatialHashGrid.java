package dev.emberline.game.world.entities.enemies;

import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Vector2D;

import java.util.*;

public class SpatialHashGrid implements Iterable<IEnemy> {

    private record CellIdx(int x, int y) {}

    private static final int CELL_SIZE = 1;

    private final int x_min, y_min;
    private final int x_max, y_max;

    private final int cols;
    private final int rows;

    private final List<List<Set<IEnemy>>> spatialHashGrid;
    private final Map<IEnemy, CellIdx> enemyCell = new HashMap<>();

    private int size = 0;

    public SpatialHashGrid(int x_min, int y_min, int x_max, int y_max) {
        this.cols = (int) Math.ceil((double)(x_max - x_min) / CELL_SIZE) + 1;
        this.rows = (int) Math.ceil((double)(y_max - y_min) / CELL_SIZE) + 1;

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

    public void add(IEnemy enemy) {
        Vector2D enemyLocation = enemy.getPosition();
        if (    enemyLocation.getX() < x_min || enemyLocation.getX() > x_max ||
                enemyLocation.getY() < y_min || enemyLocation.getY() > y_max) {
            throw new IllegalStateException("Enemy is outside the bounds of the spatial hash grid");
        }

        CellIdx cellIdx = getCellIdx(enemyLocation);
        spatialHashGrid.get(cellIdx.x()).get(cellIdx.y()).add(enemy);
        enemyCell.put(enemy, cellIdx);
        size++;
    }
    
    public void remove(IEnemy enemy) {
        CellIdx cellIdx = enemyCell.get(enemy);
        if (cellIdx == null) {
            throw new IllegalArgumentException("Enemy isn't present in the spatial hash grid");
        }

        spatialHashGrid.get(cellIdx.x()).get(cellIdx.y()).remove(enemy);
        enemyCell.remove(enemy);
        size--;
    }

    public void removeAll(Collection<IEnemy> enemies) {
        for (final IEnemy enemy : enemies) {
            remove(enemy);
        }
    }
    
    public void update(IEnemy enemy) {
        CellIdx prevCellIdx = enemyCell.get(enemy);
        if (prevCellIdx == null) {
            throw new IllegalArgumentException("Enemy isn't present in the spatial hash grid");
        }
        CellIdx currCellIdx = getCellIdx(enemy.getPosition());

        // Skip if the enemy is still in the same cell
        if (prevCellIdx.equals(currCellIdx)) {
            return;
        }

        remove(enemy);
        add(enemy);
    }

    public void updateAll(Collection<IEnemy> enemies) {
        for (final IEnemy enemy : enemies) {
            update(enemy);
        }
    }

    /**
     * @return An iterator of the elements present in the {@code SpatialHashGrid}
     */
    public Iterator<IEnemy> iterator() {
        return enemyCell.keySet().iterator();
    }

    public int size() {
        return size;
    }

    public List<IEnemy> getNear(Vector2D location, double radius) {
        CellIdx min = getCellIdx(location.subtract(radius, radius));
        CellIdx max = getCellIdx(location.add(radius, radius));

        List<IEnemy> inside = new LinkedList<>();
        for (int x = min.x(); x <= max.x(); x++) {
            for (int y = min.y(); y <= max.y(); y++) {
                CellIdx cellIdx = new CellIdx(x, y);

                if (!isInside(cellIdx)) continue;
                for (final IEnemy enemy : spatialHashGrid.get(cellIdx.x()).get(cellIdx.y())) {
                    Vector2D pos = enemy.getPosition();
                    double dstX = pos.getX() - location.getX();
                    double dstY = pos.getY() - location.getY();
                    double sqDst = dstX * dstX + dstY * dstY;

                    if (sqDst <= radius * radius) {
                        inside.add(enemy);
                    }
                }
            }
        }

        return inside;
    }

    private boolean isInside(CellIdx cellIdx) {
        return  (cellIdx.x() >= 0 && cellIdx.x() < cols &&
                cellIdx.y() >= 0 && cellIdx.y() < rows);
    }

    private CellIdx getCellIdx(Vector2D location) {
        int x = (int) Math.floor((location.getX() - x_min) / CELL_SIZE);
        int y = (int) Math.floor((location.getY() - y_min) / CELL_SIZE);

        return new CellIdx(x, y);
    }
}