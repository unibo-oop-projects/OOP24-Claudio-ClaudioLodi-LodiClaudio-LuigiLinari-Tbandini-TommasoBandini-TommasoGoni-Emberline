package dev.emberline.game.world.entities.enemy;

import dev.emberline.core.GameLoop;
import dev.emberline.core.animations.Animation;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.world.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utility.Coordinate2D;
import utility.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Enemy implements Updatable, Renderable {

    private enum EnemyBehaviour { MOVING, ATTACKING }

    private Vector2D position;
    private Vector2D velocity;
    private List<Coordinate2D> destinations;
    private int destinationsIdx = 0;

    private World world;

    private final double VELOCITY_MAG = 0.000000002;
    private final int    FULL_HEALTH  = 500;

    private int health;
    /// Other stats
    ///
    
    private Animation animation;

    private EnemyBehaviour enemyBehaviour;
    
    public Enemy(Vector2D spawnPoint, World world) {
        this.position = spawnPoint;
        this.world = world;
        
        destinations = new ArrayList<>();
        Optional<Vector2D> next = world.getWaveManager().getWave().getNext(
            new Coordinate2D(position.getX(), position.getY())
        );
        while (next.isPresent()) {
            destinations.add(
                new Coordinate2D(next.get().getX(), next.get().getY())
            );
            next = world.getWaveManager().getWave().getNext(
                    new Coordinate2D(destinations.getLast().getX(), destinations.getLast().getY())
            );
        }

        this.velocity = destinations.get(destinationsIdx).subtract(position).normalize().multiply(VELOCITY_MAG);
        this.health = FULL_HEALTH;

        List<Image> animationStates = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            animationStates.add(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/debug/" + i + ".png")))
            );
        }
        animation = new Animation(animationStates, 250_000_000);

        this.enemyBehaviour = destinationsIdx != destinations.size() ? 
            EnemyBehaviour.MOVING : EnemyBehaviour.ATTACKING;
    }

    @Override
    public void update(long elapsed) {
        animation.update(elapsed);
        health -= 4;
        if (enemyBehaviour == EnemyBehaviour.MOVING) {
            move(elapsed);
        }
    }

    @Override
    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        double sizeX = cs.getScale()*0.5;
        double sizeY = cs.getScale()*0.5;

        double screenX = cs.toScreenX(position.getX()) - sizeX/2;
        double screenY = cs.toScreenY(position.getY()) - sizeY;

        Image currAnimationState = animation.getAnimationState();

        renderer.addRenderTask(new RenderTask(RenderPriority.ENEMIES, () -> {
            gc.drawImage(currAnimationState, screenX, screenY, sizeX, sizeY);
        }));
    }

    public boolean isDead() {
        return health <= 0;
    }

    private void move(long elapsed) {
        Vector2D currDestination = destinations.get(destinationsIdx);

        // move along the velocity vector
        position = position.add(velocity.multiply(elapsed));

        // position -> destination vector
        Vector2D posToDest = currDestination.subtract(position);
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
            Vector2D nextDirection = currDestination.subtract(position).normalize();
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
    public Vector2D getPositionAfter(long deltaTime) {
        Vector2D currPosition = new Coordinate2D(position.getX(), position.getY());
        Vector2D currVelocity = new Coordinate2D(velocity.getX(), velocity.getY());
        int currDestinationIdx = destinationsIdx;

        // simulated movement
        move(deltaTime);
        Vector2D predictedPosition = new Coordinate2D(position.getX(), position.getY());
        
        position = currPosition;
        velocity = currVelocity;
        destinationsIdx = currDestinationIdx;

        return predictedPosition;
    }
}
