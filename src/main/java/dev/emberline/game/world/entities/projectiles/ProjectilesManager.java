package dev.emberline.game.world.entities.projectiles;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.event.EventDispatcher;
import dev.emberline.core.sounds.AudioController;
import dev.emberline.core.sounds.event.SfxSoundEvent;
import dev.emberline.core.sounds.event.SfxSoundEvent.SoundType;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.game.world.entities.projectiles.projectile.IProjectile;
import dev.emberline.game.world.entities.projectiles.projectile.Projectile;
import dev.emberline.utility.Vector2D;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages the pool of projectiles within the game world.
 * This class handles the addition, updating, and rendering of projectiles.
 * It maintains a list of active projectiles and ensures they are removed
 * when they have reached their targets or completed their flight path.
 */
public class ProjectilesManager implements Updatable, Renderable {

    private final List<IProjectile> projectiles;
    private final World world;

    /**
     * Constructs a new ProjectilesManager instance.
     *
     * @param world the game world in which the {@code projectilesManager} operates.
     * @see ProjectilesManager
     */
    public ProjectilesManager(final World world) {
        this.projectiles = new LinkedList<>();
        this.world = world;
    }

    /**
     * Adds a new projectile to the pool of active projectiles.
     * If the projectile's flight path cannot be determined,
     * it will not be added and it will return false.
     *
     * @param start the starting position of the projectile
     * @param target the target enemy for the projectile
     * @param projInfo the information relative to the size of the projectile to be added
     * @param enchInfo the information relative to the enchantment of the projectile to be added
     * @return true if the projectile was successfully added, or false if the flight path
     *         could not be determined
     */
    public boolean addProjectile(final Vector2D start, final IEnemy target,
                                 final ProjectileInfo projInfo, final EnchantmentInfo enchInfo) {
        try {
            final Projectile projectile = new Projectile(start, target, projInfo, enchInfo, world);
            projectiles.add(projectile);
        } catch (final FlightPathNotFound e) {
            return false;
        }

        return true;
    }

    /**
     * Updates all active projectiles in the manager.
     * Each projectile's update logic is executed, and projectiles
     * that have hit their target are removed from the manager.
     *
     * @param elapsed the time elapsed since the last update in nanoseconds
     */
    @Override
    public void update(final long elapsed) {
        final Iterator<IProjectile> it = projectiles.iterator();
        IProjectile currProjectile;
        while (it.hasNext()) {
            currProjectile = it.next();

            currProjectile.update(elapsed);
            if (currProjectile.hasHit()) {
                AudioController.requestSfxSound(this, SoundType.PROJECTILE_LANDED);
                it.remove();
            }
        }
    }

    /**
     * Renders all active projectiles in the manager.
     */
    @Override
    public void render() {
        for (final IProjectile projectile : projectiles) {
            projectile.render();
        }
    }
}
