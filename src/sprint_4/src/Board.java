package sprint_4.src;

import java.util.TreeSet;

import sprint_4.src.Tile.TileValue;
import sprint_4.src.DataStructures.*;

import java.util.Random;
import java.util.ArrayList;

public class Board {
    private static final Random RANDOM = new Random();

    public enum State {
        INIT, PLAYING, DRAW, PLAYER_ONE_WON, PLAYER_TWO_WON
    }

    public enum GameMode {
        Simple, General
    }

    private GameMode gameMode = GameMode.General;
    private Tile[][] grid;
    private TreeSet<Triplet> wins;

    public final Player playerOne = new Player(TileValue.S, "Player 1");
    public final Player playerTwo = new Player(TileValue.O, "Player 2");

    private Player turn = playerOne;

    public final int MIN_BOARD_SIZE = 3;
    public final int MAX_BOARD_SIZE = 9;
    private static int BOARD_SIZE = 3;

    private State gameState;

    public Board() {
        grid = new Tile[BOARD_SIZE][BOARD_SIZE];
        initBoard();
    }

    public Board(int boardSize) {
        grid = new Tile[BOARD_SIZE][BOARD_SIZE];
        initBoard();
        BOARD_SIZE = boardSize;
    }

    public void setGrid(Tile[][] grid) {
        this.grid = grid;
    }

