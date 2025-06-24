package dev.emberline.game.world.entities.enemies;

import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

class EnemyMock implements IEnemy {
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


    private Vector2D position;

    EnemyMock(final Vector2D position) {
        this.position = position;
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(final Vector2D position) {
        this.position = position;
    }
}

class SpatialHashGridTest {

    int x_min = 0, y_min = 0, x_max = 100, y_max = 100;
    private final SpatialHashGrid hashGrid = new SpatialHashGrid(x_min, y_min, x_max, y_max);
    private final List<EnemyMock> enemies = new ArrayList<>();

    private final long seed = 123456789L;
    private final Random generator = new Random(seed);

    @Test
    void testAdd() {
        final int TEST_SIZE = 1000;
        for (int i = 0; i < TEST_SIZE; i++) {
            final EnemyMock newEnemy = generateEnemy();
            hashGrid.add(newEnemy);
            enemies.add(newEnemy);

            integrityCheck();
        }
    }

    @Test
    void testRemove() {
        testAdd();

        final Iterator<EnemyMock> it = enemies.iterator();
        while (it.hasNext()) {
            final IEnemy enemy = it.next();
            hashGrid.remove(enemy);
            it.remove();

            integrityCheck();
        }
        Assertions.assertEquals(enemies.size(), hashGrid.size());
    }

    @Test
    void testUpdate() {
        testAdd();

        for (final EnemyMock enemy : enemies) {
            moveEnemyRandom(enemy);
            hashGrid.update(enemy);

            integrityCheck();
        }
        Assertions.assertEquals(enemies.size(), hashGrid.size());
    }

    @Test
    void testEdges() {
        final EnemyMock enemy = new EnemyMock(new Coordinate2D(x_min, y_min));
        hashGrid.add(enemy);
        enemies.add(enemy);
        integrityCheck();

        enemy.setPosition(new Coordinate2D(x_max, y_min));
        hashGrid.update(enemy);
        integrityCheck();

        enemy.setPosition(new Coordinate2D(x_max, y_max));
        hashGrid.update(enemy);
        integrityCheck();

        enemy.setPosition(new Coordinate2D(x_min, y_max));
        hashGrid.update(enemy);
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
        return generator.nextInt(x_max - x_min - 1) + x_min;
    }

    private double nextY() {
        return generator.nextInt(y_max - y_min - 1) + y_min;
    }

    private void integrityCheck() {
        Assertions.assertEquals(enemies.size(), hashGrid.size());
        for (final EnemyMock enemy : enemies) {
            final Vector2D position = enemy.getPosition();
            final List<IEnemy> near = hashGrid.getNear(position, 0.1);
            Assertions.assertTrue(near.contains(enemy));
        }
    }
}