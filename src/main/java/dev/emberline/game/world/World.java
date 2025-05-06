package dev.emberline.game.world;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.entities.enemy.EnemiesManager;
import dev.emberline.game.world.entities.enemy.Enemy;
import dev.emberline.game.world.entities.projectile.Projectile;
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

    public World() {
        this.enemiesManager = new EnemiesManager(this);
        this.waveManager = new WaveManager(this);
        this.projectiles = new LinkedList<>();
    }

    public EnemiesManager getEnemiesManager() {
        return enemiesManager;
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
            Iterator<Enemy> enemiesIt = enemiesManager.getEnemiesInArea().iterator();
            
            boolean foundEnemy = false;
            while (enemiesIt.hasNext() && !foundEnemy) {
                Enemy currEnemy = enemiesIt.next();
                try {
                    Projectile projectile = new Projectile(new Point2D(16, 8), currEnemy, this);
                    projectiles.add(projectile);
                    foundEnemy = true;
                    acc = 0;
                } catch (Exception e) {}
            }
        }
        ///
    }

    @Override
    public void render() {
        enemiesManager.render();
        for (final Projectile p : projectiles) {
            p.render();
        }
    }
}
