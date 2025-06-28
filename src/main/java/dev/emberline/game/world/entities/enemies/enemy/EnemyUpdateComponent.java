package dev.emberline.game.world.entities.enemies.enemy;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.effects.DummyEffect;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.AbstractEnemy.FacingDirection;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy.UniformMotion;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


class EnemyUpdateComponent implements Updatable, Serializable {
    @Serial
    private static final long serialVersionUID = 8979305885961605613L;

    private enum EnemyState implements Serializable {
        WALKING,
        DYING,
        DEAD
    }

    private final AbstractEnemy enemy;
    private EnemyState enemyState;
    private EnchantmentEffect activeEffect = new DummyEffect();
    private final World world;

    private double health;
    private double slowFactor = 1;

    private Vector2D position;
    private Vector2D velocity;

    private final List<Vector2D> destinations = new ArrayList<>();
    private int destinationsIdx;

    EnemyUpdateComponent(final Vector2D spawnPoint, final World world, final AbstractEnemy enemy) {
        this.enemy = enemy;
        this.health = enemy.getFullHealth();
        this.world = world;

        // Init destinations
        Optional<Vector2D> next = world.getWaveManager().getWave().getNext(spawnPoint);
        while (next.isPresent()) {
            destinations.add(next.get());
            next = world.getWaveManager().getWave().getNext(destinations.getLast());
        }
        if (destinations.isEmpty()) {
            throw new IllegalStateException("The enemy must have at least one destination.");
        }

        this.position = spawnPoint.subtract(0, enemy.getHeight() / 2);
        destinations.replaceAll(coordinate2D -> coordinate2D.subtract(0, enemy.getHeight() / 2));

        this.enemyState = EnemyState.WALKING;
        this.velocity = destinations.getFirst().subtract(position)
                .normalize().multiply(enemy.getSpeed());
    }

    private void clearEffect() {
        activeEffect.endEffect(enemy);
        activeEffect = new DummyEffect();
    }

    public double getHealth() {
        return this.health;
    }

    /**
     * Updates the enemy based on its current state and the elapsed time.
     * Depending on the enemy's state, it performs specific actions such as walking,
     * handling dying logic or updating related animations.
     *
     * @param elapsed the time elapsed since the last update in nanoseconds
     */
    @Override
    public void update(final long elapsed) {
        switch (enemyState) {
            case WALKING -> walk(elapsed);
            case DYING -> dying();
            case DEAD -> {
            }
            default -> {
                throw new IllegalStateException("The only handled enemy states are: WALKING, DYING and DEAD");
            }
        }
        enemy.getAnimationUpdatable().update(elapsed);
    }

    private void walk(final long elapsed) {
        if (activeEffect.isExpired()) {
            clearEffect();
        } else {
            activeEffect.updateEffect(enemy, elapsed);
        }
        move(elapsed);
    }

    private void dying() {
        if (enemy.isDyingAnimationFinished()) {
            setDead();
        }
    }

    /**
     * @param time time of truncation
     * @return All the uniform motions starting from the current position of the enemy
     * that is described by a list of {@code UniformMotion}
     */
    List<UniformMotion> getMotionUntil(final long time) {
        Vector2D curr = new Coordinate2D(position.getX(), position.getY());
        final List<UniformMotion> enemyMotion = new ArrayList<>();

        long durationAcc = 0;
        for (int i = destinationsIdx; i < destinations.size() && durationAcc < time; i++) {
            final Vector2D nextDestination = new Coordinate2D(destinations.get(i).getX(), destinations.get(i).getY());

            final Vector2D velocity = nextDestination.subtract(curr).normalize().multiply(enemy.getSpeed() * slowFactor);
            final long duration = (long) (curr.distance(nextDestination) / (enemy.getSpeed() * slowFactor));
            durationAcc += duration;

            enemyMotion.add(new UniformMotion(curr, velocity, duration));
            curr = new Coordinate2D(nextDestination.getX(), nextDestination.getY());
        }
        // leftover time
        if (durationAcc < time) {
            enemyMotion.add(new UniformMotion(curr, Vector2D.ZERO, time - durationAcc));
        }
        return enemyMotion;
    }

