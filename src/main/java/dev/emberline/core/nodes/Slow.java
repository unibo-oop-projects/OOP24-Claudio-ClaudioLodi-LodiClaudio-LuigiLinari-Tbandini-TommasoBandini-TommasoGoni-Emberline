package dev.emberline.core.nodes;

import dev.emberline.core.game.components.Updatable;
import dev.emberline.core.game.components.Renderable;

public class Slow implements Updatable, Renderable {

    public Slow() {
        super();
    }

    @Override
    public void update(long elapsed) {
        System.out.println("Update Running Slow...");
        System.out.println("Ran for " + elapsed + "ns");
    }

    @Override
    public void render() {
        System.out.println("Rendered Running Slow");
    }
}
