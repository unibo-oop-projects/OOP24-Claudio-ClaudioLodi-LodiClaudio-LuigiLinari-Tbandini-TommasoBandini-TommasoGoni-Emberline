package dev.emberline.game;

import java.io.*;
import java.util.EventListener;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.event.EventDispatcher;
import dev.emberline.core.event.EventHandler;
import dev.emberline.game.world.World;
import dev.emberline.gui.event.CloseOptionsEvent;
import dev.emberline.gui.event.OpenOptionsEvent;
import dev.emberline.gui.event.SetMainMenuEvent;
import dev.emberline.gui.event.SetStartEvent;
import dev.emberline.gui.event.GameOverEvent;
import dev.emberline.gui.event.ExitGameEvent;
import dev.emberline.gui.menu.GameOver;
import dev.emberline.gui.menu.MainMenu;
import dev.emberline.gui.menu.Options;
import javafx.application.Platform;
import javafx.scene.input.InputEvent;

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

    private String _fileName = "pluto";
    private long acc = 0;
    private boolean isSerialized;
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
        currentState.processInput(inputEvent);
    }

    private void serialize() {
        try {
            final OutputStream file = new FileOutputStream(_fileName);
            final OutputStream bstream = new BufferedOutputStream(file);
            final ObjectOutputStream ostream = new ObjectOutputStream(bstream);

            ostream.writeObject(world);
        } catch (IOException ex) {
            System.out.println("serialization issue from: " + ex.getMessage());
        }
    }

    private void deserialize() {
        try {
            final InputStream file = new FileInputStream(_fileName);
            final InputStream bstream = new BufferedInputStream(file);
            final ObjectInputStream ostream = new ObjectInputStream(bstream);

            world = (World) ostream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("deserializaion issue from: " + ex.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final long elapsed) {
        long nsToSeconds = 1_000_000_000;
        acc += elapsed;
        long tmp = acc / nsToSeconds;
        if (currentState == world && (tmp >= 10 && tmp <= 20 )) {
            if (tmp >= 19 && isSerialized) {
                //deserialize();
            } else if (!isSerialized) {
                serialize();
                isSerialized = true;
            }
        } else
            currentState.update(elapsed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        currentState.render();
    }

    // Event Handlers
    @EventHandler
    private void handleStartEvent(final SetStartEvent event) {
        currentState = world;
    }

    @EventHandler
    private void handleSetMainMenuEvent(final SetMainMenuEvent event) {
        currentState = mainMenu;
    }

    @EventHandler
    private void handleOpenOptionsEvent(final OpenOptionsEvent event) {
        previousState = currentState;

        if (previousState == mainMenu) {
            currentState = optionsFromMenu;
        } else {
            currentState = optionsFromGame;
        }
    }

    @EventHandler
    private void handleCloseOptionsEvent(final CloseOptionsEvent event) {
        currentState = previousState;
    }

    @EventHandler
    private void handleGameOverEvent(final GameOverEvent event) {
        currentState = gameOver;
        gameOver.setStatistics(event.getStatistics());
    }

    @EventHandler
    private void handleExitGameEvent(final ExitGameEvent event) {
        Platform.exit();
    }
}
