package dev.emberline.utility;

import java.util.Objects;

/*
 * This class is used to represent Integer 2D Points, in a simple way
 */
public class IntegerPoint2D {
    
    private Integer x;
    private Integer y;

    public IntegerPoint2D(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    @Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		IntegerPoint2D other = (IntegerPoint2D) obj;
		return Objects.equals(x, other.x) && Objects.equals(y, other.y);
	}

	@Override
	public String toString() {
		return "Tile [x=" + x + ", y=" + y + "]";
	}
}
