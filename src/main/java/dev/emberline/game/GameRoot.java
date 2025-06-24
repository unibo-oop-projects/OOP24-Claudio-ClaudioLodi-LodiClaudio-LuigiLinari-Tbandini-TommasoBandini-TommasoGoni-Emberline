package dev.emberline.game;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;
import dev.emberline.gui.event.CloseOptionsEvent;
import dev.emberline.gui.event.ExitGameEvent;
import dev.emberline.gui.event.GameEvent;
import dev.emberline.gui.event.GameEventListener;
import dev.emberline.gui.event.GameOverEvent;
import dev.emberline.gui.event.GuiEvent;
import dev.emberline.gui.event.GuiEventListener;
import dev.emberline.gui.event.OpenOptionsEvent;
import dev.emberline.gui.event.SetMainMenuEvent;
import dev.emberline.gui.event.SetStartEvent;
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
    public void processInput(InputEvent inputEvent) {
        currentState.processInput(inputEvent);
    }

    @Override
    public void update(long elapsed) {
        currentState.update(elapsed);
    }

    @Override
    public void render() {
        currentState.render();
    }

    @Override
    public void onGuiEvent(GuiEvent event) {
        if (event instanceof SetStartEvent startEvent) {
            handleStartEvent(startEvent);
        } else if (event instanceof SetMainMenuEvent menuEvent) {
            handleSetMainMenuEvent(menuEvent);
        } else if (event instanceof OpenOptionsEvent openOptionsEvent) {
            handleOpenOptionsEvent(openOptionsEvent);
        } else if (event instanceof CloseOptionsEvent closeOptionsEvent) {
            handleCloseOptionsEvent(closeOptionsEvent);
        } else if (event instanceof ExitGameEvent exitGameEvent) {
            handleExitGameEvent(exitGameEvent);
        }
    }

    @Override
    public void onGameEvent(GameEvent event) {
        if (event instanceof GameOverEvent gameOverEvent) {
            handleGameOverEvent(gameOverEvent);
        } 
    }

    private void handleStartEvent(SetStartEvent event) {
       currentState = world;
    }

    private void handleSetMainMenuEvent(SetMainMenuEvent event) {
        currentState = mainMenu;
    }

    private void handleOpenOptionsEvent(OpenOptionsEvent event) {
        previousState = currentState;
        currentState = options;
    }

    private void handleCloseOptionsEvent(CloseOptionsEvent event) {
        currentState = previousState;
    }

    private void handleGameOverEvent(GameOverEvent event) {
        currentState = gameOver;
    }

    private void handleExitGameEvent(ExitGameEvent event) {
        Platform.exit();
    }
}
