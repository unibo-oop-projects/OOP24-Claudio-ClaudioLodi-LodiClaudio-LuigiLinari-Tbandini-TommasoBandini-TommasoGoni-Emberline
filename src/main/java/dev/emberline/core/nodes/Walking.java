package dev.emberline.core.nodes;

import dev.emberline.core.game.components.Updatable;
import dev.emberline.core.game.components.Renderable;

public class Walking implements Updatable, Renderable {

    public Walking() {
        super();
    }

    @Override
    public void update(long elapsed) {
        System.out.println("Update Walking...");
        System.out.println("Walked for " + elapsed + "ns");
    }

    @Override
    public void render() {
        System.out.println("Rendered Walking");
    }
}
