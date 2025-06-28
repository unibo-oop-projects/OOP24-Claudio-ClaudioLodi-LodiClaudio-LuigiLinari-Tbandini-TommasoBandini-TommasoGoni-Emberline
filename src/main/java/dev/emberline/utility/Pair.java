package dev.emberline.utility;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * A generic Pair class.
 *
 * @param <A> the type of the first value in the pair
 * @param <B> the type of the second value in the pair
 */
public class Pair<A, B> implements Serializable {

    @Serial
    private static final long serialVersionUID = 56467219245623699L;
    private A x;
    private B y;

    /**
     * Constructs a new Pair with the specified values.
     *
     * @param x the first value of the pair
     * @param y the second value of the pair
     */
    public Pair(final A x, final B y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retrieves the first value of the pair.
     *
     * @return the first value of the pair
     */
    public A getX() {
        return x;
    }

    /**
     * Retrieves the second value of the pair.
     *
     * @return the second value of the pair
     */
    public B getY() {
        return y;
    }

    /**
     * Sets the first value of the pair.
     *
     * @param x the new value to set as the first value of the pair
     */
    public void setX(final A x) {
        this.x = x;
    }

    /**
     * Sets the second value of the pair.
     *
     * @param y the new value to set as the second value of the pair
     */
    public void setY(final B y) {
        this.y = y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair other = (Pair) obj;
        return Objects.equals(x, other.x) && Objects.equals(y, other.y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Pair [x=" + x + ", y=" + y + "]";
    }
}
