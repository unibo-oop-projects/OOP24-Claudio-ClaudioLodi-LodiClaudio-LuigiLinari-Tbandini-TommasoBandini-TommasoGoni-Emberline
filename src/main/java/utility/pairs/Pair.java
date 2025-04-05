package utility.pairs;

public class Pair<A,B> {

    private A x;
    private B y;

    public Pair(A x, B y) {
        this.x = x;
        this.y = y;
    }

    public A X() {
        return x;
    }
    
    public B Y() {
        return y;
    }

    protected void setX(A x) {
        this.x = x;
    }

    protected void setY(B y) {
        this.y = y;
    }
    
}