    public void initBoard() {
        this.playerOne.resetPoints();
        this.playerTwo.resetPoints();
        gameState = State.INIT;
        turn = playerOne;
        this.wins = new TreeSet<>();

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                grid[row][col] = new Tile(TileValue.None);
            }
        }
    }

    public void setBoardSize(int boardSize) {
        BOARD_SIZE = boardSize;
    }

    public int getBoardSize() {
        return BOARD_SIZE;
    }

    public State getGameState() {
        return this.gameState;
    }

    public void setGameState(State state) {
        this.gameState = state;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(GameMode mode) {
        for (Tile[] row : grid) {
            for (Tile tile : row) {
                if (tile.getValue() != TileValue.None) {
                    return;
                }
            }
        }
        this.gameMode = mode;
    }

    public Tile getTile(int row, int column) {
        assert (row >= 0 && row < this.getBoardSize() &&
                column >= 0 && column < this.getBoardSize()) :
                String.format("getTile(%s, %s) out of bounds for board size %s.", row, column, this.getBoardSize());

        return grid[row][column];
    }

    public ArrayList<Pair> getEmptyTiles() {
        ArrayList<Pair> emptyTiles = new ArrayList<>();
        for (int i = 0; i < this.getBoardSize(); i++) {
            for (int j = 0; j < this.getBoardSize(); j++) {
                if (this.grid[i][j].getValue() == TileValue.None) {
                    emptyTiles.add(new Pair(i, j));
                }
            }
        }
        return emptyTiles;
    }

    public Player getTurn() {
        return turn;
    }

    public boolean boardHasWinner() {
        return this.getGameState() == State.PLAYER_ONE_WON || this.getGameState() == State.PLAYER_TWO_WON;
    }

    public void makeMove(int row, int column) {
        assert (0 <= row && row < this.getBoardSize() &&
                0 <= column && column < this.getBoardSize())
                : String.format("makeMove(%s, %s) out of bounds for board size %s.", row, column, this.getBoardSize());

        if (grid[row][column].getValue() == TileValue.None) {
            grid[row][column] = (turn == playerOne) ? playerOne.getTile() : playerTwo.getTile();
            updateGameState(turn);
            turn = (turn == playerOne) ? playerTwo : playerOne;
        }
        if (this.boardHasWinner()) return;

        if (turn.getStyle() == Player.PlayStyle.Computer
                && !isFull()) {
            makeComputerMove();
        }
    }

    public void makeComputerMove() {
        if (this.boardHasWinner()) return;

        ArrayList<Pair> emptyTiles = getEmptyTiles();
        Pair choice = emptyTiles.get(RANDOM.nextInt(emptyTiles.size()));
        Tile tile = new Tile(TileValue.values()[RANDOM.nextInt(2)]);

        grid[choice.first][choice.second] = tile;
        updateGameState(turn);

        if (emptyTiles.size() <= 1) return;

        turn = (turn == playerOne) ? playerTwo : playerOne;
        if (turn.getStyle() == Player.PlayStyle.Computer) {
            makeComputerMove();
        }
    }

    private boolean isFull() {
        for (int i = 0; i < this.getBoardSize(); i++) {
            for (int j = 0; j < this.getBoardSize(); j++) {
                if (this.grid[i][j].getValue() == Tile.TileValue.None) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateGameState(Player turn) {
        // This logic is pretty bad! It currently uses hasWonSimple to register wins for hasWonGeneral.
        // hasWonSimple should be specifically for checking if a simple game has been won.
        // This might be best accomplished by factoring out a new function to increment points, then using hasWonSimple to check point count every turn.
        switch (this.getGameMode()) {
            case Simple:
                if (hasWonSimple(turn) == null) {
                    this.setGameState(this.isFull() ? State.DRAW : State.PLAYING);
                } else {
                    gameState = (turn == playerOne) ? State.PLAYER_ONE_WON : State.PLAYER_TWO_WON;
                    return;
                }
                break;
            case General: {
                if (this.isFull()) {
                    if (hasWonGeneral()) {
                        gameState = (playerOne.getPoints() > playerTwo.getPoints()) ? State.PLAYER_ONE_WON : State.PLAYER_TWO_WON;
                        return;
                    } else if (hasDrawGeneral()) {
                        gameState = State.DRAW;
                    }
                }
            }

            Triplet win = hasWonSimple(turn);
            if (win != null) {
                if (!this.wins.contains(win)) {
                    turn.incrementPoints();
                }
            } else if (isFull()) {
                gameState = State.DRAW;
            } else {
                gameState = State.PLAYING;
            }
        }
    }

    private Triplet hasWonSimple(Player turn) {
        // TODO: less awful win checking

        // Horizontal Win
        // TODO refactor as iterators
        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize() - 2; j++) {
                if (this.grid[i][j].getValue() == TileValue.S && this.grid[i][j + 1].getValue() == TileValue.O
                        && this.grid[i][j + 2].getValue() == TileValue.S) {
                    Triplet win = new Triplet(new Pair(i, j), new Pair(i, j + 1), new Pair(i, j + 2));
                    if (!this.wins.contains(win)) {
                        this.wins.add(win);
                        turn.incrementPoints();
                        return win;
                    }
                }
            }
        }

        // Vertical Win
        // TODO refactor as iterators
        for (int i = 0; i < getBoardSize() - 2; i++) {
            for (int j = 0; j < getBoardSize(); j++) {
                if (this.grid[i][j].getValue() == TileValue.S && this.grid[i + 1][j].getValue() == TileValue.O
                        && this.grid[i + 2][j].getValue() == TileValue.S) {
                    Triplet win = new Triplet(new Pair(i, j), new Pair(i + 1, j), new Pair(i + 2, j));
                    if (!this.wins.contains(win)) {
                        this.wins.add(win);
                        turn.incrementPoints();
                        return win;
                    }
                }
            }
        }

        // Diagonal Win
        // TODO refactor as iterators
        for (int i = 0; i < getBoardSize() - 2; i++) {
            for (int j = 0; j < getBoardSize() - 2; j++) {
                if (this.grid[i][j].getValue() == TileValue.S && this.grid[i + 1][j + 1].getValue() == TileValue.O
                        && this.grid[i + 2][j + 2].getValue() == TileValue.S) {
                    Triplet win = new Triplet(new Pair(i, j), new Pair(i + 1, j + 1), new Pair(i + 2, j + 2));
                    if (!this.wins.contains(win)) {
                        this.wins.add(win);
                        turn.incrementPoints();
                        return win;
                    }
                }
            }
        }
        // Backwards Diagonal Win
        // TODO refactor as iterators
        for (int i = 0; i < getBoardSize() - 2; i++) {
            for (int j = 2; j < getBoardSize(); j++) {
                if (this.grid[i][j].getValue() == TileValue.S && this.grid[i + 1][j - 1].getValue() == TileValue.O
                        && this.grid[i + 2][j - 2].getValue() == TileValue.S) {
                    Triplet win = new Triplet(new Pair(i, j), new Pair(i + 1, j - 1), new Pair(i + 2, j - 2));
//                    System.out.println(String.format("%s", win));
                    if (!this.wins.contains(win)) {
                        this.wins.add(win);
                        turn.incrementPoints();
                        return win;
                    }
                }
            }
        }
        return null;
    }

    private boolean hasWonGeneral() {
        return !playerOne.getPoints().equals(playerTwo.getPoints());
    }

    private boolean hasDrawGeneral() {
        return playerOne.getPoints().equals(playerTwo.getPoints());
    }
}
