package dev.emberline.game.world.entities.projectiles;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.game.world.entities.projectiles.projectile.IProjectile;
import dev.emberline.game.world.entities.projectiles.projectile.Projectile;
import dev.emberline.utility.Vector2D;

public class ProjectilesManager implements Updatable, Renderable {
    
    private final List<IProjectile> projectiles;
    private final World world;

    public ProjectilesManager(World world) {
        this.projectiles = new LinkedList<>();
        this.world = world;
    }

    public boolean addProjectile(Vector2D start, IEnemy target,
    ProjectileInfo projInfo, EnchantmentInfo enchInfo) {
        try {
            Projectile projectile = new Projectile(start, target, projInfo, enchInfo, world);
            projectiles.add(projectile);
        } catch (FlightPathNotFound e) {
            return false;
        }

        return true;
    }

    public void update(long elapsed) {
        Iterator<IProjectile> it = projectiles.iterator();
        IProjectile currProjectile;
        while (it.hasNext()) {
            currProjectile = it.next();

            currProjectile.update(elapsed);
            if (currProjectile.hasHit()) {
                it.remove();
            }
        }
    }

    public void render() {
        for (final IProjectile projectile : projectiles) {
            projectile.render();
        }
    }
}
