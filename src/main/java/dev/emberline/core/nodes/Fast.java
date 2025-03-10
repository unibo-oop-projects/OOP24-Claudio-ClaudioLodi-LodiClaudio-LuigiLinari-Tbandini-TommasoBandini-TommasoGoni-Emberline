package dev.emberline.core.nodes;

import dev.emberline.core.game.components.Updatable;
import dev.emberline.core.game.components.Renderable;

public class Fast implements Updatable, Renderable{
    
    public Fast() {
        super();
    }

    @Override
    public void update(long elapsed) {
        System.out.println("Update Running Fast...");
        System.out.println("Ran for " + elapsed + "ns");
    }

    @Override
    public void render() {
        System.out.println("Rendered Running Fast");
    }
}
