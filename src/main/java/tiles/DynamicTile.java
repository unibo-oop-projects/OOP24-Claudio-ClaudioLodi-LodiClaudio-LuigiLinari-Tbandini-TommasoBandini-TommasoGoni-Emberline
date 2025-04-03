package tiles;

import tiles.interfaces.Tile;

public class DynamicTile implements Tile {
    
    private boolean walkable = false;

    void setWalkable(boolean value) {
        this.walkable = value;
    }

    public boolean isWalkable() {
        return walkable;
    }
}
