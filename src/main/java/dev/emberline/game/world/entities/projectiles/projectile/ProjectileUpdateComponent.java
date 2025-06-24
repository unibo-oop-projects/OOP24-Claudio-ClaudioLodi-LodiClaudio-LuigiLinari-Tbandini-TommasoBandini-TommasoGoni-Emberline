package dev.emberline.game.world.entities.projectiles.projectile;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.game.world.entities.projectiles.FlightPathNotFound;
import dev.emberline.game.world.entities.projectiles.events.ProjectileHitEvent;
import dev.emberline.game.world.entities.projectiles.events.ProjectileHitListener;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

class ProjectileUpdateComponent implements Updatable {

    private record Trajectory(Function<Long, Projectile.PositionAndRotation> getPositionAndRotationAt,
                              Long flightTime) {
    }

    private static final long MAX_FLIGHT_TIME = 10_000_000_000L; // 10s
    private final double VELOCITY_MAG;

    /// Parameters defining the parabolic motion (arc of a circle) with a scaling factor of 1 
    private static final double START_THETA =        3.0/4 * Math.PI;
    private static final double END_THETA =          START_THETA - 1.0/2 * Math.PI;
    private static final double UNIT_RADIUS =        1.0/(2*Math.cos(END_THETA));
    private static final double UNIT_ARC_LENGTH =     UNIT_RADIUS * (START_THETA - END_THETA);
    ///

    private final long flightTime;
    private long currFlightTime;

    private final Function<Long, Projectile.PositionAndRotation> getPositionAndRotationAt;
    private Vector2D position;
    private Double rotation; // degrees since only used by javafx

    private final ProjectileHitListener projectileHitListener;
    private final ProjectileHitEvent projectileHitEvent;
    private boolean hasHit = false;

    private final ProjectileInfo projectileInfo;
    private final EnchantmentInfo enchantmentInfo;

    private final Projectile owner;

    ProjectileUpdateComponent(final Vector2D start, final IEnemy target,
                                     final ProjectileInfo projInfo, final EnchantmentInfo enchInfo, final World world, final Projectile owner) throws FlightPathNotFound {
        this.VELOCITY_MAG = projInfo.getProjectileSpeed() / 1e9; // Converted to tile/ns

        final Vector2D prediction = enemyPrediction(start, target);

        final Trajectory _trajectory = calculateTrajectory(start, prediction);
        this.getPositionAndRotationAt = _trajectory.getPositionAndRotationAt();
        this.flightTime = _trajectory.flightTime();
 
        this.currFlightTime = 0;
        final Projectile.PositionAndRotation positionAndRotation = getPositionAndRotationAt.apply(currFlightTime);
        this.position = positionAndRotation.position();
        this.rotation = positionAndRotation.rotation();

        this.projectileHitEvent = new ProjectileHitEvent(prediction, projInfo, enchInfo);
        this.projectileHitListener = world.getProjectileHitListener();

        this.projectileInfo = projInfo;
        this.enchantmentInfo = enchInfo;

        this.owner = owner;
    }

    /**
     * Updates the projectile's state based on the elapsed time.
     * If the projectile is still in its flight phase, its current position and rotation
     * are updated. Once its flight time is complete, the projectile is marked as having hit
     * its target, and the hit event is triggered. Additionally, the owning object's animation
     * is also updated.
     *
     * @param elapsed the time elapsed since the last update, in nanoseconds
     */
    @Override
    public void update(final long elapsed) {
        if (currFlightTime < flightTime) {
            currFlightTime += elapsed;
    
            final Projectile.PositionAndRotation positionAndRotation = getPositionAndRotationAt.apply(currFlightTime);
            position = positionAndRotation.position();
            rotation = positionAndRotation.rotation();
        }
        else {
            projectileHitListener.onProjectileHit(projectileHitEvent);
            hasHit = true;
        }

        owner.getAnimationUpdatable().update(elapsed);
    }

