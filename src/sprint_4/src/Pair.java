package sprint_4.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Holds a coordinate pair.
 * @see Triplet
 */
public class Pair implements Comparable<Pair> {
    public final int first;
    public final int second;
    public final Tile.TileValue value;

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
        this.value = Tile.TileValue.None;
    }

    public Pair (String s) {
        ArrayList<String> split = new ArrayList<>(Arrays.asList(s.split(",")));
        this.first = Integer.parseInt(split.get(0));
        this.second = Integer.parseInt(split.get(1));
        if (Objects.equals(split.get(2), "S")) {
            this.value = Tile.TileValue.S;
        } else {
            this.value = Tile.TileValue.O;
        }
    }
}
