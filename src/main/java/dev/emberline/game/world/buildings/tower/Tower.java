package dev.emberline.game.world.buildings.tower;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.model.TowerInfoProvider;
import dev.emberline.game.world.Building;
import dev.emberline.game.world.World;
import dev.emberline.game.world.buildings.TowersManager;
import dev.emberline.game.world.entities.enemies.IEnemiesManager;
import dev.emberline.gui.event.*;
import dev.emberline.utility.Vector2D;

import java.util.Map;

public class Tower extends Building implements TowerInfoProvider, GuiEventListener {
    private static String configsPath = "/sprites/towerAssets/tower.json";
    private static class Metadata {
        @JsonProperty("width")
        private double worldWidth;
        @JsonProperty("height")
        private Map<ProjectileInfo.Type, Double> worldHeight;
    }
    private static Metadata metadata = ConfigLoader.loadConfig(ConfigLoader.loadNode(configsPath).get("worldDimensions"), Metadata.class);

    private final TowerUpdateComponent towerUpdateComponent;
    private final TowerRenderComponent towerRenderComponent;

    public Tower(Vector2D locationBottomLeft, World world) {
        this.towerUpdateComponent = new TowerUpdateComponent(locationBottomLeft, world, this);
        this.towerRenderComponent = new TowerRenderComponent(this);
    }

    @Override
    public void onGuiEvent(GuiEvent event) {
        towerUpdateComponent.onGuiEvent(event);
    }

    @Override
    public ProjectileInfo getProjectileInfo() {
        return towerUpdateComponent.getProjectileInfo();
    }

    @Override
    public EnchantmentInfo getEnchantmentInfo() {
        return towerUpdateComponent.getEnchantmentInfo();
    }

    @Override
    public Vector2D getWorldTopLeft() {
        return towerUpdateComponent.getLocationBottomLeft().subtract(0, getWorldHeight());
    }

    @Override
    public Vector2D getWorldBottomRight() {
        return towerUpdateComponent.getLocationBottomLeft().add(getWorldWidth(), 0);
    }

    @Override
    protected void clicked() {
        towerUpdateComponent.clicked();
    }

    @Override
    public void update(long elapsed) {
        towerUpdateComponent.update(elapsed);
    }

    @Override
    public void render() {
        towerRenderComponent.render();
    }

    double getWorldWidth() {
        return metadata.worldWidth;
    }

    double getWorldHeight() {
        return metadata.worldHeight.get(getProjectileInfo().type());
    }
}
