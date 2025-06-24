package dev.emberline.game.world.entities.enemies.enemy;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.effects.DummyEffect;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.AbstractEnemy.FacingDirection;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy.UniformMotion;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


class EnemyUpdateComponent implements Updatable {
    private enum EnemyState {WALKING, DYING, DEAD}

    private final AbstractEnemy enemy;
    private EnemyState enemyState;
    private EnchantmentEffect activeEffect = new DummyEffect();
    private World world;

    private double health;
    private double slowFactor = 1;

    private Vector2D position;
    private Vector2D velocity;

    private final List<Vector2D> destinations = new ArrayList<>();
    private int destinationsIdx = 0;

    EnemyUpdateComponent(Vector2D spawnPoint, World world, AbstractEnemy enemy) {
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
        this.velocity = destinations.get(destinationsIdx).subtract(position)
                .normalize().multiply(enemy.getSpeed());
    }

    private void clearEffect() {
        activeEffect.endEffect(enemy);
        activeEffect = new DummyEffect();
    }

    public double getHealth() {
        return this.health;
    }

    @Override
    public void update(long elapsed) {
        switch (enemyState) {
            case WALKING -> walk(elapsed);
            case DYING -> dying();
            case DEAD -> {
            }
        }
        enemy.getAnimationUpdatable().update(elapsed);
    }

    private void walk(long elapsed) {
        if (activeEffect.isExpired()) {
            clearEffect();
        }
        else {
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
    List<UniformMotion> getMotionUntil(long time) {
        Vector2D curr = new Coordinate2D(position.getX(), position.getY());
        List<UniformMotion> enemyMotion = new ArrayList<>();

        long durationAcc = 0;
        for (int i = destinationsIdx; i < destinations.size() && durationAcc < time; i++) {
            Vector2D nextDestination = new Coordinate2D(destinations.get(i).getX(), destinations.get(i).getY());

            Vector2D velocity = nextDestination.subtract(curr).normalize().multiply(enemy.getSpeed() * slowFactor);
            long duration = (long) (curr.distance(nextDestination) / (enemy.getSpeed() * slowFactor));
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

    void dealDamage(double damage) {
        health -= damage;
        if (health <= 0) {
            setDying();
        }
    }

    void applyEffect(EnchantmentEffect effect) {
        this.activeEffect = effect;
    }

    void setSlowFactor(double slowFactor) {
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
        int angle = Math.round((float) Math.toDegrees(Math.atan2(-velocity.getY(), velocity.getX())));
        return switch (angle) {
            case -180 -> FacingDirection.LEFT;
            case 90 -> FacingDirection.UP;
            case 0 -> FacingDirection.RIGHT;
            case -90 -> FacingDirection.DOWN;
            default ->
                    throw new IllegalStateException("The only handled cases of velocity are: LEFT, UP, RIGHT, DOWN. Found angle: " + angle);
        };
    }

    EnemyAnimation.EnemyAppearance getEnemyAppearance() {
        if (enemyState == EnemyState.DYING) {
            return EnemyAnimation.EnemyAppearance.DYING;
        }
        return activeEffect.getEnemyAppearance();
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
                attack();
                return;
            }
            currDestination = destinations.get(++destinationsIdx);
            Vector2D nextDirection = currDestination.subtract(position).normalize();

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
