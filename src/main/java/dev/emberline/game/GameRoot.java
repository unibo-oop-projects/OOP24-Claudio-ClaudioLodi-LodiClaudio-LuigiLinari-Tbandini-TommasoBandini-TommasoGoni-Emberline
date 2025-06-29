package dev.emberline.game;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.event.EventDispatcher;
import dev.emberline.core.event.EventHandler;
import dev.emberline.game.world.World;
import dev.emberline.gui.event.CloseOptionsEvent;
import dev.emberline.gui.event.ExitGameEvent;
import dev.emberline.gui.event.GameOverEvent;
import dev.emberline.gui.event.OpenOptionsEvent;
import dev.emberline.gui.event.SetMainMenuEvent;
import dev.emberline.gui.event.SetStartEvent;
import dev.emberline.gui.menu.GameOver;
import dev.emberline.gui.menu.MainMenu;
import dev.emberline.gui.menu.Options;
import javafx.application.Platform;
import javafx.scene.input.InputEvent;

import java.util.EventListener;

/**
 * The GameRoot class serves as the central node for managing the game states and
 * their transitions. It implements the Inputable, Updatable, Renderable, and
 * EventListener interfaces to handle input events, game updates, rendering processes,
 * and event dispatching.
 * <p>
 * This class acts as the entry point that connects the main states of the game,
 * including the main menu, world (gameplay), option's menu, and game over screen.
 * The GameRoot listens for specific events to transition between these states.
 */
public class GameRoot implements Inputable, Updatable, Renderable, EventListener {
    // Navigation States
    private World world = new World();
    private final MainMenu mainMenu = new MainMenu();
    private final Options optionsFromGame = new Options(true);
    private final Options optionsFromMenu = new Options(false);
    private final GameOver gameOver = new GameOver();

    private GameState currentState;
    private GameState previousState;

    private long acc = 0;
    private boolean isSerialized;
    private final Serializer worldSerializer = new Serializer();
    /**
     * Constructs a new instance of {@code GameRoot} and initializes the main menu
     * as the current game state.
     */
    public GameRoot() {
        currentState = mainMenu;
        EventDispatcher.getInstance().registerListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processInput(final InputEvent inputEvent) {
        if (!isSerialized)
            currentState.processInput(inputEvent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final long elapsed) {
        long nsToSeconds = 1_000_000_000;
        acc += elapsed;
        long tmp = acc / nsToSeconds;
        if ((tmp >= 15 && tmp <= 30 )) {
            if (tmp >= 16 && isSerialized) {
                world = worldSerializer.getDeserializedWorld();
                isSerialized = false;
            } else if (tmp < 16 && !isSerialized) {
                isSerialized = true;
                worldSerializer.serialize(world);
            }
        } else {
            currentState.update(elapsed);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        if (!isSerialized)
            currentState.render();
    }

    // Event Handlers
    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleStartEvent(final SetStartEvent event) {
        currentState = world;
    }

    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleSetMainMenuEvent(final SetMainMenuEvent event) {
        currentState = mainMenu;
    }

    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleOpenOptionsEvent(final OpenOptionsEvent event) {
        previousState = currentState;

        if (previousState.equals(mainMenu)) {
            currentState = optionsFromMenu;
        } else {
            currentState = optionsFromGame;
        }
    }

    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleCloseOptionsEvent(final CloseOptionsEvent event) {
        currentState = previousState;
    }

    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleGameOverEvent(final GameOverEvent event) {
        currentState = gameOver;
        gameOver.setStatistics(event.getStatistics());
    }

    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleExitGameEvent(final ExitGameEvent event) {
        Platform.exit();
    }
}
