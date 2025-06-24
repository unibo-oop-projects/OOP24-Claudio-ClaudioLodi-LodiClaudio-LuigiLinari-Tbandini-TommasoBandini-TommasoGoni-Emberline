package dev.emberline.utility;

public interface Vector2D {

    /**
     * Point or vector with both coordinates set to 0.
     */
    Vector2D ZERO = new Coordinate2D(0.0, 0.0);

    /**
     * The x coordinate.
     *
     * @return the x coordinate
     */
    double getX();

    /**
     * The y coordinate.
     *
     * @return the y coordinate
     */
    double getY();

    /**
     * Computes the distance between this point and point {@code (x1, y1)}.
     *
     * @param x1 the x coordinate of other point
     * @param y1 the y coordinate of other point
     * @return the distance between this point and point {@code (x1, y1)}.
     */
    double distance(double x1, double y1);

    /**
     * Computes the distance between this point and the specified {@code point}.
     *
     * @param point the other point
     * @return the distance between this point and the specified {@code point}.
     * @throws NullPointerException if the specified {@code point} is null
     */
    double distance(Vector2D point);

    /**
     * Returns a point with the specified coordinates added to the coordinates
     * of this point.
     *
     * @param x the X coordinate addition
     * @param y the Y coordinate addition
     * @return the point with added coordinates
     */
    Vector2D add(double x, double y);

    /**
     * Returns a point with the coordinates of the specified point added to the
     * coordinates of this point.
     *
     * @param point the point whose coordinates are to be added
     * @return the point with added coordinates
     * @throws NullPointerException if the specified {@code point} is null
     */
    Vector2D add(Vector2D point);

    /**
     * Returns a point with the specified coordinates subtracted from
     * the coordinates of this point.
     *
     * @param x the X coordinate subtraction
     * @param y the Y coordinate subtraction
     * @return the point with subtracted coordinates
     */
    Vector2D subtract(double x, double y);

    /**
     * Returns a point with the coordinates of this point multiplied
     * by the specified factor
     *
     * @param factor the factor multiplying the coordinates
     * @return the point with multiplied coordinates
     */
    Vector2D multiply(double factor);

    /**
     * Returns a point with the coordinates of the specified point subtracted
     * from the coordinates of this point.
     *
     * @param point the point whose coordinates are to be subtracted
     * @return the point with subtracted coordinates
     * @throws NullPointerException if the specified {@code point} is null
     */
    Vector2D subtract(Vector2D point);

    /**
     * Normalizes the relative magnitude vector represented by this instance.
     * Returns a vector with the same direction and magnitude equal to 1.
     * If this is a zero vector, a zero vector is returned.
     *
     * @return the normalized vector represented by a {@code Point2D} instance
     */
    Vector2D normalize();

    /**
     * Computes the angle (in degrees) between the vector represented
     * by this point and the specified vector.
     *
     * @param x the X magnitude of the other vector
     * @param y the Y magnitude of the other vector
     * @return the angle between the two vectors measured in degrees
     */
    double angle(double x, double y);

    /**
     * Computes the angle (in degrees) between the vector represented
     * by this point and the vector represented by the specified point.
     *
     * @param point the other vector
     * @return the angle between the two vectors measured in degrees,
     * {@code NaN} if any of the two vectors is a zero vector
     * @throws NullPointerException if the specified {@code point} is null
     */
    double angle(Vector2D point);

    /**
     * Computes magnitude (length) of the relative magnitude vector represented
     * by this instance.
     *
     * @return magnitude of the vector
     */
    double magnitude();

    /**
     * Computes dot (scalar) product of the vector represented by this instance
     * and the specified vector.
     *
     * @param x the X magnitude of the other vector
     * @param y the Y magnitude of the other vector
     * @return the dot product of the two vectors
     */
    double dotProduct(double x, double y);

    /**
     * Computes dot (scalar) product of the vector represented by this instance
     * and the specified vector.
     *
     * @param vector the other vector
     * @return the dot product of the two vectors
     * @throws NullPointerException if the specified {@code vector} is null
     */
    double dotProduct(Vector2D vector);

    /**
     * Return the unit vector representing the direction of displacement
     * from this vector to the given vector.
     *
     * @param x
     * @param y
     * @return
     */
    dev.emberline.utility.Vector2D directionTo(double x, double y);

    /**
     * Return the unit vector representing the direction of displacement
     * from this vector to the given vector.
     *
     * @param vector
     * @return
     */
    dev.emberline.utility.Vector2D directionTo(dev.emberline.utility.Vector2D vector);

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare
     * @return true if this point is the same as the obj argument; false otherwise
     */
    boolean equals(Object obj);

    /**
     * Returns a hash code value for the point.
     *
     * @return a hash code value for the point.
     */
    int hashCode();

    /**
     * Returns a string representation of this {@code Point2D}.
     * This method is intended to be used only for informational purposes.
     * The content and format of the returned string might vary between
     * implementations.
     * The returned string might be empty but cannot be {@code null}.
     */
    String toString();

}