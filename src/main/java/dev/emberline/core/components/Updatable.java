package dev.emberline.core.components;

@FunctionalInterface
public interface Updatable {
    
    void update(long elapsed);
}
