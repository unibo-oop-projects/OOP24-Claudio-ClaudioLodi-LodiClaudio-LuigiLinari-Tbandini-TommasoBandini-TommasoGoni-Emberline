package dev.emberline.game;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.event.EventDispatcher;
import dev.emberline.core.event.EventHandler;
import dev.emberline.core.sounds.AudioController;
import dev.emberline.game.serialization.Serializer;
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
import dev.emberline.gui.menu.SaveSelection;
import dev.emberline.gui.menu.SaveSelection.Saves;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.application.Platform;
import javafx.scene.input.InputEvent;

import java.util.EventListener;
import java.util.Objects;

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
    private World world;
    private final Serializer worldSerializer = new Serializer();
    private final AudioController audioController = new AudioController();
    private Saves activeSaveSlot;
    private final MainMenu mainMenu = new MainMenu();
    private final Options optionsFromGame = new Options(true);
    private final Options optionsFromMenu = new Options(false);
    private final SaveSelection saveSelection = new SaveSelection();
    private final GameOver gameOver = new GameOver();

    private GameState currentState;
    private GameState previousState;

    /**
     * Constructs a new instance of {@code GameRoot} and initializes the main menu
     * as the current game state.
     */
    public GameRoot() {
        registerEvents();
        audioController.startSoundtrack();

        currentState = mainMenu;
    }

    private void registerEvents() {
        EventDispatcher.getInstance().registerListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processInput(final InputEvent inputEvent) {
        currentState.processInput(inputEvent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final long elapsed) {
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
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleStartEvent(final SetStartEvent event) {
        currentState = saveSelection;
    }

    /**
     * Sets the current world and active save slot for the game.
     * This method is called when the game is initialized or when a new world is loaded.
     *
     * @param world the current game world
     * @param save  the active save slot
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "This is intended behavior as this class should store the world."
    )
    public void setWorld(final World world, final Saves save) {
        EventDispatcher.getInstance().registerListener(this);
        EventDispatcher.getInstance().registerListener(audioController);
        this.activeSaveSlot = save;
        this.currentState = world;
        this.world = world;
    }

    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleSetMainMenuEvent(final SetMainMenuEvent event) {
        // Save the world if exiting from the game by the options in game
        if (Objects.equals(currentState, optionsFromGame)) { 
            worldSerializer.serialize(world, activeSaveSlot.getDisplayName());
        }
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
