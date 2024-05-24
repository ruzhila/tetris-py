import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Tetris extends JPanel implements ActionListener {
    private static final int SCREEN_WIDTH = 200;
    private static final int SCREEN_HEIGHT = 400;
    private static final int UNIT_SIZE = 20;
    private static final int[][][] shapes = {
            {
                    { 1, 1, 1, 1 }
            },
            {
                    { 1, 1, 1 },
                    { 0, 1, 0 }
            },
            {
                    { 1, 1, 1 },
                    { 1, 0, 0 }
            },
            {
                    { 1, 0 },
                    { 1, 0 },
                    { 1, 1 },
            },
            {
                    { 1, 1, 0 },
                    { 0, 1, 1 }
            },
            {
                    { 1, 1 },
                    { 1, 1 }
            },
            {
                    { 1, 1, 1 },
                    { 0, 0, 1 }
            },
            {
                    { 0, 1, 0 },
                    { 0, 1, 1 },
                    { 0, 1, 0 },
            },
    };
    private Color[] colors = {
            Color.RED, Color.GREEN, Color.ORANGE, Color.CYAN, Color.MAGENTA
    };
    private int[][] board = new int[UNIT_SIZE][SCREEN_WIDTH / UNIT_SIZE];
    private int[][] shape;
    private int shapeX = 0;
    private int shapeY = 0;
    private int score = 0;
    private boolean gameOver = false;
    private Color shapeColor;
    private Timer timer;

    Tetris() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setVisible(true);
        setFocusable(true);
        newShape();
        timer = new Timer(500, this);
        timer.start();
        addKeyListener(new TetrisKeyAdapter());
    }

    void newShape() {
        shape = shapes[new Random().nextInt(shapes.length)];
        shapeX = 5;
        shapeY = 0;
        shapeColor = colors[new Random().nextInt(colors.length)];
    }

    void rotateShape() {
        int newShape[][] = new int[shape[0].length][shape.length];
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                newShape[j][shape.length - 1 - i] = shape[i][j];
            }
        }
        shape = newShape;
    }

    void moveShape(int dx, int dy) {
        if (canMove(dx, dy)) {
            shapeX += dx;
            shapeY += dy;
        }
    }

    boolean canMove(int dx, int dy) {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 1) {
                    continue;
                }
                int newX = shapeX + j + dx;
                int newY = shapeY + i + dy;
                if (newX < 0 || newX >= SCREEN_WIDTH / UNIT_SIZE ||
                        newY >= UNIT_SIZE ||
                        board[newY][newX] == 1) {
                    return false;
                }
            }
        }
        return true;
    }

    void checkLines() {
        for (int i = board.length - 1; i >= 0; i--) {
            boolean isLine = true;
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != 1) {
                    isLine = false;
                    break;
                }
            }
            if (isLine) {
                for (int k = i; k > 0; k--) {
                    for (int j = 0; j < board[k].length; j++) {
                        board[k][j] = board[k - 1][j];
                    }
                }
                i++;
                score += 100;
            }
        }
    }

    class TetrisKeyAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    moveShape(-1, 0);
                    break;
                case KeyEvent.VK_RIGHT:
                    moveShape(1, 0);
                    break;
                case KeyEvent.VK_DOWN:
                    moveShape(0, 1);
                    break;
                case KeyEvent.VK_UP:
                    rotateShape();
                    break;
            }
            repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            return;
        }
        if (canMove(0, 1)) {
            shapeY++;
        } else {
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[i].length; j++) {
                    if (shape[i][j] == 1) {
                        board[shapeY + i][shapeX + j] = 1;
                    }
                }
            }
            checkLines();
            newShape();

            if (!canMove(0, 0)) {
                gameOver = true;
                timer.stop();
            }
        }
        repaint();
    }

    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        for (int i = 0; i < UNIT_SIZE; i++) {
            for (int j = 0; j < SCREEN_WIDTH / UNIT_SIZE; j++) {
                if (board[i][j] == 1) {
                    g.setColor(Color.BLUE);
                    g.fillRect(j * UNIT_SIZE, i * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect(j * UNIT_SIZE, i * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                }
            }
        }

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    g.setColor(shapeColor);
                    g.fillRect((j + shapeX) * UNIT_SIZE, (i + shapeY) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect((j + shapeX) * UNIT_SIZE, (i + shapeY) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                }
            }
        }
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score + " by ruzhila.cn", 0, 10);
        if (gameOver) {
            g.setColor(Color.RED);
            g.drawString("Game Over", 50, 50);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        frame.add(new Tetris());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}