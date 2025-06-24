package dev.emberline.game.world.buildings.tower;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.model.TowerInfoProvider;
import dev.emberline.game.model.UpgradableInfo;
import dev.emberline.game.world.Building;
import dev.emberline.game.world.World;
import dev.emberline.utility.Vector2D;

import java.util.Map;

public class Tower extends Building implements TowerInfoProvider {
    private static String configsPath = "/sprites/towerAssets/tower.json";

    private static class Metadata {
        @JsonProperty
        double width;
        @JsonProperty
        Map<ProjectileInfo.Type, Double> height;
        @JsonProperty
        double firingYOffsetTiles;
    }

    private static Metadata metadata = ConfigLoader.loadConfig(ConfigLoader.loadNode(configsPath).get("worldDimensions"), Metadata.class);

    private final World world;
    private final TowerUpdateComponent towerUpdateComponent;
    private final TowerRenderComponent towerRenderComponent;
    private final Vector2D locationBottomLeft;

    private ProjectileInfo projectileInfo = new ProjectileInfo(ProjectileInfo.Type.BASE, 0);
    private EnchantmentInfo enchantmentInfo = new EnchantmentInfo(EnchantmentInfo.Type.BASE, 0);

    public Tower(final Vector2D locationBottomLeft, final World world) {
        this.world = world;
        this.towerUpdateComponent = new TowerUpdateComponent(world, this);
        this.towerRenderComponent = new TowerRenderComponent(this);
        this.locationBottomLeft = locationBottomLeft;
    }

    @Override
    public ProjectileInfo getProjectileInfo() {
        return projectileInfo;
    }

    @Override
    public EnchantmentInfo getEnchantmentInfo() {
        return enchantmentInfo;
    }

    @Override
    public Vector2D getWorldTopLeft() {
        return locationBottomLeft.subtract(0, getWorldHeight());
    }

    @Override
    public Vector2D getWorldBottomRight() {
        return locationBottomLeft.add(getWorldWidth(), 0);
    }

    @Override
    protected void clicked() {
        world.getTowersManager().closeNewBuildDialog();
        world.getTowersManager().openTowerDialog(this);
    }

    @Override
    public void update(final long elapsed) {
        towerUpdateComponent.update(elapsed);
    }

    @Override
    public void render() {
        towerRenderComponent.render();
    }

    Vector2D firingWorldCenterLocation() {
        return getWorldTopLeft().add(getWorldWidth() / 2, metadata.firingYOffsetTiles);
    }

    double getWorldWidth() {
        return metadata.width;
    }

    double getWorldHeight() {
        return metadata.height.get(getProjectileInfo().type());
    }

    public void setUpgradableInfo(final UpgradableInfo<?, ?> info) {
        if (info instanceof final ProjectileInfo infoCast) {
            projectileInfo = infoCast;
        }
        else if (info instanceof final EnchantmentInfo infoCast) {
            enchantmentInfo = infoCast;
        }
    }

    public static void setConfigsPath(final String configsPath) {
        Tower.configsPath = configsPath;
    }

    public static void setMetadata(final Metadata metadata) {
        Tower.metadata = metadata;
    }
}
