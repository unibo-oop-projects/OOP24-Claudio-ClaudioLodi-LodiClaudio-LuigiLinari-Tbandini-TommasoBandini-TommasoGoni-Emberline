package dev.emberline.game.world.buildings;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.Building;
import dev.emberline.game.world.World;
import dev.emberline.game.world.buildings.towerPreBuild.TowerPreBuild;
import dev.emberline.game.world.entities.enemies.IEnemiesManager;
import dev.emberline.game.world.buildings.tower.Tower;
import dev.emberline.gui.towerdialog.TowerDialogLayer;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

import java.util.*;

public class TowersManager implements Updatable, Renderable, Inputable {

    private TowerDialogLayer towerDialogLayer;

    private Set<Building> buildings = new HashSet<>();
    private final Collection<TowerPreBuild> toBuild = new LinkedList<>();

    private final World world;
    private final IEnemiesManager enemiesManager;

    public TowersManager(World world) {
        this.world = world;
        this.enemiesManager = world.getEnemiesManager();

       buildings.add(new TowerPreBuild(new Coordinate2D(10,10), this));
       buildings.add(new TowerPreBuild(new Coordinate2D(5,5), this));
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

    public void buildTower(TowerPreBuild preBuild) {
        if (!buildings.contains(preBuild)) {
            throw new IllegalArgumentException("preBuild must be already added to the buildings set");
        }
        toBuild.add(preBuild);
    }

    /* TODO */
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

        for (final TowerPreBuild preBuild : toBuild) {
            buildings.remove(preBuild);
            Vector2D towerLocationBottomLeft = new Coordinate2D(
                    preBuild.getWorldTopLeft().getX(), preBuild.getWorldBottomRight().getY()
            );
            buildings.add(new Tower(towerLocationBottomLeft, world));
        }
        toBuild.clear();
    }
}
