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

/**
 * The {@code Tower} class represents a building within the game world with
 * capabilities to fire projectiles. The class extends {@code Building}
 * and implements {@code TowerInfoProvider} to provide information about its projectile and
 * enchantment characteristics.
 * <p>
 * The {@code Tower} dynamically retrieves its dimensions, firing location, and other properties
 * based on configuration data loaded from a JSON file.
 * <p>
 * It interacts with the game world to react to events such as clicks and updates.
 */
public class Tower extends Building implements TowerInfoProvider {
    private static String configsPath = "/sprites/towerAssets/tower.json";

    private static Metadata metadata = ConfigLoader.loadConfig(ConfigLoader.loadNode(configsPath).get("worldDimensions"), Metadata.class);

    private final World world;
    private final TowerUpdateComponent towerUpdateComponent;
    private final TowerRenderComponent towerRenderComponent;
    private final Vector2D locationBottomLeft;

    private ProjectileInfo projectileInfo = new ProjectileInfo(ProjectileInfo.Type.BASE, 0);
    private EnchantmentInfo enchantmentInfo = new EnchantmentInfo(EnchantmentInfo.Type.BASE, 0);

    private static class Metadata {
        @JsonProperty
        double width;
        @JsonProperty
        Map<ProjectileInfo.Type, Double> height;
        @JsonProperty
        double firingYOffsetTiles;
    }

    /**
     * Constructs a new Tower object with a specified location and associated world.
     *
     * @param locationBottomLeft the bottom-left corner location of the tower in the world
     * @param world the world instance where this tower exists
     */
    public Tower(final Vector2D locationBottomLeft, final World world) {
        this.world = world;
        this.towerUpdateComponent = new TowerUpdateComponent(world, this);
        this.towerRenderComponent = new TowerRenderComponent(this);
        this.locationBottomLeft = locationBottomLeft;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectileInfo getProjectileInfo() {
        return projectileInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EnchantmentInfo getEnchantmentInfo() {
        return enchantmentInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector2D getWorldTopLeft() {
        return locationBottomLeft.subtract(0, getWorldHeight());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector2D getWorldBottomRight() {
        return locationBottomLeft.add(getWorldWidth(), 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void clicked() {
        world.getTowersManager().closeNewBuildDialog();
        world.getTowersManager().openTowerDialog(this);
    }

    /**
     * Updates the tower
     * @see TowerUpdateComponent#update(long)
     */
    @Override
    public void update(final long elapsed) {
        towerUpdateComponent.update(elapsed);
    }

    /**
     * Renders the tower
     * @see TowerRenderComponent#render()
     */
    @Override
    public void render() {
        towerRenderComponent.render();
    }

    /**
     * Sets the upgradable information for this tower. This method determines whether the provided info
     * is a projectile-related upgrade or an enchantment-related upgrade and assigns it to the corresponding field.
     *
     * @param info the upgradable information to be assigned. This must be an instance of either
     *             {@code ProjectileInfo} or {@code EnchantmentInfo}.
     */
    public void setUpgradableInfo(final UpgradableInfo<?, ?> info) {
        if (info instanceof final ProjectileInfo infoCast) {
            projectileInfo = infoCast;
        } else if (info instanceof final EnchantmentInfo infoCast) {
            enchantmentInfo = infoCast;
        }
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
}
