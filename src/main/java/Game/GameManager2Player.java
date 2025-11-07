package Game;

import Game.Brick.Brick;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager2Player {
    public static final int screenWidth = 800;
    public static final int screenHeight = 600;

    private Renderer renderer;
    private Paddle paddle1, paddle2;
    private Ball ball1, ball2;
    private List<Brick> brick1 = new ArrayList<>();
    private List<Brick> brick2 = new ArrayList<>();

    private boolean leftPressed, rightPressed, APressed, DPressed;
    private boolean gameStarted1 = false;
    private boolean gameStarted2 = false;
    private boolean gameReady = false;

    private Image image = new Image(getClass().getResourceAsStream("/Image/anhnenBattle.jpg"));
    private Image imageLife = new Image(getClass().getResourceAsStream("/Image/Life.png"));
    private Random rand = new Random();

    private long lastBrickTime = 0;
    private final long brickInterval = 5_000_000_000L;

    private int lives1 = 5;
    private int lives2 = 5;
    private int score1 = 0;
    private int score2 = 0;

    int uiHeight = 80;
    Canvas canvas = new Canvas(screenWidth, screenHeight + uiHeight);

    public GameManager2Player(GraphicsContext gc) {
        renderer = new Renderer(gc);
        paddle1 = new Paddle(screenWidth / 4 - 50, screenHeight - 100, 100, 20, 4);
        paddle2 = new Paddle((3 * screenWidth) / 4 - 50, screenHeight - 100, 100, 20, 4);

        int ballSize = 15;
        double ballX1 = paddle1.getX() + paddle1.getWidth() / 2 - ballSize / 2;
        double ballY1 = paddle1.getY() - ballSize - 2;
        double ballX2 = paddle2.getX() + paddle2.getWidth() / 2 - ballSize / 2;
        double ballY2 = paddle2.getY() - ballSize - 2;

        ball1 = new Ball(ballX1, ballY1, ballSize, 4);
        ball2 = new Ball(ballX2, ballY2, ballSize, 4);
    }

    private void addBrickPair() {
        int brickWidth = 30;
        int brickHeight = 10;

        double xLeft = rand.nextDouble(screenWidth / 2 - brickWidth - 3);
        double xRight = screenWidth / 2 + 3 + xLeft;
        double y = -brickHeight;

        Brick left = new Brick(xLeft, y, 1);
        Brick right = new Brick(xRight, y, 1);

        brick1.add(left);
        brick2.add(right);
    }

    private void startGame(int player) {
        if (player == 1) gameStarted1 = true;
        else if (player == 2) gameStarted2 = true;
        lastBrickTime = System.nanoTime();
    }

    public void onKeyPressed(KeyCode key) {
        if (key == KeyCode.LEFT) leftPressed = true;
        if (key == KeyCode.RIGHT) rightPressed = true;
        if (key == KeyCode.A) APressed = true;
        if (key == KeyCode.D) DPressed = true;

        // Nhấn W để bắt đầu player 1, UP để bắt đầu player 2
        if (key == KeyCode.W && !gameStarted1) startGame(1);
        if (key == KeyCode.UP && !gameStarted2) startGame(2);
        if (key == KeyCode.SPACE) gameReady = true;
    }

    public void onKeyReleased(KeyCode key) {
        if (key == KeyCode.LEFT) leftPressed = false;
        if (key == KeyCode.RIGHT) rightPressed = false;
        if (key == KeyCode.A) APressed = false;
        if (key == KeyCode.D) DPressed = false;
    }

    public void handleInput() {
        if (leftPressed) paddle2.moveLeft();
        else if (rightPressed) paddle2.moveRight();
        else paddle2.stop();

        if (APressed) paddle1.moveLeft();
        else if (DPressed) paddle1.moveRight();
        else paddle1.stop();
    }

    boolean checkCollision(Brick b, Paddle p) {
        return b.getX() < p.getX() + p.getWidth() &&
                b.getX() + b.getWidth() > p.getX() &&
                b.getY() < p.getY() + p.getHeight() &&
                b.getY() + b.getHeight() > p.getY();
    }

    public void updateGame(double deltaTime) {
        handleInput();
        if (!gameReady)
        {
            return;
        }
        long now = System.nanoTime();
        if (now - lastBrickTime >= brickInterval) {
            addBrickPair();
            lastBrickTime = now;
        }

        int fallSpeed = 10;
        boolean brickBelowPaddle1 = false;
        boolean brickBelowPaddle2 = false;

        for (Brick b : brick1) {
            b.setY(b.getY() + fallSpeed * deltaTime);
            if (checkCollision(b, paddle1) || b.getY() + b.getHeight() >= screenHeight) {
                brickBelowPaddle1 = true;
                break;
            }
        }
        for (Brick b : brick2) {
            b.setY(b.getY() + fallSpeed * deltaTime);
            if (checkCollision(b, paddle2) || b.getY() + b.getHeight() >= screenHeight) {
                brickBelowPaddle2 = true;
                break;
            }
        }

        if (brickBelowPaddle1) {
            brick1.clear();
            lives1--;
        }
        if (brickBelowPaddle2) {
            brick2.clear();
            lives2--;
        }

        // --- PLAYER 1 ---
        if (!gameStarted1) {
            paddle1.update(deltaTime, 0, screenWidth / 2);
            int ballSize = 15;
            double x1 = paddle1.getX() + paddle1.getWidth() / 2 - ballSize / 2;
            double y1 = paddle1.getY() - ballSize - 2;
            ball1.setX(x1);
            ball1.setY(y1);
        } else {
            ball1.update(deltaTime, 0, screenWidth / 2);
        }

        // --- PLAYER 2 ---
        if (!gameStarted2) {
            paddle2.update(deltaTime, screenWidth / 2 + 3, screenWidth);
            int ballSize = 15;
            double x2 = paddle2.getX() + paddle2.getWidth() / 2 - ballSize / 2;
            double y2 = paddle2.getY() - ballSize - 2;
            ball2.setX(x2);
            ball2.setY(y2);
        } else {
            ball2.update(deltaTime, screenWidth / 2 + 3, screenWidth);
        }

        // Khi bóng rơi ra ngoài màn hình
        if (ball1.getY() > screenHeight) {
            lives1--;
            resetBallOnPaddle(ball1, paddle1);
            gameStarted1 = false; // chỉ dừng player 1
        }
        if (ball2.getY() > screenHeight) {
            lives2--;
            resetBallOnPaddle(ball2, paddle2);
            gameStarted2 = false; // chỉ dừng player 2
        }

        paddle1.update(deltaTime, 0, screenWidth / 2);
        paddle2.update(deltaTime, screenWidth / 2 + 3, screenWidth);

        if (ball1.checkCollision(paddle1)) ball1.bounceOff(paddle1);
        if (ball2.checkCollision(paddle2)) ball2.bounceOff(paddle2);

        checkBrick(deltaTime, brick1, ball1, true);
        checkBrick(deltaTime, brick2, ball2, false);
    }

    private void resetBallOnPaddle(Ball ball, Paddle paddle) {
        int ballSize = 15;
        double x = paddle.getX() + paddle.getWidth() / 2 - ballSize / 2;
        double y = paddle.getY() - ballSize - 2;
        ball.setX(x);
        ball.setY(y);
    }

    public void checkBrick(double deltaTime, List<Brick> bricks, Ball ball, boolean isPlayer1) {
        int destroyed = -1, index = 0;
        for (Brick br : bricks) {
            if (ball.checkCollision(br)) {
                ball.bounceOff(br);
                br.takeHit(ball);
                if (br.isDestroyed()) {
                    destroyed = index;
                    if (isPlayer1) score1 += 10;
                    else score2 += 10;
                }
            }
            index++;
        }
        if (destroyed != -1) bricks.remove(destroyed);
    }

    public void render() {
        renderer.clear(screenWidth, screenHeight);
        renderer.getGc().drawImage(image, 0, 0, screenWidth, screenHeight);
        // Đường chia giữa
        LinearGradient lineGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.BLUE),
                new Stop(1, Color.web("#eb1a2f"))
        );
        renderer.getGc().setStroke(lineGradient);
        renderer.getGc().setLineWidth(3);
        renderer.getGc().strokeLine(screenWidth / 2.0, 0, screenWidth / 2.0, screenHeight);

        // --- UI kim loại bạc ---
        GraphicsContext gc = renderer.getGc();

        LinearGradient silverGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#f5f5f5")),
                new Stop(0.25, Color.web("#c0c0c0")),
                new Stop(0.5, Color.web("#8c8c8c")),
                new Stop(0.75, Color.web("#dcdcdc")),
                new Stop(1, Color.web("#f5f5f5"))
        );

        DropShadow glow = new DropShadow();
        glow.setColor(Color.BLACK);
        glow.setRadius(18);
        glow.setSpread(0.5);

        gc.setEffect(glow);
        gc.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 20));
        gc.setFill(silverGradient);
        gc.setStroke(Color.web("#e0f7fa"));
        gc.setLineWidth(2);
        if (!gameReady) {
            gc.fillText("PRESS SPACE TO START",250, screenHeight / 2);
            gc.strokeText("PRESS SPACE TO START", 250, screenHeight / 2);
            return;
        }
        gc.fillText("PLAYER 1", 30, 40);
        gc.strokeText("PLAYER 1", 30, 40);
        gc.fillText("Score: " + score1, 250, 580);
        gc.strokeText("Score: " + score1, 250, 580);
        gc.fillText("Lives: ", 30, 580);
        gc.strokeText("Lives: ", 30, 580);
        int start1 = 115;
        for (int i = 1; i <= lives1; i++) {
            renderer.getGc().drawImage(imageLife, start1, 565, 20, 20);
            start1 += 25;
        }

        gc.fillText("PLAYER 2", screenWidth - 180, 40);
        gc.strokeText("PLAYER 2", screenWidth - 180, 40);
        gc.fillText("Score: " + score2, screenWidth - 140, 580);
        gc.strokeText("Score: " + score2, screenWidth - 140, 580);
        gc.fillText("Lives: " , screenWidth - 360, 580);
        gc.strokeText("Lives: " , screenWidth - 360, 580);
        int start2 = 520;
        for (int i = 1; i <= lives2; i++) {
            renderer.getGc().drawImage(imageLife, start2, 565, 20, 20);
            start2 += 25;
        }

        gc.setEffect(null);

        for (Brick brick : brick1) renderer.draw(brick);
        for (Brick brick : brick2) renderer.draw(brick);
        renderer.draw(ball1);
        renderer.draw(paddle1);
        renderer.draw(ball2);
        renderer.draw(paddle2);

    }
}
