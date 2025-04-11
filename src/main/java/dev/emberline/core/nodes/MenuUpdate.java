package dev.emberline.core.nodes;

import dev.emberline.core.game.components.Updatable;

import java.util.concurrent.atomic.AtomicLong;

public class MenuUpdate implements Updatable {
    private final AtomicLong _PREV = new AtomicLong(0);

    public MenuUpdate() {
        _PREV.set(System.nanoTime());
    }

    @Override
    public void update(long elapsed) {
        System.out.println("Update Menu...");
        System.out.println("Ran for " + elapsed + "ns");
    }
}