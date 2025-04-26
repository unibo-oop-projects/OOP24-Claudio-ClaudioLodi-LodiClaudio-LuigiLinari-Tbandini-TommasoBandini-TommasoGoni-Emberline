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
import utility.pairs.Pair;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Enemy implements Updatable, Renderable {

    private enum EnemyBehaviour { MOVING, ATTACKING }

    // Up to debate if we actually want to use the Point2D class for vectors (sqrt for magnitude, immutable...) TODO
    private Point2D position;
    private Point2D velocity;
    private List<Point2D> destinations;
    private int destinationsIdx = 0;

    private World world;

    private final double VELOCITY_MAG = 0.000000002;
    private final int    FULL_HEALTH  = 500;

    private int health;
    /// Other stats
    ///
    
    private Animation animation;

    private EnemyBehaviour enemyBehaviour;
    
    public Enemy(Point2D spawnPoint, World world) {
        this.position = spawnPoint;
        this.world = world;
        
        destinations = new ArrayList<>();
        Optional<Pair<Integer, Integer>> next = world.getWaveManager().getWave().getNext(
            new Pair<>((int)position.getX(), (int)position.getY())
        );
        while (next.isPresent()) {
            destinations.add(
                new Point2D(next.get().getX(), next.get().getY())
            );
            next = world.getWaveManager().getWave().getNext(
                new Pair<>((int)destinations.getLast().getX(), (int)destinations.getLast().getY())
            );
        }

        this.velocity = destinations.get(destinationsIdx).subtract(position).normalize().multiply(VELOCITY_MAG);
        this.health = FULL_HEALTH;

        List<Image> animationStates = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            animationStates.add(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/enemies/" + i + ".png")))
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
        CoordinateSystem cs = renderer.getWorldContext().getCS();

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
        Point2D currDestination = destinations.get(destinationsIdx);

        // move along the velocity vector
        position = position.add(velocity.multiply(elapsed));

        // position -> destination vector
        Point2D posToDest = currDestination.subtract(position);
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
            Point2D nextDirection = currDestination.subtract(position).normalize();
            position = position.add(nextDirection.multiply(overshootAmount));

            velocity = nextDirection.multiply(VELOCITY_MAG);

            posToDest = currDestination.subtract(position);
            dot = posToDest.dotProduct(velocity);
        }
    }

    /**
     * @param deltaTime nanoseconds in the future
     * @return The position of the enemy after {@code deltaTime} nanoseconds
     */
    public Point2D getPositionAfter(long deltaTime) {
        Point2D currPosition = new Point2D(position.getX(), position.getY());
        Point2D currVelocity = new Point2D(velocity.getX(), velocity.getY());
        int currDestinationIdx = destinationsIdx;

        // simulated movement
        move(deltaTime);
        Point2D predictedPosition = new Point2D(position.getX(), position.getY());
        
        position = currPosition;
        velocity = currVelocity;
        destinationsIdx = currDestinationIdx;

        return predictedPosition;
    }
}
