package dev.emberline.core.update;

import dev.emberline.core.components.Updatable;

public class Updater {

    private final Updatable root;

    public Updater(final Updatable root) {
        this.root = root;
    }

    public void update(final long elapsed) {
        root.update(elapsed);
    }
}