    public boolean hasHit() {
        return hasHit;
    }

    Projectile.PositionAndRotation getPositionAndRotation() {
        return new Projectile.PositionAndRotation(position, rotation);
    }

    ProjectileInfo.Type getSizeType() {
        return projectileInfo.type();
    }

    EnchantmentInfo.Type getEnchantmentType() {
        return enchantmentInfo.type();
    }

    /**
     * Knowing that the flight time of the projectile scales with the distance from the enemy, we can substitute the enemy motion equation
     * with the position of the enemy and solve for time.
     * Due to the changes of direction of the enemy, it's equation of motion is also described with a duration.
     * So to find the right {@code t}, we need to try with each motion and take the first that doesn't exceed the duration.
     *
     * @param start
     * @param target
     * @return The first position of the enemy such that the flight time of the projectile is equal
     * to the time it takes the enemy to reach that position
     * @throws IllegalStateException if that position doesn't exist or the flight time to reach it exceeds {@code MAX_FLIGHT_TIME}
     */
    private Vector2D enemyPrediction(final Vector2D start, final IEnemy target) throws FlightPathNotFound {
        final List<IEnemy.UniformMotion> targetMotion = target.getMotionUntil(MAX_FLIGHT_TIME);
        final var motionsIt = targetMotion.iterator();
        IEnemy.UniformMotion currMotion = null;

        double bestDeltaT = -1, t_0 = 0;
        boolean found = false;
        while (motionsIt.hasNext() && !found) {
            currMotion = motionsIt.next();
            final Vector2D E_0 = currMotion.origin();
            final Vector2D v_E = currMotion.velocity();
            final long duration = currMotion.durationNs();

            /// Solve quadratic
            // (l / v_proj) ^ 2
            final double lv_projSq = UNIT_ARC_LENGTH / VELOCITY_MAG * (UNIT_ARC_LENGTH / VELOCITY_MAG);

            final double A1 = lv_projSq * (v_E.magnitude() * v_E.magnitude());
            final double A = 1.0 - A1;
            
            final double B1 = 2 * t_0;
            final double B2 = 2 * lv_projSq * E_0.subtract(start).dotProduct(v_E);
            final double B = B1 - B2;

            final double C1 = t_0 * t_0;
            final double C2 = lv_projSq * (E_0.subtract(start).magnitude() * E_0.subtract(start).magnitude());
            final double C = C1 - C2;

            // sqrt delta
            final double sqrtD = Math.sqrt(B * B - 4 * A * C);

            final double deltaT1 = (-B + sqrtD) / (2 * A);
            final double deltaT2 = (-B - sqrtD) / (2 * A);
            ///

            // The t is valid only if it's > 0 and inside that specific uniform motion
            bestDeltaT = Stream.of(deltaT1, deltaT2)
                    .filter(t -> t.compareTo(0.0) >= 0 && t.compareTo((double) duration) <= 0)
                    .min(Double::compare)
                    .orElse(-1.0);
            found = Double.compare(bestDeltaT, -1.0) != 0;

            if (!found) {
                t_0 += (double) duration;
            }
        }

        if (found) {
            return currMotion.origin().add(currMotion.velocity().multiply(bestDeltaT));
        }
        else {
            throw new FlightPathNotFound("Location to hit the target doesn't exist or the flight time to reach it exceeds the MAX_FLIGHT_TIME");
        }
    }

