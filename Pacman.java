import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class Pacman {
    public static void main(String[] args) {

        int rowcount = 21;
        int columncount = 19;
        int titlesize = 32;
        int Boardwidth = columncount * titlesize;
        int Boardheigth = rowcount * titlesize;

        JFrame frame = new JFrame("PAC MAN");
        Gamepanel gamepanel = new Gamepanel();

        gamepanel.setPreferredSize(new Dimension(Boardwidth, Boardheigth));
        gamepanel.setBackground(Color.BLACK);

        frame.add(gamepanel);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Ensure the panel gets focus after the frame is shown
        SwingUtilities.invokeLater(() -> gamepanel.requestFocusInWindow());
    }
}

class Gamepanel extends JPanel implements ActionListener, KeyListener {

    // Loading images---
    Image pacup, pacdown, pacright, pacleft;
    Image blueghost, redghost, scaredghost, pinkghost, orangeghost;
    Image cherry, cherry2, powerfood, wall;

    // Game-Constraints--
    int rowcount = 21;
    int columncount = 19;
    int titlesize = 32;

    // Pacman positions and movement --
    int pacmanX, pacmanY;
    int pacmanDirection = 3; // 0=up, 1=down, 2=left, 3=right
    int nextDirection = 3;
    int pacmanSpeed = 4;

    // Ghost speed - increased from 3 to 4
    int ghostSpeed = 4;

    // Ghosts positions and movement
    int[] ghostX = new int[4];
    int[] ghostY = new int[4];
    int[] ghostDirection = new int[4];

    // Game state
    int score = 0;
    int highScore = 0;
    int lives = 3;
    int dotsEaten = 0;
    int totalDots = 0;
    boolean gameRunning = true;
    boolean gamePaused = false;
    boolean powerMode = false;
    int powerTimer = 0;

    javax.swing.Timer gameTimer;
    Random random = new Random();

