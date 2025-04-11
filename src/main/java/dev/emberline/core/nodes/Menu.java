package dev.emberline.core.nodes;

import dev.emberline.core.game.components.Updatable;
import dev.emberline.core.game.components.Inputable;
import dev.emberline.core.game.components.Renderable;

public class Menu {
    public final MenuInput inputer;
    public final MenuUpdate updater;
    public final MenuRender renderer;

    public Menu() {
        super();
        this.inputer = new MenuInput();
        this.updater = new MenuUpdate();
        this.renderer = new MenuRender();
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

