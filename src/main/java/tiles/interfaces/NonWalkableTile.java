package tiles.interfaces;

public interface NonWalkableTile extends Tile {
    
    default boolean isWalkable() {
        return false;
    }
}
