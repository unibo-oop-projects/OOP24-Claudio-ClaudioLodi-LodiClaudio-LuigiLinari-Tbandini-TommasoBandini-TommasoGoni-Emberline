package dev.emberline.game.world.entities.enemy;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;
import javafx.geometry.Point2D;

public class Enemy implements Updatable, Renderable {

    // Up to debate if we actually want to use the Point2D class for vectors (sqrt for magnitude, immutable...) TODO
    private Point2D position;
    private Point2D velocityVector;
    private Point2D destination;

    private World world;

    private final double VELOCITY_MAG = 0.0000001;
    /// Other stats
    ///
    
    public Enemy(Point2D spawnPoint, World world) {
        this.position = spawnPoint;
        this.world = world;
        // this.destination = world.getWave().getNextDestination(position);
        // this.velocityVector = destination.subtract(position).normalize().multiply(VELOCITY_MAG);
    }

    @Override
    public void update(long elapsed) {
        
    }

    @Override
    public void render() {
        
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
        // Point2D newDestination = world.getWave().getNextDestination(destination);
        // Point2D newDirection = newDestination.subtract(destination).normalize();

        // position = destination.add(newDirection.scale(overshootAmount));
        // destination = newDestination;
        // velocityVector = newDirection.multiply(VELOCITY_MAG);
    }
}
