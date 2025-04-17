package utility.pairs;

import java.util.Objects;

public class Pair<A,B> {

    private A x;
    private B y;

    public Pair(A x, B y) {
        this.x = x;
        this.y = y;
    }

    public A getX() {
        return x;
    }
    
    public B getY() {
        return y;
    }
    
    public void setX(A x) {
        this.x = x;
    }

    public void setY(B y) {
        this.y = y;
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
		Pair other = (Pair) obj;
		return Objects.equals(x, other.x) && Objects.equals(y, other.y);
	}

	@Override
	public String toString() {
		return "Pair [x=" + x + ", y=" + y + "]";
	}
}
