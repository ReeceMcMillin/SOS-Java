package sprint_4.src;
import sprint_4.src.Pair;

/**
 * Triplet contains a triplet of Pairs, meant to represent a 3-tuple of Tiles on the board.
 * @see Pair
 */
public class Triplet implements Comparable<Triplet> {
    public final Pair first;
    public final Pair second;
    public final Pair third;

    public String toString() {
        return String.format("[%s, %s, %s]", this.first, this.second, this.third);
    }

    @Override
    public int compareTo(Triplet other) {
        if (this.first.compareTo(other.first) == 0 && this.second.compareTo(other.second) == 0 && this.third.compareTo(other.third) == 0) {
            return 0;
        } else {
            return -1;
        }
    }

    public Triplet(Pair first, Pair second, Pair third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}