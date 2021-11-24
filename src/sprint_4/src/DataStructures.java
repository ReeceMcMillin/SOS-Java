package sprint_4.src;

/**
 * DataStructures is the parent class for helpful internal data structures.
 */
public class DataStructures {
    /**
     * Triplet contains a triplet of Pairs, meant to represent a 3-tuple of Tiles on the board.
     * @see Pair
     */
    public static class Triplet implements Comparable<Triplet> {
        public Pair first;
        public Pair second;
        public Pair third;

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

    /**
     * Holds a coordinate pair.
     * @see Triplet
     */
    public static class Pair implements Comparable<Pair> {
        public int first;
        public int second;

        public String toString() {
            return String.format("(%s, %s)", this.first, this.second);
        }

        @Override
        public int compareTo(Pair other) {
            if (this.first == other.first && this.second == other.second) {
                return 0;
            } else {
                return -1;
            }
        }

        public Pair(int first, int second) {
            this.first = first;
            this.second = second;
        }
    }
}
