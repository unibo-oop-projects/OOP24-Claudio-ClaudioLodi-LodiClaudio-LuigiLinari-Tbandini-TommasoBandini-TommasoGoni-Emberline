package dev.emberline.gui.event;

import dev.emberline.game.world.buildings.towerprebuild.TowerPreBuild;

import java.io.Serial;

/**
 * Represents an event triggered to initiate the construction of a new tower.
 * <p>
 * This event is specifically tied to the pre-build tower, represented
 * by a {@link TowerPreBuild} entity. It is typically dispatched within the GUI
 * framework when a user interacts with the interface to construct a new tower.
 */
public class NewBuildEvent extends GuiEvent {

    @Serial
    private static final long serialVersionUID = -1536112446735959877L;
    private final TowerPreBuild tower;

    /**
     * Constructs a new {@code NewBuildEvent}.
     *
     * @param source the object on which the event initially occurred
     * @param tower  the pre-build tower associated with this event
     * @see NewBuildEvent
     */
    public NewBuildEvent(final Object source, final TowerPreBuild tower) {
        super(source);
        this.tower = tower;
    }

    /**
     * Returns the pre-build tower associated with this event.
     *
     * @return the pre-build tower associated with this event.
     */
    public TowerPreBuild getTowerPreBuild() {
        return tower;
    }
}
