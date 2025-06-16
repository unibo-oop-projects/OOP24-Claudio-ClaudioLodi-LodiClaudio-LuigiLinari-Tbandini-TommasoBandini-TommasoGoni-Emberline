package dev.emberline.game.world.entities.enemies.enemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.effects.DummyEffect;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.Enemy.EnemyState;
import dev.emberline.game.world.entities.enemies.enemy.Enemy.FacingDirection;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy.UniformMotion;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;

public class EnemyUpdateComponent implements Updatable {
    private static final double VELOCITY_MAG = 1.0 / 1e9; // 1 tile/s
    private static final double FULL_HEALTH = 50;
    private final double width = 1;
    private final double height = 1;

    private final Enemy enemy;
    private EnemyState enemyState;
    private EnchantmentEffect activeEffect = new DummyEffect();

    private double health = FULL_HEALTH;
    private double slowFactor = 1;

    private Vector2D position;
    private Vector2D velocity;

    private final List<Vector2D> destinations = new ArrayList<>();
    private int destinationsIdx = 0;
    public EnemyUpdateComponent(Vector2D spawnPoint, World world, Enemy enemy) {
        this.enemy = enemy;

        // Init destinations
        for (
            Optional<Vector2D> next = world.getWaveManager().getWave().getNext(spawnPoint);
            next.isPresent();
                next.isPresent();
                next = world.getWaveManager().getWave().getNext(destinations.getLast())
        ) {
            destinations.add(next.get());
        }

        this.position = spawnPoint.subtract(0, height/2);
        destinations.replaceAll(coordinate2D -> coordinate2D.subtract(0, height / 2));

        this.enemyState = !destinations.isEmpty() ? EnemyState.WALKING : EnemyState.ATTACKING;
        if (enemyState == EnemyState.WALKING) {
            this.velocity = destinations.get(destinationsIdx).subtract(position).normalize().multiply(VELOCITY_MAG);
        }
    }

    @Override
    public void update(long elapsed) {
        switch (enemyState) {
            case WALKING:
                if (!activeEffect.isExpired()) {
                    // activeEffect.updateEffect(elapsed, enemy);
                }
                move(elapsed);
                break;

            case ATTACKING:
                // attack();
                enemyState = EnemyState.DEAD;
                break;

            case DYING:
                if (enemy.getAnimation().hasEnded()) {
                    enemyState = EnemyState.DEAD;
                }

            case DEAD:
        }

        enemy.getAnimation().update(elapsed);
    }

    /**
     * @param time time of truncation
     * @return All the uniform motions starting from the current position of the enemy
     * that is described by a list of {@code UniformMotion}
     */
    public List<UniformMotion> getMotionUntil(long time) {
        Vector2D curr = new Coordinate2D(position.getX(), position.getY());
        List<UniformMotion> enemyMotion = new ArrayList<>();

        long durationAcc = 0;
        for (int i = destinationsIdx; i < destinations.size() && durationAcc < time; i++) {
            Vector2D nextDestination = new Coordinate2D(destinations.get(i).getX(), destinations.get(i).getY());

            Vector2D velocity = nextDestination.subtract(curr).normalize().multiply(VELOCITY_MAG);
            Long duration = (long)(curr.distance(nextDestination) / VELOCITY_MAG);
            durationAcc += duration;

            enemyMotion.add(new UniformMotion(curr, velocity, duration));
            curr = nextDestination;
        }
        // leftover time
        if (durationAcc < time) {
            enemyMotion.add(new UniformMotion(curr, Vector2D.ZERO, time - durationAcc));
        }
        return enemyMotion;
    }

    public void dealDamage(double damage) {
        health -= damage;
        if (health <= 0) {
            enemyState = EnemyState.DYING;
        }
    }

    public void applyEffect(EnchantmentEffect effect) {
        this.activeEffect = effect;
    }

    public void setSlowFactor(double slowFactor) {
        this.slowFactor = slowFactor;
    }

    public boolean isDead() {
        return enemyState == EnemyState.DEAD;
    }

    public boolean isHittable() {
        return enemyState == EnemyState.WALKING;
    }

    public Vector2D getPosition() {
        return new Coordinate2D(position.getX(), position.getY());
    }

    double getWidth() {
        return width;
    }

    double getHeight() {
        return height;
    }

    double getHealthPercentage() {
        return health / FULL_HEALTH;
    }

    FacingDirection getFacingDirection() {
        int angle = Math.round((float)Math.toDegrees(Math.atan2(velocity.getY(), velocity.getX())));
        return switch(angle) {
            case 180 -> FacingDirection.LEFT;
            case 90 -> FacingDirection.UP;
            case 0 -> FacingDirection.RIGHT;
            case -90 -> FacingDirection.DOWN;
            default -> throw new IllegalStateException("The only handled cases of velocity are: LEFT, UP, RIGHT, DOWN");
        };
    }

    EnemyState getEnemyState() {
        return enemyState;
    }

    EnchantmentInfo.Type getEffectType() {
        return activeEffect.getEffectType();
    }

    private void move(long elapsed) {
        // move along the velocity vector
        position = position.add(velocity.multiply(slowFactor).multiply(elapsed));

        // position -> destination vector
        Vector2D currDestination = destinations.get(destinationsIdx);
        Vector2D posToDest = currDestination.subtract(position);
        double dot = posToDest.dotProduct(velocity);

        // dot <= 0 => either overshot or exactly at currDestination
        while (dot <= 0) {
            // if overshot => go back to destination and do the difference in movement in the next direction
            double overshootAmount = posToDest.magnitude();

            position = currDestination;
            if (currDestination == destinations.getLast()) {
                enemyState = EnemyState.ATTACKING;
                return;
            }
            currDestination = destinations.get(++destinationsIdx);
            Vector2D nextDirection = currDestination.subtract(position).normalize();

            // correction
            position = position.add(nextDirection.multiply(overshootAmount));

            velocity = nextDirection.multiply(VELOCITY_MAG);

            posToDest = currDestination.subtract(position);
            dot = posToDest.dotProduct(velocity);
        }
    }
}
