package dev.emberline.core.nodes;

import dev.emberline.core.game.components.Updatable;
import dev.emberline.core.game.components.Inputable;
import dev.emberline.core.game.components.Renderable;

public class Options {
    public final OptionsInput inputer;
    public final OptionsUpdate updater;
    public final OptionsRender renderer;

    public Options () {
        this.inputer = new OptionsInput();
        this.updater = new OptionsUpdate();
        this.renderer = new OptionsRender();
    }

    public Inputable asInputable() {
        return inputer;
    }

    public Updatable asUpdatable() {
        return updater;
    }

    public Renderable asRenderable() {
        return renderer;
    }
}

