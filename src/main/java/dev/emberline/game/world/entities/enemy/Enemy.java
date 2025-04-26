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

    // Up to debate if we actually want to use the Point2D class for vectors (sqrt for magnitude, immutable...) TODO
    private Point2D position;
    private Point2D velocityVector;
    private Point2D destination;

    private World world;

    private final double VELOCITY_MAG = 0.000000002;
    private final int    FULL_HEALTH  = 500;

    private int health;
    /// Other stats
    ///
    
    private Animation animation;
    
    public Enemy(Point2D spawnPoint, World world) {
        this.position = spawnPoint;
        this.world = world;

        Optional<Pair<Integer, Integer>> next = world.getWaveManager().getWave().getNext(
            new Pair<>((int)position.getX(), (int)position.getY())
        );
        this.destination = next.isEmpty() ? position : new Point2D(next.get().getX(), next.get().getY());
        // this.destination = world.getWave().getNextDestination(position);

        this.velocityVector = destination.subtract(position).normalize().multiply(VELOCITY_MAG);
        this.health = FULL_HEALTH;

        List<Image> animationStates = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            animationStates.add(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/debug/" + i + ".png")))
            );
        }
        animation = new Animation(animationStates, 1_000_000_000);
    }

    @Override
    public void update(long elapsed) {
        animation.update(elapsed);
        move(elapsed);
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
        // move along the velocity vector
        position = position.add(velocityVector.multiply(elapsed));

        // position -> destination same direction as the velocity     => didn't reach destination
        // position -> destination direction opposite to the velocity => overshot destination
        // position -> destination = (0, 0)                           => exactly arrived at destination

        Point2D posToDest = destination.subtract(position);
        double dot = posToDest.dotProduct(velocityVector);
        
        // same direction
        if (dot > 0) {
            return;
        }

        // dot <= 0 => either arrived perfectly at destination or overshot (->v * ->0 = 0)
        
        // if the new position exceeds the destination
        // go back to destination and do the difference in movement in the new direction

        double overshootAmount = posToDest.magnitude();

        Optional<Pair<Integer,Integer>> next = world.getWaveManager().getWave().getNext(new Pair<>((int)destination.getX(), (int)destination.getY()));
        /// TEST (TODO)
        if (next.isEmpty()) {
            position = destination;
            destination = position;
            health = 0;
            return;
        }
        /// 
        Point2D newDestination = new Point2D(next.get().getX(), next.get().getY());
        Point2D newDirection = newDestination.subtract(destination).normalize();

        position = destination.add(newDirection.multiply(overshootAmount));
        destination = newDestination;
        velocityVector = newDirection.multiply(VELOCITY_MAG);
    }
}
