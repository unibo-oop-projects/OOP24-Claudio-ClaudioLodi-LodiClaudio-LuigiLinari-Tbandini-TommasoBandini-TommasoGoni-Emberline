package tiles.interfaces;

public interface WalkableTile extends Tile {
    
    default boolean isWalkable() {
        return true;
    }
}
