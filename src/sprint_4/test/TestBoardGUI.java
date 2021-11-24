package sprint_4.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sprint_4.src.Board;
import sprint_4.src.GUI;
import sprint_4.src.Tile;

public class TestBoardGUI {
    private Board board;
    private final int sleepTime = 0;

    @Before
    public void setUp() {
        board = new Board(9);
    }

    @After
    public void tearDown() {}

    //AC1.1: Change game mode with fully initialized game state
    @Test
    public void testChangeGameModeBeforeGame() {
        board = new Board(9);
        GUI gui = new GUI(board);
        board.setGameMode(Board.GameMode.General);
        assert(gui.getBoard().getGameMode() == Board.GameMode.General);

        board.setGameMode(Board.GameMode.Simple);
        assert(gui.getBoard().getGameMode() == Board.GameMode.Simple);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //AC1.2: Change game mode when tiles placed on board
    @Test
    public void testChangeGameModeDuringGame() {
        board = new Board(9);
        GUI gui = new GUI(board);
        board.setGameMode(Board.GameMode.General);
        assert(gui.getBoard().getGameMode() == Board.GameMode.General);

        board.makeMove(1,1);

        board.setGameMode(Board.GameMode.Simple);
        //Game mode should not have updated
        assert(gui.getBoard().getGameMode() == Board.GameMode.General);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //AC2.1: New game button clears data
    @Test
    public void testNewGame() {
        board = new Board(9);
        GUI gui = new GUI(board);
        board.makeMove(1, 2);

        //Test that move was made correctly
        assert(gui.getBoard().getTile(1, 2).getValue() == Tile.TileValue.S);
        gui.getBoard().initBoard();

        // Test that ALL tiles are registered as Tile.TileValue.None
        for (int row = 0; row < board.getTotalRows(); row++) {
            for (int col = 0; col < board.getTotalColumns(); col++) {
                assert(gui.getBoard().getTile(row, col).getValue() == Tile.TileValue.None);
            }
        }

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //AC3.1: Board is visible
    @Test
    public void testEmptyBoard() {
        board = new Board(9);
        GUI gui = new GUI(board);

        assert(gui.getBoard() != null);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //AC4.1: Player can select tile during game
    @Test
    public void testChangeTileSelection() {
        board = new Board(9);
        GUI gui = new GUI(board);

        // Player 1 begins by default with tile S, this ensures O is selected before a move is made.
        gui.getBoard().playerOne.setTile(Tile.TileValue.O);
        board.makeMove(0, 0);
        assert(gui.getBoard().getTile(0, 0).getValue() == Tile.TileValue.O);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //AC5.1: Player cannot place tile over existing tile
    @Test
    public void testPlaceTileOverFilled() {
        board = new Board(9);
        GUI gui = new GUI(board);

        // Player 1 should place an S here.
        board.makeMove(1, 1);
        // Player 2 should not be able to place their tile here, leaving it an S and not causing an error.
        board.makeMove(1, 1);

        assert(gui.getBoard().getTile(1, 1).getValue() == Tile.TileValue.S);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //AC5.2: Player can place tile over empty space
    @Test
    public void testPlaceTileOverEmpty() {
        board = new Board(9);
        GUI gui = new GUI(board);

        board.makeMove(0, 0);
        board.makeMove(1, 1);
        board.makeMove(8, 8);

        assert(gui.getBoard().getTile(0, 0).getValue() == Tile.TileValue.S);
        assert(gui.getBoard().getTile(1, 1).getValue() == Tile.TileValue.O);
        assert(gui.getBoard().getTile(8, 8).getValue() == Tile.TileValue.S);


        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // User Stories 6 and 7 (and associated Acceptance Criteria) were not supposed to be implemented this sprint.
    // AC6.1: Simple game, winner
    @Test
    public void testSimpleGameWinner() {
        board = new Board(3);
        board.setGameMode(Board.GameMode.Simple);
        GUI gui = new GUI(board);
        board.makeMove(0, 0);
        board.makeMove(0, 1);
        board.makeMove(0, 2);

        assert(gui.getBoard().getGameMode() == Board.GameMode.Simple);
        assert(gui.getBoard().getGameState() == Board.State.PLAYER_ONE_WON);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // AC6.2: Simple game, no winner
    @Test
    public void testSimpleGameNoWinner() {
        board = new Board(3);
        board.setBoardSize(3);
        GUI gui = new GUI(board);
        board.playerTwo.setTile(Tile.TileValue.S);
        board.setGameMode(Board.GameMode.Simple);

        assert(gui.getBoard().getGameMode() == Board.GameMode.Simple);

        for (int i = 0; i < board.getBoardSize(); i++) {
            for (int j = 0; j < board.getBoardSize(); j++) {
                board.makeMove(i, j);
            }
        }

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert(gui.getBoard().getGameState() == Board.State.DRAW);


    }

    // AC7.1: General game, winner
    @Test
    public void testGeneralGameWinner() {
        board = new Board(9);
        board.setGameMode(Board.GameMode.General);
        GUI gui = new GUI(board);
        for (int i = 0; i < board.getBoardSize(); i++) {
            for (int j = 0; j < board.getBoardSize(); j++) {
                board.makeMove(i, j);
            }
        }

        assert(gui.getBoard().getGameMode() == Board.GameMode.General);
        assert(gui.getBoard().getGameState() == Board.State.PLAYER_ONE_WON);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    // AC7.2: General game, no winner
    @Test
    public void testGeneralGameNoWinner() {
        board = new Board(9);
        board.playerTwo.setTile(Tile.TileValue.S);
        GUI gui = new GUI(board);
        for (int i = 0; i < board.getBoardSize(); i++) {
            for (int j = 0; j < board.getBoardSize(); j++) {
                board.makeMove(i, j);
            }
        }

        assert(gui.getBoard().getGameMode() == Board.GameMode.General);
        assert(gui.getBoard().getGameState() == Board.State.DRAW);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
