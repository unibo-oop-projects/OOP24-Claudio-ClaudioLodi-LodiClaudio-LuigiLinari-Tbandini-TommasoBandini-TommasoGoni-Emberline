package dev.emberline.game.world.entities.enemies.enemy;

import dev.emberline.game.model.effects.BurnEffect;
import dev.emberline.game.model.effects.SlowEffect;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.concrete.Ogre;
import dev.emberline.game.world.entities.player.Player;
import dev.emberline.game.world.waves.Wave;
import dev.emberline.game.world.waves.WaveManager;
import dev.emberline.utility.Coordinate2D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.when;

class EnemyTest {

    @Mock
    private World world;

    @Mock
    private WaveManager waveManager;

    @Mock
    private Wave wave;

    @Mock
    private Player player;


    private AbstractEnemy enemy;

    private final Coordinate2D[] nodes = new Coordinate2D[]{
            new Coordinate2D(0, 0),
            new Coordinate2D(0, 1),
            new Coordinate2D(2, 1),
            new Coordinate2D(2, 2),
            new Coordinate2D(0, 2)
    };

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(world.getPlayer()).thenReturn(player);
        when(world.getWaveManager()).thenReturn(waveManager);
        when(waveManager.getWave()).thenReturn(wave);

        for (int i = 0; i < nodes.length - 1; i++) {
            when(wave.getNext(nodes[i])).thenReturn(Optional.of(nodes[i + 1]));
        }
        assert nodes.length > 0;
        when(wave.getNext(nodes[nodes.length - 1])).thenReturn(Optional.empty());

        enemy = new Ogre(nodes[0], world);
    }

    @Test
    void testMovementWithoutSlowFactor() {
        for (int i = 0; i < nodes.length - 1; i++) {
            final long expectedTravelTime = (long) Math.ceil(nodes[i].distance(nodes[i + 1]) /
                                                            (enemy.getSpeed() * enemy.getSlowFactor()));
            enemy.update(expectedTravelTime);

            Assertions.assertEquals(nodes[i + 1], enemy.getPosition().add(0, enemy.getHeight() / 2));
        }
    }

    @Test
    void testMovementWithSlowFactor() {
        double slowFactor = 0.5;
        enemy.setSlowFactor(slowFactor);
        testMovementWithoutSlowFactor();
    }

    @Test
    void testDealingDamage() {
        double health = enemy.getHealth();
        final double damage = 10.0;
        while (health > 0) {
            Assertions.assertTrue(enemy.getHealth() > 0);

            enemy.dealDamage(damage);
            health -= damage;
        }
        Assertions.assertTrue(enemy.getHealth() <= 0);
    }

    @Test
    void testBurnEffectDamageOverTime() {
        double dps = 5.0, duration = 1;
        final double initialHealth = enemy.getHealth();
        final BurnEffect burnEffect = new BurnEffect(dps, duration);
        enemy.applyEffect(burnEffect);

        long oneSecondNs = 1_000_000_000L;
        enemy.update(oneSecondNs);

        Assertions.assertTrue(burnEffect.isExpired());
        Assertions.assertEquals(enemy.getHealth(), initialHealth - dps);
    }

    @Test
    void testSlowEffectOverTime() {
        double slowFactor = 0.5, duration = 1;
        final SlowEffect slowEffect = new SlowEffect(slowFactor, duration);
        enemy.applyEffect(slowEffect);

        long oneSecondNs = 1_000_000_000L;
        enemy.update(oneSecondNs);
        Assertions.assertTrue(slowEffect.isExpired());
    }
}
