package dev.emberline.game.world.entities.enemies.enemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy.UniformMotion;
import javafx.geometry.Point2D;
import utility.Coordinate2D;
import utility.Tile;

public class EnemyUpdateComponent implements Updatable {

    private enum EnemyBehaviour { MOVING, ATTACKING }

    private final double VELOCITY_MAG = 0.000000001;
    private Coordinate2D position;
    private Coordinate2D velocity;
    private List<Coordinate2D> destinations;
    private int destinationsIdx = 0;

    private final double FULL_HEALTH = 50;
    private double health;
    
    private EnchantmentEffect activeEffect;
    
    private EnemyBehaviour enemyBehaviour;
    
    private final Enemy owner;

    public EnemyUpdateComponent(Coordinate2D spawnPoint, World world, Enemy owner) {
        this.owner = owner;
        this.position = spawnPoint;
        
        destinations = new ArrayList<>();
        Optional<Tile> next = world.getWaveManager().getWave().getNext(
            new Tile((int)position.getX(), (int)position.getY())
        );
        while (next.isPresent()) {
            destinations.add(
                new Coordinate2D(next.get().getX(), next.get().getY())
            );
            next = world.getWaveManager().getWave().getNext(
                new Tile((int)destinations.getLast().getX(), (int)destinations.getLast().getY())
            );
        }

        this.velocity = destinations.get(destinationsIdx).subtract(position).normalize().multiply(VELOCITY_MAG);
        this.health = FULL_HEALTH;

        this.enemyBehaviour = destinationsIdx != destinations.size() ? 
            EnemyBehaviour.MOVING : EnemyBehaviour.ATTACKING;

        this.activeEffect = new EnchantmentEffect() {
            @Override
            public boolean isExpired() {
                return true;
            }
        };
    }

    @Override
    public void update(long elapsed) {
        // animation.update(elapsed);

        if (!activeEffect.isExpired()) {
            // activeEffect.updateEffect(elapsed, owner);
        }
        
        if (enemyBehaviour == EnemyBehaviour.MOVING) {
            move(elapsed);
        }
    }

    private void move(long elapsed) {
        Coordinate2D currDestination = destinations.get(destinationsIdx);

        // move along the velocity vector
        position = position.add(velocity.multiply(elapsed));

        // position -> destination vector
        Coordinate2D posToDest = currDestination.subtract(position);
        double dot = posToDest.dotProduct(velocity);
        // dot <= 0 => either overshot or exactly at currDestination
        while (dot <= 0) {
            // if overshot => go back to destination and do the difference in movement in the next direction
            double overshootAmount = posToDest.magnitude();
            
            position = currDestination;
            if (currDestination == destinations.getLast()) {
                enemyBehaviour = EnemyBehaviour.ATTACKING;
                return;
            }
            currDestination = destinations.get(++destinationsIdx);
            
            // correction
            Coordinate2D nextDirection = currDestination.subtract(position).normalize();
            position = position.add(nextDirection.multiply(overshootAmount));

            velocity = nextDirection.multiply(VELOCITY_MAG);

            posToDest = currDestination.subtract(position);
            dot = posToDest.dotProduct(velocity);
        }
    }

    /**
     * @param time time of truncation
     * @return All the uniform motions starting from the current position of the enemy
     * That is described by a list of {@code UniformMotion}
     */
    public List<UniformMotion> getMotionUntil(long time) {
        Point2D curr = new Point2D(position.getX(), position.getY());
        List<UniformMotion> enemyMotion = new ArrayList<>();

        long durationAcc = 0;
        for (int i = destinationsIdx; i < destinations.size() && durationAcc < time; i++) {
            Point2D nextDestination = new Point2D(destinations.get(i).getX(), destinations.get(i).getY());

            Point2D velocity = nextDestination.subtract(curr).normalize().multiply(VELOCITY_MAG);
            Long duration = (long)(curr.distance(nextDestination) / VELOCITY_MAG);
            durationAcc += duration;
            
            enemyMotion.add(new UniformMotion(curr, velocity, duration));
            curr = nextDestination;
        }
        // leftover time
        if (durationAcc < time) {
            enemyMotion.add(new UniformMotion(curr, Point2D.ZERO, time - durationAcc));
        }
        return enemyMotion;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void dealDamage(double damage) {
        health -= damage;
    }

    public void applyEffect(EnchantmentEffect effect) {
        this.activeEffect = effect;
    }

    double getHealthPercentage() {
        return health / FULL_HEALTH;
    }

    Coordinate2D getPosition() {
        return position;
    }
}
