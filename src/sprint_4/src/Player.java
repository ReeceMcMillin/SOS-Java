package sprint_4.src;

import sprint_4.src.Tile.TileValue;

public class Player {
    public enum PlayStyle {
        Human, Computer
    }

    private Tile tile;
    private PlayStyle style;
    private final String name;
    private Integer points;

    public Tile getTile() {
        return this.tile;
    }

    public void setTile(TileValue tile) {
        this.tile = new Tile(tile);
    }

    public PlayStyle getStyle() {
        return this.style;
    }

    public void setStyle(PlayStyle style) {
        this.style = style;
    }

    public String getName() {
        return this.name;
    }

    public Integer getPoints() {
        return this.points;
    }

    public void incrementPoints() {
        this.points++;
    }

    public void resetPoints() {
        this.points = 0;
    }

    public String toString() {
        return String.format("%s (%s, %s)", this.name, this.tile, this.style);
    }

    public Player(TileValue value, String name) {
        this.name = name;
        this.tile = new Tile(value);
        this.style = PlayStyle.Human;
        this.points = 0;
    }
}
