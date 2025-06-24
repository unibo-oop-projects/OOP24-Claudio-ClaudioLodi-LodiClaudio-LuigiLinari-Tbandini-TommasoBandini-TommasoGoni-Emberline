package dev.emberline.game;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;
import dev.emberline.gui.event.*;
import dev.emberline.gui.menu.GameOver;
import dev.emberline.gui.menu.MainMenu;
import dev.emberline.gui.menu.Options;
import javafx.application.Platform;
import javafx.scene.input.InputEvent;

public class GameRoot implements Inputable, Updatable, Renderable, GuiEventListener, GameEventListener {
    // Navigation States
    private final World world = new World();
    private final MainMenu mainMenu = new MainMenu();
    private final Options options = new Options();
    private final GameOver gameOver = new GameOver();

    private GameState currentState;
    private GameState previousState;

    public GameRoot() {
        currentState = mainMenu;
        mainMenu.setListener(this);
        options.setListener(this);
        world.getPlayer().setListener(this);
        world.setListener(this);
    }

    @Override
    public void processInput(final InputEvent inputEvent) {
        currentState.processInput(inputEvent);
    }

    @Override
    public void update(final long elapsed) {
        currentState.update(elapsed);
    }

    @Override
    public void render() {
        currentState.render();
    }

    @Override
    public void onGuiEvent(final GuiEvent event) {
        if (event instanceof final SetStartEvent startEvent) {
            handleStartEvent(startEvent);
        }
        else if (event instanceof final SetMainMenuEvent menuEvent) {
            handleSetMainMenuEvent(menuEvent);
        }
        else if (event instanceof final OpenOptionsEvent openOptionsEvent) {
            handleOpenOptionsEvent(openOptionsEvent);
        }
        else if (event instanceof final CloseOptionsEvent closeOptionsEvent) {
            handleCloseOptionsEvent(closeOptionsEvent);
        }
        else if (event instanceof final ExitGameEvent exitGameEvent) {
            handleExitGameEvent(exitGameEvent);
        }
    }

    @Override
    public void onGameEvent(final GameEvent event) {
        if (event instanceof final GameOverEvent gameOverEvent) {
            handleGameOverEvent(gameOverEvent);
        }
    }

    private void handleStartEvent(final SetStartEvent event) {
        currentState = world;
    }

    private void handleSetMainMenuEvent(final SetMainMenuEvent event) {
        currentState = mainMenu;
    }

    private void handleOpenOptionsEvent(final OpenOptionsEvent event) {
        previousState = currentState;
        currentState = options;
    }

    private void handleCloseOptionsEvent(final CloseOptionsEvent event) {
        currentState = previousState;
    }

    private void handleGameOverEvent(final GameOverEvent event) {
        currentState = gameOver;
    }

    private void handleExitGameEvent(final ExitGameEvent event) {
        Platform.exit();
    }
}
