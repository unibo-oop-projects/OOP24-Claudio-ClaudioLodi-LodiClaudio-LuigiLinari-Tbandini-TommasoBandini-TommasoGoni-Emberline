package dev.emberline.core.game;

import java.util.HashSet;
import java.util.Set;

import dev.emberline.core.game.components.Inputable;
import dev.emberline.core.game.components.Updatable;
import dev.emberline.core.game.components.Renderable;

import javafx.scene.input.InputEvent;

public abstract class GeneralParent implements Inputable, Updatable, Renderable {
    
    // The children the parent needs to wake (may be different for each phase of the game loop)
    private final Set<Inputable> activeInputables;
    private final Set<Updatable> activeUpdatables;
    private final Set<Renderable> activeRenderables;

    public GeneralParent() {
        activeInputables = new HashSet<>();
        activeUpdatables = new HashSet<>();
        activeRenderables = new HashSet<>();
    }

    public GeneralParent(Set<Inputable> activeInputables, Set<Updatable> activeUpdatables,
            Set<Renderable> activeRenderables) {
        this.activeInputables = activeInputables;
        this.activeUpdatables = activeUpdatables;
        this.activeRenderables = activeRenderables;
    }

    public void processInput(InputEvent inputEvent) {
        for (final Inputable active : activeInputables) {
            active.processInput(inputEvent);
        }
    }

    public void update(long elapsed) {
        for (final Updatable active : activeUpdatables) {
            active.update(elapsed);
        }
    }

    public void render() {
        for (final Renderable active : activeRenderables) {
            active.render();
        }
    }

    public void addActiveInputable(Inputable inputable) {
        activeInputables.add(inputable);
    }

    public void addActiveUpdatable(Updatable updatable) {
        activeUpdatables.add(updatable);
    }

    public void addActiveRenderable(Renderable renderable) {
        activeRenderables.add(renderable);
    }

    public void removeActiveInputable(Inputable inputable) {
        activeInputables.remove(inputable);
    }

    public void removeActiveUpdatable(Updatable updatable) {
        activeUpdatables.remove(updatable);
    }

    public void removeActiveRenderable(Renderable renderable) {
        activeRenderables.remove(renderable);
    }
}
