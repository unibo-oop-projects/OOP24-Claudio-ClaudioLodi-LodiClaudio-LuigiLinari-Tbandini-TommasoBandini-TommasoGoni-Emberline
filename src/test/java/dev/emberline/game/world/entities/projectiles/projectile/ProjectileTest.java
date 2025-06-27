package dev.emberline.game.world.entities.projectiles.projectile;

import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.EnemiesManager;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.game.world.entities.projectiles.FlightPathNotFound;
import dev.emberline.game.world.entities.projectiles.events.ProjectileHitListener;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class ProjectileTest {

    private static final long MAX_FLIGHT_TIME = 10_000_000_000L;
    private final ProjectileInfo projInfo = new ProjectileInfo(ProjectileInfo.Type.BASE, 0);
    private final EnchantmentInfo enchInfo = new EnchantmentInfo(EnchantmentInfo.Type.BASE, 0);

    @Mock
    private World world;

    @Mock
    private EnemiesManager enemiesManager;

    @Mock
    private IEnemy enemy;
    private double health = 100.0;

    private ProjectileHitListener projectileHitListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(enemy.getMotionUntil(anyLong())).thenReturn(
                List.of(new IEnemy.UniformMotion(new Coordinate2D(1, 0), Vector2D.ZERO, MAX_FLIGHT_TIME))
        );
        when(enemy.getHealth()).thenReturn(health);
        doAnswer(invocation -> {
            final double damage = invocation.getArgument(0);
            health -= damage;
            return null;
        }).when(enemy).dealDamage(anyDouble());

        when(enemiesManager.getNear(any(), anyDouble())).thenReturn(List.of(enemy));
        projectileHitListener = new ProjectileHitListener(enemiesManager);
        when(world.getProjectileHitListener()).thenReturn(projectileHitListener);
    }

    @Test
    void testHitAndDamageToEnemy() {
        final Vector2D start = new Coordinate2D(0, 0);
        final Projectile projectile;
        try {
            projectile = new Projectile(
                    start, enemy,
                    projInfo, enchInfo,
                    world
            );
        } catch (final FlightPathNotFound e) {
            Assertions.fail("Flight path not found", e);
            return;
        }

        final double enemyHealthBefore = health;

        long totalElapsed = 0;
        while (!projectile.hasHit() && totalElapsed < MAX_FLIGHT_TIME) {
            final long step = 1_000_000_000L;
            totalElapsed += step;
            projectile.update(step);
        }
        Assertions.assertTrue(projectile.hasHit());

        Assertions.assertTrue(enemyHealthBefore > health);
    }
}
