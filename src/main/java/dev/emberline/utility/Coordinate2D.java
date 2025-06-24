package dev.emberline.utility;

import org.apache.commons.geometry.euclidean.twod.Vector2D;

import java.io.Serializable;

/**
 * A 2D geometric point that represents the x, y coordinates.
 */
public class Coordinate2D implements dev.emberline.utility.Vector2D, Serializable {

    /**
     * x coordinate
     */
    private double x;
    /**
     * y coordinate
     */
    private double y;

    /**
     * The x coordinate.
     *
     * @return the x coordinate
     */
    @Override
    public final double getX() {
        return x;
    }


    /**
     * The y coordinate.
     *
     * @return the y coordinate
     */
    @Override
    public final double getY() {
        return y;
    }

    /**
     * Creates a new instance of {@code Coordinate2D}.
     *
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     */
    public Coordinate2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    private Vector2D toPoint2D(double x, double y) {
        return Vector2D.of(x, y);
    }

    /**
     * Computes the distance between this point and point {@code (x1, y1)}.
     *
     * @param x1 the x coordinate of other point
     * @param y1 the y coordinate of other point
     * @return the distance between this point and point {@code (x1, y1)}.
     */
    @Override
    public double distance(double x1, double y1) {
        return toPoint2D(this.x, this.y).distance(Vector2D.of(x1, y1));
    }

    /**
     * Computes the distance between this point and the specified {@code point}.
     *
     * @param point the other point
     * @return the distance between this point and the specified {@code point}.
     * @throws NullPointerException if the specified {@code point} is null
     */
    @Override
    public double distance(dev.emberline.utility.Vector2D point) {
        return distance(point.getX(), point.getY());
    }

    /**
     * Returns a point with the specified coordinates added to the coordinates
     * of this point.
     *
     * @param x the X coordinate addition
     * @param y the Y coordinate addition
     * @return the point with added coordinates
     */
    @Override
    public dev.emberline.utility.Vector2D add(double x, double y) {
        Vector2D sum = toPoint2D(this.x, this.y).add(Vector2D.of(x, y));
        return new Coordinate2D(sum.getX(), sum.getY());
    }

    /**
     * Returns a point with the coordinates of the specified point added to the
     * coordinates of this point.
     *
     * @param point the point whose coordinates are to be added
     * @return the point with added coordinates
     * @throws NullPointerException if the specified {@code point} is null
     */
    @Override
    public dev.emberline.utility.Vector2D add(dev.emberline.utility.Vector2D point) {
        return add(point.getX(), point.getY());
    }

    /**
     * Returns a point with the specified coordinates subtracted from
     * the coordinates of this point.
     *
     * @param x the X coordinate subtraction
     * @param y the Y coordinate subtraction
     * @return the point with subtracted coordinates
     */
    @Override
    public dev.emberline.utility.Vector2D subtract(double x, double y) {
        Vector2D difference = toPoint2D(this.x, this.y).subtract(Vector2D.of(x, y));
        return new Coordinate2D(difference.getX(), difference.getY());
    }

    /**
     * Returns a point with the coordinates of this point multiplied
     * by the specified factor
     *
     * @param factor the factor multiplying the coordinates
     * @return the point with multiplied coordinates
     */
    @Override
    public dev.emberline.utility.Vector2D multiply(double factor) {
        return new Coordinate2D(getX() * factor, getY() * factor);
    }

    /**
     * Returns a point with the coordinates of the specified point subtracted
     * from the coordinates of this point.
     *
     * @param point the point whose coordinates are to be subtracted
     * @return the point with subtracted coordinates
     * @throws NullPointerException if the specified {@code point} is null
     */
    @Override
    public dev.emberline.utility.Vector2D subtract(dev.emberline.utility.Vector2D point) {
        return subtract(point.getX(), point.getY());
    }

