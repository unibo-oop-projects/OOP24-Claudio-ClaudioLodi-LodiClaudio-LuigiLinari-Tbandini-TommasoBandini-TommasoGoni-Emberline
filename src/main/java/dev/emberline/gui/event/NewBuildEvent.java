package dev.emberline.gui.event;

import dev.emberline.game.world.buildings.towerprebuild.TowerPreBuild;

public class NewBuildEvent extends GuiEvent {

    private final TowerPreBuild tower;

    public NewBuildEvent(final Object source, final TowerPreBuild tower) {
        super(source);
        this.tower = tower;
    }

    public TowerPreBuild getTowerPreBuild() {
        return tower;
    }
}
