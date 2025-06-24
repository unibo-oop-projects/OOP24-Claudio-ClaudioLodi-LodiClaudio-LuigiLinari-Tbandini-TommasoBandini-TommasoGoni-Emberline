package dev.emberline.game.world.buildings.tower;

import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.EnemiesManager;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.game.world.entities.projectiles.ProjectilesManager;
import dev.emberline.utility.Vector2D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

class TowerTest {

    @Mock
    private World world;
    @Mock
    private EnemiesManager enemiesManager;
    @Mock
    private ProjectilesManager projectilesManager;
    @Mock
    private IEnemy enemy;

    private Tower tower;
    private int addedProjectiles = 0;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(world.getEnemiesManager()).thenReturn(enemiesManager);
        when(world.getProjectilesManager()).thenReturn(projectilesManager);
        when(enemiesManager.getNear(any(), anyDouble())).thenReturn(
                List.of(enemy)
        );
        doAnswer(invocation -> {
            addedProjectiles++;
            return null;
        }).when(projectilesManager).addProjectile(any(), any(), any(), any());

        tower = new Tower(Vector2D.ZERO, world);
    }

    @Test
    void testSetUpgradableInfo() {
        ProjectileInfo big = new ProjectileInfo(ProjectileInfo.Type.BIG, 1);

        tower.setUpgradableInfo(new ProjectileInfo(ProjectileInfo.Type.BIG, 1));
        Assertions.assertEquals(tower.getProjectileInfo(), big);
        while (tower.getProjectileInfo().canUpgrade()) {
            tower.setUpgradableInfo(tower.getProjectileInfo().getUpgrade());
            big = big.getUpgrade();
            Assertions.assertEquals(tower.getProjectileInfo(), big);
        }


        EnchantmentInfo ice = new EnchantmentInfo(EnchantmentInfo.Type.ICE, 1);

        tower.setUpgradableInfo(new EnchantmentInfo(EnchantmentInfo.Type.ICE, 1));
        Assertions.assertEquals(tower.getEnchantmentInfo(), ice);
        while (tower.getEnchantmentInfo().canUpgrade()) {
            tower.setUpgradableInfo(tower.getEnchantmentInfo().getUpgrade());
            ice = ice.getUpgrade();
            Assertions.assertEquals(tower.getEnchantmentInfo(), ice);
        }
    }

    @Test
    void testFiringRateAndAddingOfProjectiles() {
        long expectedShootingIntervalNs = (long) (1e9 / tower.getProjectileInfo().getFireRate());

        for (int i = 0; i < 1000; i++) {
            tower.update(expectedShootingIntervalNs);
            Assertions.assertEquals(addedProjectiles, i + 1);
        }
    }
}