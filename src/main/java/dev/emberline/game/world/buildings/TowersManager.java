package dev.emberline.game.world.buildings;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.Building;
import dev.emberline.game.world.World;
import dev.emberline.game.world.buildings.tower.Tower;
import dev.emberline.game.world.buildings.towerPreBuild.TowerPreBuild;
import dev.emberline.game.world.entities.enemies.IEnemiesManager;
import dev.emberline.gui.towerdialog.NewBuildDialogLayer;
import dev.emberline.gui.towerdialog.TowerDialogLayer;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class TowersManager implements Updatable, Renderable, Inputable {

    private TowerDialogLayer towerDialogLayer;
    private NewBuildDialogLayer newBuildDialogLayer;

    private final Set<Building> buildings = new HashSet<>();
    private final Collection<TowerPreBuild> toBuild = new LinkedList<>();

    private final World world;
    private final IEnemiesManager enemiesManager;

    public TowersManager(final World world) {
        this.world = world;
        this.enemiesManager = world.getEnemiesManager();

        // TODO
        buildings.add(new TowerPreBuild(new Coordinate2D(10, 10), this));
        buildings.add(new TowerPreBuild(new Coordinate2D(5, 5), this));
        buildings.add(new TowerPreBuild(new Coordinate2D(18, 10), this));
        buildings.add(new TowerPreBuild(new Coordinate2D(20, 7), this));
        buildings.add(new TowerPreBuild(new Coordinate2D(5, 15), this));
    }

    public void openNewBuildDialog(final TowerPreBuild tower) {
        if (newBuildDialogLayer == null || newBuildDialogLayer.getTowerPreBuild() != tower) {
            newBuildDialogLayer = new NewBuildDialogLayer(tower);
        }
    }

    public void openTowerDialog(final Tower tower) {
        if (towerDialogLayer == null || towerDialogLayer.getTower() != tower) {
            towerDialogLayer = new TowerDialogLayer(tower);
        }
    }

    public void closeNewBuildDialog() {
        newBuildDialogLayer = null;
    }

    public void closeTowerDialog() {
        towerDialogLayer = null;
    }

    public void buildTower(final TowerPreBuild preBuild) {
        if (!buildings.contains(preBuild)) {
            throw new IllegalArgumentException("preBuild must be already added to the buildings set");
        }
        toBuild.add(preBuild);
    }

    @Override
    public void processInput(final InputEvent inputEvent) {
        if (inputEvent.isConsumed()) {
            return;
        }

        if (newBuildDialogLayer != null) {
            newBuildDialogLayer.processInput(inputEvent);
        }

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
            closeNewBuildDialog();
            closeTowerDialog();
        }
    }

    @Override
    public void render() {
        if (newBuildDialogLayer != null) {
            newBuildDialogLayer.render();
        }

        if (towerDialogLayer != null) {
            towerDialogLayer.render();
        }

        for (final Building building : buildings) {
            building.render();
        }
    }

    @Override
    public void update(final long elapsed) {
        for (final Building building : buildings) {
            building.update(elapsed);
        }

        for (final TowerPreBuild preBuild : toBuild) {
            buildings.remove(preBuild);
            final Vector2D towerLocationBottomLeft = new Coordinate2D(
                    preBuild.getWorldTopLeft().getX(), preBuild.getWorldBottomRight().getY()
            );
            closeNewBuildDialog();
            buildings.add(new Tower(towerLocationBottomLeft, world));
        }
        toBuild.clear();
    }
}
