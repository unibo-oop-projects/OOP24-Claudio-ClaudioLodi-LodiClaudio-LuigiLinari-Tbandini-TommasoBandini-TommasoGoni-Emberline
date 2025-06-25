package dev.emberline.utility;

import java.io.Serializable;
import java.util.Objects;

/*
 * Generic pair class
 */
public class Pair<A, B> implements Serializable {

    private A x;
    private B y;

    public Pair(final A x, final B y) {
        this.x = x;
        this.y = y;
    }

    public A getX() {
        return x;
    }

    public B getY() {
        return y;
    }

    public void setX(final A x) {
        this.x = x;
    }

    public void setY(final B y) {
        this.y = y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

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

    @Override
    public String toString() {
        return "Pair [x=" + x + ", y=" + y + "]";
    }
}
