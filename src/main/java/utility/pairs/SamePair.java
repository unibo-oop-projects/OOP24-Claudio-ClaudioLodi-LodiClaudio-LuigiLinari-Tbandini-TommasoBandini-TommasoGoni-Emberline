package utility.pairs;

public class SamePair<A> extends Pair<A,A> {

    public SamePair(A x, A y) {
        super(x, y);
    }
    
    public void swap() {
        A tmp = this.X();
        setX(this.Y());
        setY(tmp);
    }
}
