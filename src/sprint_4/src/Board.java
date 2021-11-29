package sprint_4.src;

import java.util.TreeSet;

import sprint_4.src.Tile.TileValue;
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
    private int boardSize = MIN_BOARD_SIZE;

    private State gameState;

    public Board() {
        grid = new Tile[MIN_BOARD_SIZE][MIN_BOARD_SIZE];
        initBoard();
    }

    public Board(int boardSize) {
        setBoardSize(boardSize);
        grid = new Tile[getBoardSize()][getBoardSize()];
        initBoard();
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

        for (int row = 0; row < getBoardSize(); row++) {
            for (int col = 0; col < getBoardSize(); col++) {
                grid[row][col] = new Tile(TileValue.None);
            }
        }
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public int getBoardSize() {
        return this.boardSize;
    }

    public void setGameState(State state) {
        this.gameState = state;
    }

    public State getGameState() {
        return this.gameState;
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

    public GameMode getGameMode() {
        return this.gameMode;
    }

    /**
     * @param row The row index.
     * @param column The column index.
     * @return The tile at the given (row, column) index.
     */
    public Tile getTile(int row, int column) {
        assert (row >= 0 && row < this.getBoardSize() &&
                column >= 0 && column < this.getBoardSize()) :
                String.format("getTile(%s, %s) out of bounds for board size %s.", row, column, this.getBoardSize());

        return grid[row][column];
    }

    /**
     * @return ArrayList<Pair> of all empty tile locations on board.
     */
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

    /**
     * @return The board's active player.
     */
    public Player getTurn() {
        return turn;
    }

    public boolean boardHasWinner() {
        return this.getGameState() == State.PLAYER_ONE_WON || this.getGameState() == State.PLAYER_TWO_WON;
    }

    /**
     * Places a tile from the active player at the given (row, column) index.
     * @param row The row index of the tile to be placed.
     * @param column The column index of the tile to be placed.
     */
    public void makeMove(int row, int column) {
        assert (0 <= row && row < this.getBoardSize() &&
                0 <= column && column < this.getBoardSize())
                : String.format("makeMove(%s, %s) out of bounds for board size %s.", row, column, this.getBoardSize());

        if (grid[row][column].getValue() == TileValue.None) {
            grid[row][column] = (this.turn == playerOne) ? playerOne.getTile() : playerTwo.getTile();
            updateGameState();
            this.turn = (this.turn == playerOne) ? playerTwo : playerOne;
        }
        if (this.boardHasWinner()) return;

        if (turn.getStyle() == Player.PlayStyle.Computer
                && !isFull()) {
            makeComputerMove();
        }
    }

    /**
     * Places a tile in a random location on behalf of an active player who has selected the Computer play style.
     * @see sprint_4.src.Player.PlayStyle
     */
    public void makeComputerMove() {
        if (this.boardHasWinner()) return;

        ArrayList<Pair> emptyTiles = getEmptyTiles();
        Pair choice = emptyTiles.get(RANDOM.nextInt(emptyTiles.size()));
        Tile tile = new Tile(TileValue.values()[RANDOM.nextInt(2)]);

        grid[choice.first][choice.second] = tile;
        updateGameState();

        if (emptyTiles.size() <= 1) return;

        turn = (turn == playerOne) ? playerTwo : playerOne;
        if (turn.getStyle() == Player.PlayStyle.Computer) {
            makeComputerMove();
        }
    }

    /**
     * @return True if the board is full, false otherwise.
     */
    public boolean isFull() {
        for (int i = 0; i < this.getBoardSize(); i++) {
            for (int j = 0; j < this.getBoardSize(); j++) {
                if (this.grid[i][j].getValue() == Tile.TileValue.None) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Given the selected game mode, register any newly completed wins update the game state accordingly.
     */
    private void updateGameState() {
        registerWin();
        switch (this.getGameMode()) {
            case Simple:
                if (hasWonSimple()) {
                    this.setGameState(this.turn == playerOne ? State.PLAYER_ONE_WON : State.PLAYER_TWO_WON);
                } else {
                    this.setGameState(this.isFull() ? State.DRAW : State.PLAYING);
                }
                break;
            case General: {
                if (this.isFull()) {
                    if (hasWonGeneral()) {
                        this.setGameState(playerOne.getPoints() > playerTwo.getPoints() ? State.PLAYER_ONE_WON : State.PLAYER_TWO_WON);
                    } else if (hasDrawGeneral()) {
                        this.setGameState(State.DRAW);
                    }
                }
            }
        }
    }

    private void addWin(Triplet win) {
        if (!this.wins.contains(win)) {
            this.wins.add(win);
            turn.incrementPoints();
        }
    }

    private boolean checkHorizontalWin(int i, int j) {
        return this.grid[i][j].getValue() == TileValue.S && this.grid[i][j + 1].getValue() == TileValue.O
                && this.grid[i][j + 2].getValue() == TileValue.S;
    }

    private boolean checkDiagonalWin(int i, int j) {
        return this.grid[i][j].getValue() == TileValue.S && this.grid[i + 1][j + 1].getValue() == TileValue.O
                && this.grid[i + 2][j + 2].getValue() == TileValue.S;
    }

    private boolean checkBackwardsDiagonalWin(int i, int j) {
        return this.grid[i][j].getValue() == TileValue.S && this.grid[i + 1][j - 1].getValue() == TileValue.O
                && this.grid[i + 2][j - 2].getValue() == TileValue.S;
    }

    private boolean checkVerticalWin(int i, int j) {
        return this.grid[i][j].getValue() == TileValue.S && this.grid[i + 1][j].getValue() == TileValue.O
                && this.grid[i + 2][j].getValue() == TileValue.S;
    }

    private void registerWin() {
        for (int row = 0; row < getBoardSize(); row++) {
            for (int col = 0; col < getBoardSize(); col++) {
                if (col >= 2 && row < getBoardSize() - 2) {
                    // Check backward diagonal win
                    if (checkBackwardsDiagonalWin(row, col)) {
                        Triplet win = new Triplet(new Pair(row, col), new Pair(row + 1, col - 1), new Pair(row + 2, col - 2));
                        addWin(win);
                    }
                } if (col < getBoardSize() - 2 && row < getBoardSize() - 2) {
                    // Check forward diagonal win
                    if (checkDiagonalWin(row, col)) {
                        Triplet win = new Triplet(new Pair(row, col), new Pair(row + 1, col + 1), new Pair(row + 2, col + 2));
                        addWin(win);
                    }
                } if (col < getBoardSize() - 2) {
                    // Check horizontal win
                    if (checkHorizontalWin(row, col)) {
                        Triplet win = new Triplet(new Pair(row, col), new Pair(row, col + 1), new Pair(row, col + 2));
                        addWin(win);
                    }
                } if (row < getBoardSize() - 2) {
                    // Check vertical win
                    if (checkVerticalWin(row, col)) {
                        Triplet win = new Triplet(new Pair(row, col), new Pair(row + 1, col), new Pair(row + 2, col));
                        addWin(win);
                    }
                }
            }
        }
    }

    private boolean hasWonGeneral() {
        return !playerOne.getPoints().equals(playerTwo.getPoints());
    }

    private boolean hasDrawGeneral() {
        return playerOne.getPoints().equals(playerTwo.getPoints());
    }

    private boolean hasWonSimple() {
        return playerOne.getPoints() == 1 || playerTwo.getPoints() == 1;
    }
}
