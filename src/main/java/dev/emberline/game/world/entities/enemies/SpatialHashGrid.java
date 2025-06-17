package dev.emberline.game.world.entities.enemies;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Vector2D;

public class SpatialHashGrid implements Iterable<IEnemy> {

    private record CellIdx(int x, int y) {}

    private static final int CELL_SIZE = 1;

    private final int x_min, y_min;
    private final int x_max, y_max;

    private final Set<IEnemy>[][] spatialHashGrid;
    private final Map<IEnemy, CellIdx> enemyCell;

    @SuppressWarnings("unchecked")
    public SpatialHashGrid(int x_min, int y_min, int x_max, int y_max) {
        int cols = (int) Math.ceil((double)(x_max - x_min) / CELL_SIZE);
        int rows = (int) Math.ceil((double)(y_max - y_min) / CELL_SIZE);

        this.x_min = x_min;
        this.y_min = y_min;
        this.x_max = x_max + CELL_SIZE * cols;
        this.y_max = y_max + CELL_SIZE * rows;

        this.spatialHashGrid = (HashSet<IEnemy>[][])new HashSet[cols][rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                spatialHashGrid[i][j] = new HashSet<>();
            }
        }

        this.enemyCell = new HashMap<>();
    }

    public void add(IEnemy enemy) {
        CellIdx cellIdx = getCellIdx(enemy.getPosition());
        if (cellIdx == null) {
            throw new IllegalArgumentException("Enemy already added to the spatial hash grid");
        }

        spatialHashGrid[cellIdx.x()][cellIdx.y()].add(enemy);
        enemyCell.put(enemy, cellIdx);
    }
    
    public void remove(IEnemy enemy) {
        CellIdx cellIdx = enemyCell.get(enemy);
        if (cellIdx == null) {
            throw new IllegalArgumentException("Enemy isn't present in the spatial hash grid");
        }

        spatialHashGrid[cellIdx.x()][cellIdx.y()].remove(enemy);
        enemyCell.remove(enemy);
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
    
    public List<IEnemy> getNear(Vector2D location, double radius) {
        CellIdx min = getCellIdx(location.subtract(radius, radius));
        CellIdx max = getCellIdx(location.add(radius, radius));

        List<IEnemy> inside = new LinkedList<>();
        for (int i = min.x(); i <= max.x(); i++) {
            for (int j = min.y(); j <= max.y(); j++) {
                for (final IEnemy enemy : spatialHashGrid[i][j]) {
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
    
    private CellIdx getCellIdx(Vector2D location) {
        if (location.getX() < x_min || location.getY() < y_min ||
            location.getX() > x_max || location.getY() > y_max) {
            throw new IllegalArgumentException("Location out of bounds of the spatial hash grid");
        }

        int x = (int) Math.floor((double)(location.getX() - x_min) / CELL_SIZE);
        int y = (int) Math.floor((double)(location.getY() - y_min) / CELL_SIZE);

        return new CellIdx(x, y);
    }
}