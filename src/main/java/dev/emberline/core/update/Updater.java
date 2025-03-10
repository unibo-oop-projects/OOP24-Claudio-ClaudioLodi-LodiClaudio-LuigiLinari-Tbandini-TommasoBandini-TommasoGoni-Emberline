package dev.emberline.core.update;

import dev.emberline.core.game.components.Updatable;

public class Updater {

    private final Updatable root;

    public Updater(Updatable root) {
        this.root = root;
    }

    public void update(long elapsed) {
        root.update(elapsed);
    }
}
