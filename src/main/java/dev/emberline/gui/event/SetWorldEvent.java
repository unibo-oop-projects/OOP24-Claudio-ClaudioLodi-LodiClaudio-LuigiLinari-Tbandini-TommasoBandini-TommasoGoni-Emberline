package dev.emberline.gui.event;

import dev.emberline.game.world.World;
import dev.emberline.gui.saveselection.SaveSelection.Saves;

/**
 * Represents an event that signals the transition to the effective game.
 */
public class SetWorldEvent extends GuiEvent {
    private final World world;
    private final Saves save;
    
    /**
     * Constructs a new SetWorldEvent with the specified source and world.
     *
     * @param source the source of the event, typically the component that triggered it
     * @param world  the World instance to be set
     */
    public SetWorldEvent(final Object source, World world, Saves save) {
        super(source);
        this.world = world;
        this.save = save;
    }

    /**
     * Returns the World instance associated with this event.
     *
     * @return the World instance
     */
    public World getWorld() {
        return world;
    }

    /**
     * Returns the save  associated with this event.
     *
     * @return the save 
     */
    public Saves getSave() {
        return save;
    }
}
