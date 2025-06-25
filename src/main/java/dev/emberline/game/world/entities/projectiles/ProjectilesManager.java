package dev.emberline.game.world.entities.projectiles;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
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

public class ProjectilesManager implements Updatable, Renderable {

    private final List<IProjectile> projectiles;
    private final World world;

    public ProjectilesManager(final World world) {
        this.projectiles = new LinkedList<>();
        this.world = world;
    }

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

    @Override
    public void update(final long elapsed) {
        final Iterator<IProjectile> it = projectiles.iterator();
        IProjectile currProjectile;
        while (it.hasNext()) {
            currProjectile = it.next();

            currProjectile.update(elapsed);
            if (currProjectile.hasHit()) {
                it.remove();
            }
        }
    }

    @Override
    public void render() {
        for (final IProjectile projectile : projectiles) {
            projectile.render();
        }
    }
}