    int[][] maze = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,0,1,1,1,0,1,1,1,0,1,1,0,1,0,1},
            {1,0,1,1,0,1,1,1,0,1,1,1,0,1,1,0,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,0,1},
            {1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,1},
            {1,1,1,1,0,1,1,1,0,1,0,1,1,1,0,1,1,1,1},
            {0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0},
            {1,1,1,1,0,1,0,1,1,0,1,1,0,1,0,1,1,1,1},
            {0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0},
            {1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1},
            {0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0},
            {1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,0,1,1,1,0,1,1,1,0,1,1,0,1,0,1},
            {1,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,1},
            {1,1,0,1,0,1,0,1,1,1,1,1,0,1,0,1,0,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    Gamepanel() {
        loadimages();
        initializeGame();

        gameTimer = new javax.swing.Timer(20, this);
        gameTimer.start();

        setFocusable(true);
        addKeyListener(this);
        // Request focus after the panel is added to the frame
        SwingUtilities.invokeLater(() -> requestFocusInWindow());

        // Add a mouse listener to click and gain focus (optional)
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });
    }

    void initializeGame() {
        totalDots = 0;
        for (int row = 0; row < rowcount; row++) {
            for (int col = 0; col < columncount; col++) {
                if (maze[row][col] == 0) {
                    totalDots++;
                }
            }
        }

        // Starting position at (9,16) which is a path
        pacmanX = 9 * titlesize;
        pacmanY = 16 * titlesize;
        pacmanDirection = 3;
        nextDirection = 3;

        ghostX[0] = 9 * titlesize; // Red
        ghostY[0] = 8 * titlesize;
        ghostX[1] = 8 * titlesize; // Blue
        ghostY[1] = 8 * titlesize;
        ghostX[2] = 10 * titlesize; // Pink
        ghostY[2] = 8 * titlesize;
        ghostX[3] = 10 * titlesize; // Orange
        ghostY[3] = 7 * titlesize;

        for (int i = 0; i < 4; i++) {
            ghostDirection[i] = random.nextInt(4);
        }

        score = 0;
        dotsEaten = 0;
        lives = 3;
        gameRunning = true;
        powerMode = false;
        powerTimer = 0;

        System.out.println("Game started. Pacman at: " + pacmanX/titlesize + "," + pacmanY/titlesize);
    }

    void loadimages() {
        try {
            pacright = new ImageIcon("pacmanRight.png").getImage();
            pacup = new ImageIcon("pacmanUp.png").getImage();
            pacdown = new ImageIcon("pacmanDown.png").getImage();
            pacleft = new ImageIcon("pacmanLeft.png").getImage();

            blueghost = new ImageIcon("blueGhost.png").getImage();
            redghost = new ImageIcon("redGhost.png").getImage();
            scaredghost = new ImageIcon("scaredGhost.png").getImage();
            pinkghost = new ImageIcon("pinkGhost.png").getImage();
            orangeghost = new ImageIcon("orangeGhost.png").getImage();

            cherry = new ImageIcon("cherry.png").getImage();
            cherry2 = new ImageIcon("cherry2.png").getImage();
            powerfood = new ImageIcon("powerFood.png").getImage();
            wall = new ImageIcon("wall.png").getImage();
        } catch (Exception e) {
            System.out.println("Image loading error: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw enhanced background
        drawEnhancedBackground(g);

        drawMaze(g);
        drawFood(g);

        // Draw enhanced scoreboard
        drawEnhancedScoreboard(g);

        // Draw ghosts
        for (int i = 0; i < 4; i++) {
            Image ghostImage;
            if (i == 0) ghostImage = redghost;
            else if (i == 1) ghostImage = blueghost;
            else if (i == 2) ghostImage = pinkghost;
            else ghostImage = orangeghost;

            if (powerMode) {
                ghostImage = scaredghost;
            }
            g.drawImage(ghostImage, ghostX[i], ghostY[i], titlesize, titlesize, this);
        }

        // Draw Pacman
        Image pacmanImage = pacright;
        if (pacmanDirection == 0) pacmanImage = pacup;
        else if (pacmanDirection == 1) pacmanImage = pacdown;
        else if (pacmanDirection == 2) pacmanImage = pacleft;
        else if (pacmanDirection == 3) pacmanImage = pacright;

        g.drawImage(pacmanImage, pacmanX, pacmanY, titlesize, titlesize, this);

        // Draw cherries with glow effect
        drawEnhancedCherries(g);

        // Game over / pause messages
        if (!gameRunning) {
            drawGameOverScreen(g);
        }

        if (gamePaused && gameRunning) {
            drawPauseScreen(g);
        }
    }

    void drawEnhancedBackground(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(0, 0, new Color(10, 10, 30),
                0, getHeight(), new Color(0, 0, 10));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw subtle grid pattern
        g2d.setColor(new Color(255, 255, 255, 10));
        for (int i = 0; i < columncount; i++) {
            g2d.drawLine(i * titlesize, 0, i * titlesize, getHeight());
        }
        for (int i = 0; i < rowcount; i++) {
            g2d.drawLine(0, i * titlesize, getWidth(), i * titlesize);
        }
    }

    void drawEnhancedScoreboard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw semi-transparent panel
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(10, 10, 300, 80, 20, 20);
        g2d.setColor(new Color(255, 255, 0, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(10, 10, 300, 80, 20, 20);

        // Score with gradient
        g2d.setFont(new Font("Press Start 2P", Font.BOLD, 14));

        // Current Score
        g2d.setColor(Color.YELLOW);
        g2d.drawString("SCORE", 25, 35);
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.format("%05d", score), 120, 35);

        // High Score
        g2d.setColor(Color.CYAN);
        g2d.drawString("HIGH", 200, 35);
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.format("%05d", highScore), 260, 35);

        // Lives with Pacman icons
        g2d.setColor(Color.YELLOW);
        g2d.drawString("LIVES", 25, 60);
        for (int i = 0; i < lives; i++) {
            g2d.drawImage(pacright, 100 + (i * 25), 45, 20, 20, this);
        }

        // Dots remaining
        g2d.setColor(Color.GREEN);
        g2d.drawString("DOTS", 200, 60);
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.format("%03d", totalDots - dotsEaten), 260, 60);

        if (powerMode) {
            // Power mode timer bar
            int barWidth = (powerTimer * 2);
            g2d.setColor(new Color(255, 255, 0, 150));
            g2d.fillRoundRect(320, 20, barWidth, 15, 10, 10);
            g2d.setColor(Color.YELLOW);
            g2d.drawRoundRect(320, 20, 200, 15, 10, 10);
            g2d.drawString("POWER", 320, 15);
        }

        // Level indicator
        g2d.setColor(Color.ORANGE);
        g2d.drawString("LEVEL 1", 320, 60);
    }

    void drawEnhancedCherries(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Add glow effect for cherries
        int cherry1X = 1 * titlesize;
        int cherry1Y = 2 * titlesize;
        int cherry2X = 17 * titlesize;
        int cherry2Y = 17 * titlesize;

        // Cherry 1 glow
        g2d.setColor(new Color(255, 0, 0, 50));
        g2d.fillOval(cherry1X - 5, cherry1Y - 5, titlesize + 10, titlesize + 10);
        g2d.drawImage(cherry, cherry1X, cherry1Y, titlesize, titlesize, this);

        // Cherry 2 glow
        g2d.setColor(new Color(255, 0, 0, 50));
        g2d.fillOval(cherry2X - 5, cherry2Y - 5, titlesize + 10, titlesize + 10);
        g2d.drawImage(cherry2, cherry2X, cherry2Y, titlesize, titlesize, this);

        // Add shine
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillOval(cherry1X + 8, cherry1Y + 8, 6, 6);
        g2d.fillOval(cherry2X + 8, cherry2Y + 8, 6, 6);
    }

    void drawGameOverScreen(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setFont(new Font("Press Start 2P", Font.BOLD, 48));

        if (lives <= 0) {
            // Game Over
            g2d.setColor(Color.RED);
            String gameOver = "GAME OVER";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(gameOver)) / 2;
            g2d.drawString(gameOver, x, getHeight() / 2 - 50);

            // Final score
            g2d.setFont(new Font("Press Start 2P", Font.BOLD, 24));
            g2d.setColor(Color.YELLOW);
            String finalScore = "FINAL SCORE: " + String.format("%05d", score);
            fm = g2d.getFontMetrics();
            x = (getWidth() - fm.stringWidth(finalScore)) / 2;
            g2d.drawString(finalScore, x, getHeight() / 2);

            // Update high score
            if (score > highScore) {
                highScore = score;
                String newRecord = "NEW HIGH SCORE!";
                g2d.setColor(Color.CYAN);
                fm = g2d.getFontMetrics();
                x = (getWidth() - fm.stringWidth(newRecord)) / 2;
                g2d.drawString(newRecord, x, getHeight() / 2 + 40);
            }
        } else {
            // You Win
            g2d.setColor(Color.GREEN);
            String winMsg = "YOU WIN!";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(winMsg)) / 2;
            g2d.drawString(winMsg, x, getHeight() / 2 - 50);

            g2d.setFont(new Font("Press Start 2P", Font.BOLD, 24));
            g2d.setColor(Color.YELLOW);
            String finalScore = "SCORE: " + String.format("%05d", score);
            fm = g2d.getFontMetrics();
            x = (getWidth() - fm.stringWidth(finalScore)) / 2;
            g2d.drawString(finalScore, x, getHeight() / 2);
        }

        g2d.setFont(new Font("Press Start 2P", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        String restartMsg = "PRESS R TO RESTART";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(restartMsg)) / 2;
        g2d.drawString(restartMsg, x, getHeight() / 2 + 100);
    }

    void drawPauseScreen(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setFont(new Font("Press Start 2P", Font.BOLD, 48));
        g2d.setColor(Color.YELLOW);
        String paused = "PAUSED";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(paused)) / 2;
        g2d.drawString(paused, x, getHeight() / 2);

        g2d.setFont(new Font("Press Start 2P", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        String resume = "PRESS P TO RESUME";
        fm = g2d.getFontMetrics();
        x = (getWidth() - fm.stringWidth(resume)) / 2;
        g2d.drawString(resume, x, getHeight() / 2 + 50);
    }

    void drawMaze(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (int row = 0; row < rowcount; row++) {
            for (int col = 0; col < columncount; col++) {
                if (maze[row][col] == 1) {
                    int x = col * titlesize;
                    int y = row * titlesize;

                    // Draw wall with 3D effect
                    g2d.setColor(new Color(33, 33, 255));
                    g2d.fillRoundRect(x + 2, y + 2, titlesize - 4, titlesize - 4, 8, 8);
                    g2d.setColor(new Color(100, 100, 255));
                    g2d.drawRoundRect(x + 1, y + 1, titlesize - 3, titlesize - 3, 8, 8);
                    g2d.setColor(new Color(255, 255, 255, 50));
                    g2d.drawRoundRect(x + 3, y + 3, titlesize - 7, titlesize - 7, 5, 5);
                }
            }
        }
    }

    void drawFood(Graphics g) {
        for (int row = 0; row < rowcount; row++) {
            for (int col = 0; col < columncount; col++) {
                if (maze[row][col] == 0) {
                    int x = col * titlesize + titlesize / 2;
                    int y = row * titlesize + titlesize / 2;

                    boolean isPowerPellet = (row == 3 && col == 1) ||
                            (row == 3 && col == 17) ||
                            (row == 17 && col == 1) ||
                            (row == 17 && col == 17);

                    if (isPowerPellet) {
                        // Power pellet with glow
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setColor(new Color(255, 255, 0, 100));
                        g2d.fillOval(x - 12, y - 12, 24, 24);
                        g2d.setColor(Color.YELLOW);
                        g2d.fillOval(x - 7, y - 7, 14, 14);
                    } else {
                        g.setColor(Color.WHITE);
                        g.fillOval(x - 2, y - 2, 5, 5);
                    }
                }
            }
        }
    }

    void drawGameInfo(Graphics g) {
        // This method is replaced by drawEnhancedScoreboard
        // Keeping empty to avoid errors
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning && !gamePaused) {
            movePacman();
            moveGhosts();
            checkCollisions();
            checkPowerMode();
            repaint();
        }
    }

    void movePacman() {
        // Try to change to desired direction
        if (nextDirection != pacmanDirection) {
            int newX = pacmanX;
            int newY = pacmanY;

            switch(nextDirection) {
                case 0: newY -= pacmanSpeed; break;
                case 1: newY += pacmanSpeed; break;
                case 2: newX -= pacmanSpeed; break;
                case 3: newX += pacmanSpeed; break;
            }

            if (!isCollision(newX, newY)) {
                pacmanDirection = nextDirection;
            }
        }

        // Move in current direction
        int newX = pacmanX;
        int newY = pacmanY;

        switch(pacmanDirection) {
            case 0: newY -= pacmanSpeed; break;
            case 1: newY += pacmanSpeed; break;
            case 2: newX -= pacmanSpeed; break;
            case 3: newX += pacmanSpeed; break;
        }

        if (!isCollision(newX, newY)) {
            pacmanX = newX;
            pacmanY = newY;
            checkFoodCollection();
        }

        // Tunnel teleport
        if (pacmanX < 0) {
            pacmanX = columncount * titlesize - titlesize;
        } else if (pacmanX >= columncount * titlesize) {
            pacmanX = 0;
        }

        if (pacmanY < 0) {
            pacmanY = rowcount * titlesize - titlesize;
        } else if (pacmanY >= rowcount * titlesize) {
            pacmanY = 0;
        }
    }

    boolean isCollision(int x, int y) {
        int leftCol = x / titlesize;
        int rightCol = (x + titlesize - 1) / titlesize;
        int topRow = y / titlesize;
        int bottomRow = (y + titlesize - 1) / titlesize;

        if (leftCol < 0 || rightCol >= columncount || topRow < 0 || bottomRow >= rowcount) {
            return false; // Allow tunnel
        }

        return (maze[topRow][leftCol] == 1 ||
                maze[topRow][rightCol] == 1 ||
                maze[bottomRow][leftCol] == 1 ||
                maze[bottomRow][rightCol] == 1);
    }

    void checkFoodCollection() {
        int col = pacmanX / titlesize;
        int row = pacmanY / titlesize;

        if (row >= 0 && row < rowcount && col >= 0 && col < columncount) {
            if (maze[row][col] == 0) {
                boolean isPowerPellet = (row == 3 && col == 1) ||
                        (row == 3 && col == 17) ||
                        (row == 17 && col == 1) ||
                        (row == 17 && col == 17);

                maze[row][col] = 4; // Mark as eaten

                if (isPowerPellet) {
                    score += 50;
                    powerMode = true;
                    powerTimer = 100;
                } else {
                    score += 10;
                }
                dotsEaten++;
            }
        }

        if (dotsEaten >= totalDots) {
            gameRunning = false;
            if (score > highScore) {
                highScore = score;
            }
        }
    }

    void moveGhosts() {
        for (int i = 0; i < 4; i++) {
            if (random.nextInt(30) == 0) {
                ghostDirection[i] = random.nextInt(4);
            }

            int newX = ghostX[i];
            int newY = ghostY[i];

            switch(ghostDirection[i]) {
                case 0: newY -= ghostSpeed; break; // Using increased ghost speed
                case 1: newY += ghostSpeed; break;
                case 2: newX -= ghostSpeed; break;
                case 3: newX += ghostSpeed; break;
            }

            if (!isCollision(newX, newY)) {
                ghostX[i] = newX;
                ghostY[i] = newY;
            } else {
                ghostDirection[i] = random.nextInt(4);
            }

            if (ghostX[i] < 0) {
                ghostX[i] = columncount * titlesize - titlesize;
            } else if (ghostX[i] >= columncount * titlesize) {
                ghostX[i] = 0;
            }

            if (ghostY[i] < 0) {
                ghostY[i] = rowcount * titlesize - titlesize;
            } else if (ghostY[i] >= rowcount * titlesize) {
                ghostY[i] = 0;
            }
        }
    }

    void checkCollisions() {
        int pacmanCenterX = pacmanX + titlesize / 2;
        int pacmanCenterY = pacmanY + titlesize / 2;

        for (int i = 0; i < 4; i++) {
            int ghostCenterX = ghostX[i] + titlesize / 2;
            int ghostCenterY = ghostY[i] + titlesize / 2;

            double distance = Math.sqrt(Math.pow(pacmanCenterX - ghostCenterX, 2) +
                    Math.pow(pacmanCenterY - ghostCenterY, 2));

            if (distance < titlesize / 2) {
                if (powerMode) {
                    score += 200;
                    if (i == 0) {
                        ghostX[i] = 9 * titlesize;
                        ghostY[i] = 8 * titlesize;
                    } else if (i == 1) {
                        ghostX[i] = 8 * titlesize;
                        ghostY[i] = 8 * titlesize;
                    } else if (i == 2) {
                        ghostX[i] = 10 * titlesize;
                        ghostY[i] = 8 * titlesize;
                    } else {
                        ghostX[i] = 9 * titlesize;
                        ghostY[i] = 7 * titlesize;
                    }
                } else {
                    lives--;
                    if (lives <= 0) {
                        gameRunning = false;
                        if (score > highScore) {
                            highScore = score;
                        }
                    } else {
                        pacmanX = 9 * titlesize;
                        pacmanY = 16 * titlesize;
                        ghostX[0] = 9 * titlesize;
                        ghostY[0] = 8 * titlesize;
                        ghostX[1] = 8 * titlesize;
                        ghostY[1] = 8 * titlesize;
                        ghostX[2] = 10 * titlesize;
                        ghostY[2] = 8 * titlesize;
                        ghostX[3] = 9 * titlesize;
                        ghostY[3] = 7 * titlesize;

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    void checkPowerMode() {
        if (powerMode) {
            powerTimer--;
            if (powerTimer <= 0) {
                powerMode = false;
            }
        }
    }

    void restartGame() {
        int[][] originalMaze = {
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,1,1,0,1,1,1,0,1,1,1,0,1,1,0,1,0,1},
                {1,0,1,1,0,1,1,1,0,1,1,1,0,1,1,0,1,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,0,1},
                {1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,1},
                {1,1,1,1,0,1,1,1,0,1,0,1,1,1,0,1,1,1,1},
                {0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0},
                {1,1,1,1,0,1,0,1,1,0,1,1,0,1,0,1,1,1,1},
                {0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0},
                {1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1},
                {0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0},
                {1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,1,1,0,1,1,1,0,1,1,1,0,1,1,0,1,0,1},
                {1,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,1},
                {1,1,0,1,0,1,0,1,1,1,1,1,0,1,0,1,0,1,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };

        maze = originalMaze;
        initializeGame();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (!gameRunning) {
            if (key == KeyEvent.VK_R) {
                restartGame();
            }
            return;
        }

        if (key == KeyEvent.VK_P) {
            gamePaused = !gamePaused;
            return;
        }

        if (gamePaused) return;

        if (key == KeyEvent.VK_UP) {
            nextDirection = 0;
        } else if (key == KeyEvent.VK_DOWN) {
            nextDirection = 1;
        } else if (key == KeyEvent.VK_LEFT) {
            nextDirection = 2;
        } else if (key == KeyEvent.VK_RIGHT) {
            nextDirection = 3;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}