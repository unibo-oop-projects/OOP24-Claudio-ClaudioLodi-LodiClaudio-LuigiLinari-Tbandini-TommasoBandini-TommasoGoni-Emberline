package dev.emberline.game.world.entities.enemy;

import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import dev.emberline.core.GameLoop;
import dev.emberline.core.animations.Animation;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;
import utility.Pair;
import utility.Tile;
import utility.Coordinate2D;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Enemy implements Updatable, Renderable {
    
    /** 
     * Uniform motion (s_0 + v * t) with t in [0, {@code duration}] ns
    */
    public record UniformMotion(Point2D origin, Point2D velocity, long duration) {};

    private enum EnemyBehaviour { MOVING, ATTACKING }

    private Coordinate2D position;
    private Coordinate2D velocity;
    private List<Coordinate2D> destinations;
    private int destinationsIdx = 0;

    private World world;

    private final double VELOCITY_MAG = 0.000000001;
    private final int    FULL_HEALTH  = 500;

    private int health;
    /// Other stats
    ///
    
    private Animation animation;

    private EnemyBehaviour enemyBehaviour;
    
    public Enemy(Coordinate2D spawnPoint, World world) {
        this.position = spawnPoint;
        this.world = world;
        
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

        List<Image> animationStates = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            animationStates.add(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/enemies/pigs/" + i + ".png")))
            );
        }
        animation = new Animation(animationStates, 250_000_000);

        this.enemyBehaviour = destinationsIdx != destinations.size() ? 
            EnemyBehaviour.MOVING : EnemyBehaviour.ATTACKING;
    }

    @Override
    public void update(long elapsed) {
        animation.update(elapsed);
        
        if (enemyBehaviour == EnemyBehaviour.MOVING) {
            move(elapsed);
        }
    }

    @Override
    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        double sizeX = 25;
        double sizeY = 25;

        double screenX = cs.toScreenX(position.getX() + 0.5) - sizeX/2;
        double screenY = cs.toScreenY(position.getY() + 0.5) - sizeY/2;

        Image currAnimationState = animation.getAnimationState();

        renderer.addRenderTask(new RenderTask(RenderPriority.ENEMIES, () -> {
            gc.drawImage(currAnimationState, screenX, screenY, sizeX, sizeY);
        }));
    }

    public boolean isDead() {
        return health <= 0;
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
}
