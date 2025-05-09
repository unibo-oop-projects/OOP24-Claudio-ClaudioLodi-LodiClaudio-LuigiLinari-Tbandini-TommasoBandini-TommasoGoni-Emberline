package dev.emberline.game.world;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.world.entities.enemies.EnemiesManager;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.game.world.entities.projectile.Projectile;
import dev.emberline.game.world.entities.projectile.ProjectileHitListener;
import dev.emberline.game.world.waves.WaveManager;
import javafx.geometry.Point2D;

public class World implements Updatable, Renderable {
    
    // Enemies
    private final EnemiesManager enemiesManager;
    // Towers
    // Waves
    private final WaveManager waveManager;

    /// TEST
    private final List<Projectile> projectiles;
    /// 
    private final ProjectileHitListener projectileHitListener;

    public World() {
        this.enemiesManager = new EnemiesManager(this);
        this.waveManager = new WaveManager(this);
        this.projectiles = new LinkedList<>();
        projectileHitListener = new ProjectileHitListener(enemiesManager);
    }

    public EnemiesManager getEnemiesManager() {
        return enemiesManager;
    }

    public ProjectileHitListener getProjectileHitListener() {
        return projectileHitListener;
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }

    private long acc = 0;
    @Override
    public void update(long elapsed) {
        enemiesManager.update(elapsed);
        waveManager.update(elapsed);
        for (final Projectile p : projectiles) {
            p.update(elapsed);
        }

        /// TEST
        acc += elapsed;
        if (acc >= 2_000_000_000L) {
            Iterator<IEnemy> enemiesIt = enemiesManager.getNear(Point2D.ZERO, 0).iterator();
            
            boolean foundEnemy = false;
            while (enemiesIt.hasNext() && !foundEnemy) {
                IEnemy currEnemy = enemiesIt.next();
                try {
                    Projectile projectile = new Projectile(
                        new Point2D(16, 8), currEnemy, 
                        new ProjectileInfo(ProjectileInfo.Type.BASE, 0),
                        new EnchantmentInfo(EnchantmentInfo.Type.BASE, 0), 
                        this
                    );
                    projectiles.add(projectile);
                    foundEnemy = true;
                    acc = 0;
                } catch (Exception e) {}
            }
        }
        ///
    }

    //TEST
    public void removeProjectile(Projectile proj) {
        projectiles.remove(proj);
    } 

    @Override
    public void render() {
        enemiesManager.render();

        for (final Projectile p : projectiles) {
            p.render();
        }
        
        waveManager.getWave().render();
    }
}
