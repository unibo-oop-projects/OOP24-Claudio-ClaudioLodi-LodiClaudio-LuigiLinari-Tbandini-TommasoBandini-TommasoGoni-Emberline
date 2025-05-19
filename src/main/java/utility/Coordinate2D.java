 package utility;
 /**
  * A 2D geometric point that represents the x, y coordinates.
  */
 public class Coordinate2D implements Vector2D {
 
     /**
      * The x coordinate.
      * DefaultValue 0.0
      */
     private final double x;
 
     /**
      * The x coordinate.
      * @return the x coordinate
      */
     @Override
    public final double getX() {
         return x;
     }
 
     /**
      * The y coordinate.
      * DefaultValue 0.0
      */
     private final double y;
 
     /**
      * The y coordinate.
      * @return the y coordinate
      */
     @Override
    public final double getY() {
         return y;
     }
 
     /**
      * Cache the hash code to make computing hashes faster.
      */
     private int hash = 0;
 
     /**
      * Creates a new instance of {@code Point2D}.
      * @param x the x coordinate of the point
      * @param y the y coordinate of the point
      */
     public Coordinate2D(double x, double y) {
         this.x = x;
         this.y = y;
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
         double a = getX() - x1;
         double b = getY() - y1;
         return Math.sqrt(a * a + b * b);
     }
 
     /**
      * Computes the distance between this point and the specified {@code point}.
      *
      * @param point the other point
      * @return the distance between this point and the specified {@code point}.
      * @throws NullPointerException if the specified {@code point} is null
      */
     @Override
    public double distance(Vector2D point) {
         return distance(point.getX(), point.getY());
     }
 
     /**
      * Returns a point with the specified coordinates added to the coordinates
      * of this point.
      * @param x the X coordinate addition
      * @param y the Y coordinate addition
      * @return the point with added coordinates
      */
     @Override
    public Vector2D add(double x, double y) {
         return new Coordinate2D(
                 getX() + x,
                 getY() + y);
     }
 
     /**
      * Returns a point with the coordinates of the specified point added to the
      * coordinates of this point.
      * @param point the point whose coordinates are to be added
      * @return the point with added coordinates
      * @throws NullPointerException if the specified {@code point} is null
      */
     @Override
    public Vector2D add(Vector2D point) {
         return add(point.getX(), point.getY());
     }
 
     /**
      * Returns a point with the specified coordinates subtracted from
      * the coordinates of this point.
      * @param x the X coordinate subtraction
      * @param y the Y coordinate subtraction
      * @return the point with subtracted coordinates
      */
     @Override
    public Vector2D subtract(double x, double y) {
         return new Coordinate2D(
                 getX() - x,
                 getY() - y);
     }
 
     /**
      * Returns a point with the coordinates of this point multiplied
      * by the specified factor
      * @param factor the factor multiplying the coordinates
      * @return the point with multiplied coordinates
      */
     @Override
    public Vector2D multiply(double factor) {
         return new Coordinate2D(getX() * factor, getY() * factor);
     }
 
     /**
      * Returns a point with the coordinates of the specified point subtracted
      * from the coordinates of this point.
      * @param point the point whose coordinates are to be subtracted
      * @return the point with subtracted coordinates
      * @throws NullPointerException if the specified {@code point} is null
      */
     @Override
    public Vector2D subtract(Vector2D point) {
         return subtract(point.getX(), point.getY());
     }
 
     /**
      * Normalizes the relative magnitude vector represented by this instance.
      * Returns a vector with the same direction and magnitude equal to 1.
      * If this is a zero vector, a zero vector is returned.
      * @return the normalized vector represented by a {@code Point2D} instance
      */
     @Override
    public Vector2D normalize() {
         final double mag = magnitude();
 
         if (mag == 0.0) {
             return new Coordinate2D(0.0, 0.0);
         }
 
         return new Coordinate2D(
                 getX() / mag,
                 getY() / mag);
     }

     /**
      * Computes the angle (in degrees) between the vector represented
      * by this point and the specified vector.
      * @param x the X magnitude of the other vector
      * @param y the Y magnitude of the other vector
      * @return the angle between the two vectors measured in degrees
      */
     @Override
    public double angle(double x, double y) {
         final double ax = getX();
         final double ay = getY();
 
         final double delta = (ax * x + ay * y) / Math.sqrt(
                 (ax * ax + ay * ay) * (x * x + y * y));
 
         if (delta > 1.0) {
             return 0.0;
         }
         if (delta < -1.0) {
             return 180.0;
         }
 
         return Math.toDegrees(Math.acos(delta));
     }
 
     /**
      * Computes the angle (in degrees) between the vector represented
      * by this point and the vector represented by the specified point.
      * @param point the other vector
      * @return the angle between the two vectors measured in degrees,
      *         {@code NaN} if any of the two vectors is a zero vector
      * @throws NullPointerException if the specified {@code point} is null
      */
     @Override
    public double angle(Vector2D point) {
         return angle(point.getX(), point.getY());
     }
 
     /**
      * Computes the angle (in degrees) between the three points with this point
      * as a vertex.
      * @param p1 one point
      * @param p2 other point
      * @return angle between the vectors (this, p1) and (this, p2) measured
      *         in degrees, {@code NaN} if the three points are not different
      *         from one another
      * @throws NullPointerException if {@code p1} or {@code p2} is null
      */
     @Override
    public double angle(Vector2D p1, Vector2D p2) {
         final double x = getX();
         final double y = getY();
 
         final double ax = p1.getX() - x;
         final double ay = p1.getY() - y;
         final double bx = p2.getX() - x;
         final double by = p2.getY() - y;
 
         final double delta = (ax * bx + ay * by) / Math.sqrt(
                 (ax * ax + ay * ay) * (bx * bx + by * by));
 
         if (delta > 1.0) {
             return 0.0;
         }
         if (delta < -1.0) {
             return 180.0;
         }
 
         return Math.toDegrees(Math.acos(delta));
     }
 
     /**
      * Computes magnitude (length) of the relative magnitude vector represented
      * by this instance.
      * @return magnitude of the vector
      */
     @Override
    public double magnitude() {
         final double x = getX();
         final double y = getY();
 
         return Math.sqrt(x * x + y * y);
     }
 
     /**
      * Computes dot (scalar) product of the vector represented by this instance
      * and the specified vector.
      * @param x the X magnitude of the other vector
      * @param y the Y magnitude of the other vector
      * @return the dot product of the two vectors
      */
     @Override
    public double dotProduct(double x, double y) {
         return getX() * x + getY() * y;
     }
 
     /**
      * Computes dot (scalar) product of the vector represented by this instance
      * and the specified vector.
      * @param vector the other vector
      * @return the dot product of the two vectors
      * @throws NullPointerException if the specified {@code vector} is null
      */
     @Override
    public double dotProduct(Vector2D vector) {
         return dotProduct(vector.getX(), vector.getY());
     }

     /**
      * Indicates whether some other object is "equal to" this one.
      *
      * @param obj the reference object with which to compare
      * @return true if this point is the same as the obj argument; false otherwise
       */
     @Override public boolean equals(Object obj) {
         if (obj == this) return true;
         if (obj instanceof Coordinate2D) {
             Vector2D other = (Vector2D) obj;
             return getX() == other.getX() && getY() == other.getY();
         } else return false;
     }
 
     /**
      * Returns a hash code value for the point.
      * @return a hash code value for the point.
      */
     @Override public int hashCode() {
         if (hash == 0) {
             long bits = 7L;
             bits = 31L * bits + Double.doubleToLongBits(getX());
             bits = 31L * bits + Double.doubleToLongBits(getY());
             hash = (int) (bits ^ (bits >> 32));
         }
         return hash;
     }
 
     /**
      * Returns a string representation of this {@code Point2D}.
      * This method is intended to be used only for informational purposes.
      * The content and format of the returned string might vary between
      * implementations.
      * The returned string might be empty but cannot be {@code null}.
      */
     @Override public String toString() {
         return "Point2D [x = " + getX() + ", y = " + getY() + "]";
     }
 }
