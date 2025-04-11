package dev.emberline.core.nodes;

import dev.emberline.core.game.components.Updatable;
import dev.emberline.core.game.components.Inputable;
import dev.emberline.core.game.components.Renderable;

public class GameSaves {
    public final GameSavesInput inputer;
    public final GameSavesUpdate updater;
    public final GameSavesRender renderer;

    public GameSaves() {
        super();
        this.inputer = new GameSavesInput();
        this.updater = new GameSavesUpdate();
        this.renderer = new GameSavesRender();
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