    /**
     * @see IEnemy#getRemainingDistanceToTarget()
     *
     * @return the remaining distance to the target destination.
     */
    public double getRemainingDistanceToTarget() {
        double remainingDistance = 0;
        Vector2D currPosition = new Coordinate2D(position.getX(), position.getY());
        for (int i = destinationsIdx; i < destinations.size(); ++i) {
            Vector2D currDestination = destinations.get(i);
            remainingDistance += currPosition.distance(currDestination);
            currPosition = new Coordinate2D(currDestination.getX(), currDestination.getY());
        }
        return remainingDistance;
    }

    void dealDamage(final double damage) {
        health -= damage;
        if (health <= 0) {
            setDying();
        }
    }

    void applyEffect(final EnchantmentEffect effect) {
        this.activeEffect = effect;
    }

    void setSlowFactor(final double slowFactor) {
        this.slowFactor = slowFactor;
    }

    double getSlowFactor() {
        return slowFactor;
    }

    boolean isDead() {
        return enemyState == EnemyState.DEAD;
    }

    boolean isHittable() {
        return enemyState == EnemyState.WALKING;
    }

    Vector2D getPosition() {
        return new Coordinate2D(position.getX(), position.getY());
    }

    double getHealthPercentage() {
        return Math.clamp(health / enemy.getFullHealth(), 0, 1);
    }

    FacingDirection getFacingDirection() {
        final int leftAngle = -180, upAngle = 90, rightAngle = 0, downAngle = -90;
        final int angle = Math.round((float) Math.toDegrees(Math.atan2(-velocity.getY(), velocity.getX())));
        return switch (angle) {
            case leftAngle -> FacingDirection.LEFT;
            case upAngle -> FacingDirection.UP;
            case rightAngle -> FacingDirection.RIGHT;
            case downAngle -> FacingDirection.DOWN;
            default ->
                    throw new IllegalStateException(
                            "The only handled cases of velocity are: LEFT, UP, RIGHT, DOWN. Found angle: " + angle
                    );
        };
    }

    EnemyAnimation.EnemyAppearance getEnemyAppearance() {
        if (enemyState == EnemyState.DYING) {
            return EnemyAnimation.EnemyAppearance.DYING;
        }
        return activeEffect.getEnemyAppearance();
    }

    private void move(final long elapsed) {
        // move along the velocity vector
        position = position.add(velocity.multiply(slowFactor).multiply(elapsed));

        // position -> destination vector
        Vector2D currDestination = destinations.get(destinationsIdx);
        Vector2D posToDest = currDestination.subtract(position);
        double dot = posToDest.dotProduct(velocity);

        // dot <= 0 => either overshot or exactly at currDestination
        while (dot <= 0) {
            // if overshot => go back to destination and do the difference in movement in the next direction
            final double overshootAmount = posToDest.magnitude();

            position = currDestination;
            if (currDestination.equals(destinations.getLast())) {
                attack();
                return;
            }
            currDestination = destinations.get(++destinationsIdx);
            final Vector2D nextDirection = currDestination.subtract(position).normalize();

            // correction
            position = position.add(nextDirection.multiply(overshootAmount));

            velocity = nextDirection.multiply(enemy.getSpeed());

            posToDest = currDestination.subtract(position);
            dot = posToDest.dotProduct(velocity);
        }
    }

    private void attack() {
        world.getPlayer().takeDamage();
        setDead();
    }

    private void setDying() {
        enemyState = EnemyState.DYING;
        clearEffect();
    }

    private void setDead() {
        enemyState = EnemyState.DEAD;
        clearEffect();
    }

}