    /**
     * Normalizes the relative magnitude vector represented by this instance.
     * Returns a vector with the same direction and magnitude equal to 1.
     * If this is a zero vector, a zero vector is returned.
     *
     * @return the normalized vector represented by a {@code Coordinate2D} instance
     */
    @Override
    public dev.emberline.utility.Vector2D normalize() {
        Vector2D sum = toPoint2D(this.x, this.y).normalize();
        return new Coordinate2D(sum.getX(), sum.getY());
    }

    /**
     * Computes the angle (in degrees) between the vector represented
     * by this point and the specified vector.
     *
     * @param x the X magnitude of the other vector
     * @param y the Y magnitude of the other vector
     * @return the angle between the two vectors measured in degrees
     */
    @Override
    public double angle(double x, double y) {
        return toPoint2D(this.x, this.y).angle(Vector2D.of(x, y));
    }

    /**
     * Computes the angle (in degrees) between the vector represented
     * by this point and the vector represented by the specified point.
     *
     * @param point the other vector
     * @return the angle between the two vectors measured in degrees,
     * {@code NaN} if any of the two vectors is a zero vector
     * @throws NullPointerException if the specified {@code point} is null
     */
    @Override
    public double angle(dev.emberline.utility.Vector2D point) {
        return angle(point.getX(), point.getY());
    }

    /**
     * Computes magnitude (length) of the relative magnitude vector represented
     * by this instance.
     *
     * @return magnitude of the vector
     */
    @Override
    public double magnitude() {
        return toPoint2D(this.x, this.y).norm();
    }

    /**
     * Computes dot (scalar) product of the vector represented by this instance
     * and the specified vector.
     *
     * @param x the X magnitude of the other vector
     * @param y the Y magnitude of the other vector
     * @return the dot product of the two vectors
     */
    @Override
    public double dotProduct(double x, double y) {
        return toPoint2D(this.x, this.y).dot(Vector2D.of(x, y));
    }

    /**
     * Computes dot (scalar) product of the vector represented by this instance
     * and the specified vector.
     *
     * @param vector the other vector
     * @return the dot product of the two vectors
     * @throws NullPointerException if the specified {@code vector} is null
     */
    @Override
    public double dotProduct(dev.emberline.utility.Vector2D vector) {
        return dotProduct(vector.getX(), vector.getY());
    }

    /**
     * Return the unit vector representing the direction of displacement
     * from this vector to the given vector.
     *
     * @param x
     * @param y
     * @return
     */
    public dev.emberline.utility.Vector2D directionTo(double x, double y) {
        Vector2D vector = Vector2D.of(x, y);
        Vector2D direction;
        if (vector.equals(Vector2D.of(this.x, this.y))) {
            direction = Vector2D.ZERO;
        } else {
            direction = toPoint2D(this.x, this.y).directionTo(vector);
        }
        return new Coordinate2D(direction.getX(), direction.getY());
    }

    /**
     * Return the unit vector representing the direction of displacement
     * from this vector to the given vector.
     *
     * @param vector
     * @return
     */
    public dev.emberline.utility.Vector2D directionTo(dev.emberline.utility.Vector2D vector) {
        return this.directionTo(vector.getX(), vector.getY());
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare
     * @return true if this point is the same as the obj argument; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Coordinate2D other) {
            return toPoint2D(this.x, this.y).equals(Vector2D.of(other.getX(), other.getY()));
        } else return false;
    }

    /**
     * Returns a hash code value for the point.
     *
     * @return a hash code value for the point.
     */
    @Override
    public int hashCode() {
        return toPoint2D(this.x, this.y).hashCode();
    }

    /**
     * Returns a string representation of this {@code Coordinate2D}.
     * This method is intended to be used only for informational purposes.
     * The content and format of the returned string might vary between
     * implementations.
     * The returned string might be empty but cannot be {@code null}.
     */
    @Override
    public String toString() {
        return toPoint2D(this.x, this.y).toString();
    }
}
