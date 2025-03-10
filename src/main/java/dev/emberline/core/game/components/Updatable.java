package dev.emberline.core.game.components;

@FunctionalInterface
public interface Updatable {
    
    void update(long elapsed);
}