    /**
     * The determination of the trajectory is done by scaling the model trajectory by the distance from {@code start} to {@code end}
     * and rotating it by the angle formed by the vector {@code end - start}. Note that when the {@code end} sits to the left of {@code start},
     * the model trajectory also has to be mirrored.
     *
     * @param start
     * @param end
     * @return The {@code Trajectory} from {@code start} to {@code end}
     */
    private Trajectory calculateTrajectory(final Vector2D start, final Vector2D end) {
        // Start and end are in world coordinates so the y is "flipped", for simplicity convert them to canonical
        final Vector2D cEnd = worldToCanonical(end);
        final Vector2D cStart = worldToCanonical(start);

        // Linear transformation: e1 -> B1, e2 -> B2
        // It rotates space so that the "x-axis" is aligned with the direction from the starting point to the ending point
        // The "y-axis" sits 90° from the trasformed x-axis, if the ending point is on the right of the starting point the direction is upwards otherwise is downwards
        final Vector2D B1 = cEnd.subtract(cStart).normalize();
        final double signY = B1.getX() >= 0 ? +1 : -1;
        final Vector2D B2 = new Coordinate2D(-B1.getY(), B1.getX()).multiply(signY);
        final Function<Vector2D, Vector2D> rotation = p -> new Coordinate2D(
                B1.getX() * p.getX() + B2.getX() * p.getY(),
                B1.getY() * p.getX() + B2.getY() * p.getY()
        );
        final double scalingFactor = cStart.distance(cEnd);
        final double tranformationAngle = Math.toDegrees(Math.atan2(B1.getY(), B1.getX())); // I and II qudrant > 0, III and IV quadrant < 0

        final double radius = scalingFactor * UNIT_RADIUS;
        final double angularVelocity = -(VELOCITY_MAG / radius);

        final long timeInAir = (long) (scalingFactor * UNIT_ARC_LENGTH / VELOCITY_MAG);
        return new Trajectory(t -> {
            if (t > flightTime) {
                t = flightTime;
            }
            
            final double theta = theta(t, START_THETA, angularVelocity);
            
            // Compute the position on the scaled trajectory, rotate it and translate so that the starting point is cStart
            Vector2D pos = rotation.apply(r(theta, radius, START_THETA)).add(cStart);

            final Vector2D tangentTraj = r_derivative(theta, radius, angularVelocity);
            final double tangentTrajAngle = Math.toDegrees(Math.atan2(tangentTraj.getY(), tangentTraj.getX()));
            double angle;
            // abs > 90 => II and III quadrant, the angle needs to be reflected
            if (Math.abs(tranformationAngle) > 90) {
                angle = tranformationAngle - tangentTrajAngle;
            }
            else {
                angle = tangentTrajAngle + tranformationAngle;
            }

            // conversion to world
            pos = canonicalToWorld(pos);
            angle *= -1; // the angles are positive counterclockwise in the screen coordinates

            return new Projectile.PositionAndRotation(pos, angle);
        }, timeInAir);
    }

    ///// Circular uniform motion
    /**
     * @param t       time
     * @param theta_0 starting angle
     * @param w       angular velocity
     * @return the angle after {@code t} time with starting angle {@code theta_0} and {@code w} angular velocity
     */
    private double theta(final long t, final double theta_0, final double w) {
        return theta_0 + w * t;
    }

    /**
     * @param theta
     * @param radius
     * @param theta_0
     * @return the position vector on a circonference of radius {@code radius} at {@code theta}, translated so that {@code (cos(theta_0), sin(theta_0))} is in {@code (0, 0)}
     */
    private Vector2D r(final double theta, final double radius, final double theta_0) {
        final double x = radius * (Math.cos(theta) - Math.cos(theta_0));
        final double y = radius * (Math.sin(theta) - Math.sin(theta_0));
        return new Coordinate2D(x, y);
    }

    /**
     * @param theta
     * @param radius
     * @param w      angular velocity
     * @return vector tangent to the trajectory at {@code theta}
     */
    private Vector2D r_derivative(final double theta, final double radius, final double w) {
        final double x = -radius * w * Math.sin(theta);
        final double y = radius * w * Math.cos(theta);
        return new Coordinate2D(x, y);
    }

    /// // Circular uniform motion

    private Vector2D worldToCanonical(final Vector2D p) {
        return new Coordinate2D(p.getX(), -p.getY());
    }

    private Vector2D canonicalToWorld(final Vector2D p) {
        return new Coordinate2D(p.getX(), -p.getY());
    }
}
