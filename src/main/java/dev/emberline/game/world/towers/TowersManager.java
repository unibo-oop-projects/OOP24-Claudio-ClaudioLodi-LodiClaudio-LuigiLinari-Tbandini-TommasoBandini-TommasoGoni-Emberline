package dev.emberline.game.world.towers;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.Building;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.IEnemiesManager;
import dev.emberline.game.world.towers.tower.Tower;
import dev.emberline.gui.towerdialog.TowerDialogLayer;
import dev.emberline.utility.Coordinate2D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

import java.util.Collection;
import java.util.HashSet;

public class TowersManager implements Updatable, Renderable, Inputable {

    private TowerDialogLayer towerDialogLayer;

    private Collection<Building> buildings = new HashSet<>();

    private final World world;
    private final IEnemiesManager enemiesManager;

    public TowersManager(World world) {
        this.world = world;
        this.enemiesManager = world.getEnemiesManager();

       buildings.add(new Tower(this, new Coordinate2D(10,10)));
       buildings.add(new Tower(this, new Coordinate2D(5,5)));
    }

    public void openTowerDialog(Tower tower) {
        if (towerDialogLayer == null || towerDialogLayer.getTower() != tower) {
            towerDialogLayer = new TowerDialogLayer(tower);
            towerDialogLayer.setListener(tower);
        }
    }

    public void closeTowerDialog() {
        towerDialogLayer = null;
    }

    @Override
    public void processInput(InputEvent inputEvent) {
        if (towerDialogLayer != null) {
            towerDialogLayer.processInput(inputEvent);
        }

        for (final Building building : buildings) {
            if (inputEvent.isConsumed()) {
                return;
            }
            building.processInput(inputEvent);
        }

        // Clicking elsewhere closes the active tower dialog
        if (!inputEvent.isConsumed() && inputEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            closeTowerDialog();
        }
    }

    @Override
    public void render() {
        if (towerDialogLayer != null) {
            towerDialogLayer.render();
        }

        for (final Building building : buildings) {
            building.render();
        }
    }

    @Override
    public void update(long elapsed) {
        for (final Building building : buildings) {
            building.update(elapsed);
        }
    }
}
