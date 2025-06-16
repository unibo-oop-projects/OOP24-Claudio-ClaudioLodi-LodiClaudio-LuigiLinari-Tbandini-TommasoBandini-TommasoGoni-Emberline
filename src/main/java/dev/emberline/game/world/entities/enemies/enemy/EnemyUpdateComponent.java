package dev.emberline.game.world.entities.enemies.enemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.EnchantmentInfo.Type;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.Enemy.EnemyState;
import dev.emberline.game.world.entities.enemies.enemy.Enemy.FacingDirection;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy.UniformMotion;
import javafx.geometry.Point2D;
import utility.Coordinate2D;
import utility.Tile;

public class EnemyUpdateComponent implements Updatable {

    private final double VELOCITY_MAG = 1.0 / 1e9; // 1 tile/s
    private double slowFactor = 1;

    private Coordinate2D position;
    private Coordinate2D velocity;
    private List<Coordinate2D> destinations;
    private int destinationsIdx = 0;

    private double width = 1;
    private double height = 1;

    private final double FULL_HEALTH = 50;
    private double health;
    
    private EnchantmentEffect activeEffect;
    
    private EnemyState enemyState;
    
    private final Enemy owner;

    public EnemyUpdateComponent(Coordinate2D spawnPoint, World world, Enemy owner) {
        this.owner = owner;
        this.health = FULL_HEALTH;

        // Init destinations
        destinations = new ArrayList<>();
        for (
            Optional<Coordinate2D> next = world.getWaveManager().getWave().getNext(spawnPoint);
            next.isPresent(); 
            next = world.getWaveManager().getWave().getNext(destinations.getLast())
        ) {
            destinations.add(
                next.get()
            );
        }
        this.position = spawnPoint.subtract(0, height/2);
        for (int i = 0; i < destinations.size(); i++) {
            destinations.set(i, destinations.get(i).subtract(0, height/2));
        }
        
        this.enemyState = destinations.size() != 0 ? EnemyState.WALKING : EnemyState.ATTACKING;
        if (enemyState == EnemyState.WALKING) {
            this.velocity = destinations.get(destinationsIdx).subtract(position).normalize().multiply(VELOCITY_MAG);
        }
        
        this.activeEffect = new EnchantmentEffect() {
            @Override
            public Type getEffectType() {
                return Type.BASE;
            }

            @Override
            public boolean isExpired() {
                return true;
            }
        };
    }

    @Override
    public void update(long elapsed) {
        switch (enemyState) {
            case WALKING:
                if (!activeEffect.isExpired()) {
                    // activeEffect.updateEffect(elapsed, owner);
                }
                move(elapsed);
                break;

            case ATTACKING:
                // attack();
                enemyState = EnemyState.DEAD;
                break;

            case DYING:
                if (owner.getAnimation().hasEnded()) {
                    enemyState = EnemyState.DEAD;
                }
            
            case DEAD:
        }

        owner.getAnimation().update(elapsed);
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

    public Point2D getPosition() {
        return new Point2D(position.getX(), position.getY());
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
        int angle = Math.round(
            (float)Math.toDegrees(Math.atan2(velocity.getY(), velocity.getX()))
        );
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

        Coordinate2D currDestination = destinations.get(destinationsIdx);
        // position -> destination vector
        Coordinate2D posToDest = currDestination.subtract(position);
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
            Coordinate2D nextDirection = currDestination.subtract(position).normalize();
            
            // correction
            position = position.add(nextDirection.multiply(overshootAmount));

            velocity = nextDirection.multiply(VELOCITY_MAG);

            posToDest = currDestination.subtract(position);
            dot = posToDest.dotProduct(velocity);
        }
    }
}
