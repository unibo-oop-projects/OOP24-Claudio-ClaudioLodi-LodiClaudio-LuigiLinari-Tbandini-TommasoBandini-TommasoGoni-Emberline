package dev.emberline.game.world.entities.enemies;

import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

class EnemyMock implements IEnemy {
    @Serial
    private static final long serialVersionUID = -882343261731098642L;
    private Vector2D position;

    @Override
    public double getHeight() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getWidth() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getHealth() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void dealDamage(final double damage) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void applyEffect(final EnchantmentEffect effect) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSlowFactor(final double slowFactor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDead() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isHittable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<UniformMotion> getMotionUntil(final long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void render() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(final long elapsed) {
        throw new UnsupportedOperationException();
    }

    EnemyMock(final Vector2D position) {
        this.position = position;
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public double getRemainingDistanceToTarget() {
        throw new UnsupportedOperationException();
    }

    public void setPosition(final Vector2D position) {
        this.position = position;
    }

    @Override
    public int getGoldReward() {
        throw new UnsupportedOperationException();
    }
}

class SpatialHashGridTest {

    private static final int X_MIN = 0, Y_MIN = 0, X_MAX = 100, Y_MAX = 100;
    private static final SpatialHashGrid HASH_GRID = new SpatialHashGrid(X_MIN, Y_MIN, X_MAX, Y_MAX);
    private static final List<EnemyMock> ENEMIES = new ArrayList<>();

    private static final long SEED = 123_456_789L;
    private static final Random GENERATOR = new Random(SEED);

    @Test
    void testAdd() {
        final int testSize = 1000;
        for (int i = 0; i < testSize; i++) {
            final EnemyMock newEnemy = generateEnemy();
            HASH_GRID.add(newEnemy);
            ENEMIES.add(newEnemy);

            integrityCheck();
        }
    }

    @Test
    void testRemove() {
        testAdd();

        final Iterator<EnemyMock> it = ENEMIES.iterator();
        while (it.hasNext()) {
            final IEnemy enemy = it.next();
            HASH_GRID.remove(enemy);
            it.remove();

            integrityCheck();
        }
        Assertions.assertEquals(ENEMIES.size(), HASH_GRID.size());
    }

    @Test
    void testUpdate() {
        testAdd();

        for (final EnemyMock enemy : ENEMIES) {
            moveEnemyRandom(enemy);
            HASH_GRID.update(enemy);

            integrityCheck();
        }
        Assertions.assertEquals(ENEMIES.size(), HASH_GRID.size());
    }

    @Test
    void testEdges() {
        final EnemyMock enemy = new EnemyMock(new Coordinate2D(X_MIN, Y_MIN));
        HASH_GRID.add(enemy);
        ENEMIES.add(enemy);
        integrityCheck();

        enemy.setPosition(new Coordinate2D(X_MAX, Y_MIN));
        HASH_GRID.update(enemy);
        integrityCheck();

        enemy.setPosition(new Coordinate2D(X_MAX, Y_MAX));
        HASH_GRID.update(enemy);
        integrityCheck();

        enemy.setPosition(new Coordinate2D(X_MIN, Y_MAX));
        HASH_GRID.update(enemy);
        integrityCheck();
    }

    private EnemyMock generateEnemy() {
        final double x = nextX();
        final double y = nextY();
        return new EnemyMock(new Coordinate2D(x, y));
    }

    private void moveEnemyRandom(final EnemyMock enemy) {
        enemy.setPosition(
                new Coordinate2D(
                        nextX(),
                        nextY()
                )
        );
    }

    private double nextX() {
        return GENERATOR.nextInt(X_MAX - X_MIN - 1) + X_MIN;
    }

    private double nextY() {
        return GENERATOR.nextInt(Y_MAX - Y_MIN - 1) + Y_MIN;
    }

    private void integrityCheck() {
        Assertions.assertEquals(ENEMIES.size(), HASH_GRID.size());
        for (final EnemyMock enemy : ENEMIES) {
            final Vector2D position = enemy.getPosition();
            final List<IEnemy> near = HASH_GRID.getNear(position, 0.1);
            Assertions.assertTrue(near.contains(enemy));
        }
    }
}
