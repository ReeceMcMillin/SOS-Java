package sprint_4.src;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI extends JFrame {

    private static final int CELL_SIZE = 100;
    private static final int GRID_WIDTH = 8;
    private static final int GRID_WIDTH_HALF = GRID_WIDTH / 2;

    private static final int CELL_PADDING = CELL_SIZE / 6;
    private static final int SYMBOL_STROKE_WIDTH = 2;

    private int CANVAS_WIDTH;
    private int CANVAS_HEIGHT;

    private GameBoardCanvas gameBoardCanvas;
    private JLabel gameStatusBar;

    private final Board board;

    public GUI(Board board) {
        this.board = board;
        setContentPane();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setTitle("SOS");
        setVisible(true);
    }

    public Board getBoard() {
        return board;
    }

    private void setContentPane() {
        this.gameBoardCanvas = new GameBoardCanvas();
        CANVAS_WIDTH = CELL_SIZE * board.getBoardSize();
        CANVAS_HEIGHT = CELL_SIZE * board.getBoardSize();
        gameBoardCanvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        gameStatusBar = new JLabel("");
        gameStatusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        gameStatusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

        JPanel topMenu = GenerateGameMenu(this.board);
        JPanel leftMenu = GenerateTileMenu(this.board.playerOne);
        JPanel rightMenu = GenerateTileMenu(this.board.playerTwo);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.removeAll();
        contentPane.add(topMenu, BorderLayout.PAGE_START);
        contentPane.add(leftMenu, BorderLayout.WEST);
        contentPane.add(rightMenu, BorderLayout.EAST);
        contentPane.add(gameBoardCanvas, BorderLayout.CENTER);
        contentPane.add(gameStatusBar, BorderLayout.AFTER_LAST_LINE);
    }

    public JPanel GenerateGameMenu(Board board) {
        JPanel menu = new JPanel();
        menu.setPreferredSize(new Dimension(CANVAS_WIDTH + 200, 100));

        JPanel boardSizeMenu = new JPanel();
        boardSizeMenu.setPreferredSize(new Dimension(CANVAS_WIDTH + 200, 25));

        ButtonGroup gameModeSelection = new ButtonGroup();
        menu.setLayout(new FlowLayout());
        JRadioButton simpleGame = new JRadioButton("Simple Game");
        JRadioButton generalGame = new JRadioButton("General Game");
        JButton newGame = new JButton("New Game");
        JButton incButton = new JButton("+");
        JButton decButton = new JButton("-");

        newGame.addActionListener(e -> {
            board.playerOne.resetPoints();
            board.playerTwo.resetPoints();
            gameStatusBar.setText("");
            board.initBoard();
            gameStatusBar.setText("");
            gameBoardCanvas.repaint();
        });

        simpleGame.setSelected(this.board.getGameMode() == Board.GameMode.Simple);
        generalGame.setSelected(this.board.getGameMode() == Board.GameMode.General);

        simpleGame.addActionListener(e -> board.setGameMode(Board.GameMode.Simple));
        generalGame.addActionListener(e -> board.setGameMode(Board.GameMode.General));

        incButton.addActionListener(e -> {
            if (board.getBoardSize() >= board.MAX_BOARD_SIZE) {
                return;
            }
            board.setGrid(new Tile[board.getBoardSize() + 1][board.getBoardSize() + 1]);
            board.setBoardSize(board.getBoardSize() + 1);
            board.initBoard();
            setContentPane();
            pack();
            gameBoardCanvas.repaint();
            gameBoardCanvas.printStatusBar();
        });

        decButton.addActionListener(e -> {
            if (board.getBoardSize() <= board.MIN_BOARD_SIZE) {
                return;
            }
            board.setGrid(new Tile[board.getBoardSize() - 1][board.getBoardSize() - 1]);
            board.setBoardSize(board.getBoardSize() - 1);
            board.initBoard();
            setContentPane();
            pack();
            gameBoardCanvas.repaint();
            gameBoardCanvas.printStatusBar();
        });

        menu.setLayout(new BorderLayout());
        menu.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 100));

        boardSizeMenu.setLayout(new BorderLayout());
        boardSizeMenu.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 100));

        boardSizeMenu.add(decButton, BorderLayout.WEST);
        boardSizeMenu.add(incButton, BorderLayout.EAST);
        gameModeSelection.add(simpleGame);
        gameModeSelection.add(generalGame);
        menu.add(boardSizeMenu, BorderLayout.PAGE_START);
        menu.add(simpleGame, BorderLayout.WEST);
        menu.add(generalGame, BorderLayout.EAST);
        menu.add(newGame, BorderLayout.SOUTH);
        return menu;
    }

    public JPanel GenerateTileMenu(Player player) {
        JPanel menu = new JPanel();
        ButtonGroup tileSelection = new ButtonGroup();
        ButtonGroup styleSelection = new ButtonGroup();
        menu.setLayout(new FlowLayout());
        menu.add(new Label(player.getName()));
        JRadioButton s = new JRadioButton("S");
        JRadioButton o = new JRadioButton("O");
        JRadioButton human = new JRadioButton("Human");
        JRadioButton comp = new JRadioButton("Computer");

        s.setSelected(player.getTile().getValue() == Tile.TileValue.S);
        o.setSelected(player.getTile().getValue() == Tile.TileValue.O);
        human.setSelected(player.getStyle() == Player.PlayStyle.Human);
        comp.setSelected(player.getStyle() == Player.PlayStyle.Computer);

        s.addActionListener(e -> player.setTile(Tile.TileValue.S));
        o.addActionListener(e -> player.setTile(Tile.TileValue.O));
        human.addActionListener(e -> player.setStyle(Player.PlayStyle.Human));
        comp.addActionListener(e -> {
            player.setStyle(Player.PlayStyle.Computer);
            if (board.getTurn() == player) {
                board.makeComputerMove();
                repaint();
            }
        });

        tileSelection.add(s);
        tileSelection.add(o);
        styleSelection.add(human);
        styleSelection.add(comp);
        menu.add(s);
        menu.add(o);
        menu.add(human);
        menu.add(comp);
        menu.setPreferredSize(new Dimension(100, CANVAS_HEIGHT+15));
        return menu;
    }

    class GameBoardCanvas extends JPanel {
        GameBoardCanvas() {
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (board.getGameState() == Board.State.PLAYING || board.getGameState() == Board.State.INIT) {
                        int rowSelected = e.getY() / CELL_SIZE;
                        int colSelected = e.getX() / CELL_SIZE;
                        board.makeMove(rowSelected, colSelected);
                    } else {
                        gameStatusBar.setText("");
                        board.initBoard();
                    }
                    repaint();
                }
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.WHITE);
            drawGridLines(g);
            drawBoard(g);
            printStatusBar();
        }

        private void drawGridLines(Graphics g) {
            g.setColor(Color.LIGHT_GRAY);

            for (int row = 1; row < board.getBoardSize(); row++) {
                g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDTH_HALF,
                        CANVAS_WIDTH-1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);

                g.fillRoundRect(CELL_SIZE * row - GRID_WIDTH_HALF, 0,
                        GRID_WIDTH, CANVAS_HEIGHT-1, GRID_WIDTH, GRID_WIDTH);
            }
        }

        private void drawBoard(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setFont(new Font("Helvetica", Font.PLAIN, GRID_WIDTH * 12));

            for (int row = 0; row < board.getBoardSize(); row++) {
                for (int col = 0; col < board.getBoardSize(); col++) {
                    int x1 = col * CELL_SIZE + CELL_PADDING;
                    int y1 = row * CELL_SIZE + CELL_PADDING;
                    if (board.getTile(row, col).getValue() == Tile.TileValue.S) {
                        g2d.setColor(Color.RED);
                        g2d.drawString("S", x1 + 5, y1 + 70);
                    } else if (board.getTile(row, col).getValue() == Tile.TileValue.O) {
                        g2d.setColor(Color.BLUE);
                        g2d.drawString("O", x1 - 2, y1 + 70);
                    }
                }
            }
        }

        private void printStatusBar() {
            switch (board.getGameState()) {
                case PLAYING:
                case INIT: {
                    gameStatusBar.setForeground(Color.BLACK);
                    gameStatusBar.setText(String.format("%s's Turn (%s points)", board.getTurn().getName(), board.getTurn().getPoints()));
                    break;
                }
                case DRAW: {
                    gameStatusBar.setForeground(Color.MAGENTA);
                    gameStatusBar.setText("It's a draw! Click to play again.");
                    break;
                }
                case PLAYER_ONE_WON: {
                    gameStatusBar.setForeground(Color.RED);
                    gameStatusBar.setText(String.format("%s wins! Click to play again.", board.playerOne.getName()));
                    break;
                }
                case PLAYER_TWO_WON: {
                    gameStatusBar.setForeground(Color.BLUE);
                    gameStatusBar.setText(String.format("%s wins! Click to play again.", board.playerTwo.getName()));
                    break;
                }
                default:
                    break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI(new Board()));
    }
}
