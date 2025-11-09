package Game;

import Game.Brick.Brick;
import Game.PowerUp.PowerUpManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameManager2Player {
    public static final int screenWidth = 800;
    public static final int screenHeight = 600;
    private Stage stage;
    private Main mainApp;
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
    private final long brickInterval = 8_000_000_000L;

    private int lives1 = 5;
    private int lives2 = 5;
    private int score1 = 0;
    private int score2 = 0;
    private int winScores;
    private int winPlayer;

    private PowerUpManager powerUpManager1;
    private PowerUpManager powerUpManager2;
    private List<Ball> balls1;
    private List<Ball> balls2;
    private boolean isGameOver = false;

    int uiHeight = 80;
    Canvas canvas = new Canvas(screenWidth, screenHeight + uiHeight);

    public GameManager2Player(GraphicsContext gc, Stage stage, Main mainApp) {
        this.stage = stage;
        this.mainApp = mainApp;
        renderer = new Renderer(gc);
        paddle1 = new Paddle(screenWidth / 4 - 50, screenHeight - 100, 100, 20, 4);
        paddle2 = new Paddle((3 * screenWidth) / 4 - 50, screenHeight - 100, 100, 20, 4);

        int ballSize = 15;
        double ballX1 = paddle1.getX() + paddle1.getWidth() / 2 - ballSize / 2;
        double ballY1 = paddle1.getY() - ballSize - 2;
        double ballX2 = paddle2.getX() + paddle2.getWidth() / 2 - ballSize / 2;
        double ballY2 = paddle2.getY() - ballSize - 2;

        // tạo 2 quả bóng chính ban đầu
        ball1 = new Ball(ballX1, ballY1, ballSize, 4, false);
        ball2 = new Ball(ballX2, ballY2, ballSize, 4, false);

        // tạo danh sách quản lý các bóng cho từng player (dùng bởi PowerUpManager)
        balls1 = new ArrayList<>();
        balls2 = new ArrayList<>();
        balls1.add(ball1);
        balls2.add(ball2);

        // khởi tạo PowerUpManager cho từng player
        if (powerUpManager1 != null) {
            powerUpManager1.clearAll();
        }
        powerUpManager1 = new PowerUpManager(screenHeight);
        powerUpManager1.setBallSink(balls1::add);
        powerUpManager1.setBallsSupplier(() -> balls1);
        powerUpManager1.setFireBallSkin("/Image/ballfire.png");

        if (powerUpManager2 != null) {
            powerUpManager2.clearAll();
        }
        powerUpManager2 = new PowerUpManager(screenHeight);
        powerUpManager2.setBallSink(balls2::add);
        powerUpManager2.setBallsSupplier(() -> balls2);
        powerUpManager2.setFireBallSkin("/Image/ballfire.png");

        SoundManager.playGameMusic(true);
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
        if (isGameOver) return;
        handleInput();
        if (!gameReady) {
            return;
        }
        long now = System.nanoTime();
        if (now - lastBrickTime >= brickInterval) {
            addBrickPair();
            lastBrickTime = now;
        }

        int fallSpeed = 5;
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
            if(lives1 != 0) {
                SoundManager.lostLive();
            }
            // reset powerups của player1 nếu muốn:
            powerUpManager1.clearAll();
        }
        if (brickBelowPaddle2) {
            brick2.clear();
            lives2--;
            if (lives2 != 0) {
                SoundManager.lostLive();
            }
            powerUpManager2.clearAll();
        }

        // --- PLAYER 1: cập nhật tất cả bóng thuộc balls1 ---
        if (!gameStarted1) {
            // nếu chưa bắt đầu, giữ tất cả bóng dính paddle
            for (Ball b : balls1) {
                paddle1.update(deltaTime, 0, screenWidth / 2);
                int ballSize = b.getWidth();
                double x1 = paddle1.getX() + paddle1.getWidth() / 2 - ballSize / 2;
                double y1 = paddle1.getY() - ballSize - 2;
                b.setX(x1);
                b.setY(y1);
            }
        } else {
            // cập nhật từng bóng
            Iterator<Ball> it1 = balls1.iterator();
            while (it1.hasNext()) {
                Ball b = it1.next();
                b.update(deltaTime, 0, screenWidth / 2);
                // va chạm với paddle
                if (b.checkCollision(paddle1)) b.bounceOff(paddle1);
                // kiểm tra va chạm với brick (riêng cho player1)
                checkBricksForBall(b, brick1, true);
                // nếu rơi khỏi màn hình => remove
                if (b.getY() > screenHeight) {
                    it1.remove();
                }
            }
        }

        // --- PLAYER 2: cập nhật tất cả bóng thuộc balls2 ---
        if (!gameStarted2) {
            for (Ball b : balls2) {
                paddle2.update(deltaTime, screenWidth / 2 + 3, screenWidth);
                int ballSize = b.getWidth();
                double x2 = paddle2.getX() + paddle2.getWidth() / 2 - ballSize / 2;
                double y2 = paddle2.getY() - ballSize - 2;
                b.setX(x2);
                b.setY(y2);
            }
        } else {
            Iterator<Ball> it2 = balls2.iterator();
            while (it2.hasNext()) {
                Ball b = it2.next();
                b.update(deltaTime, screenWidth / 2 + 3, screenWidth);
                if (b.checkCollision(paddle2)) b.bounceOff(paddle2);
                checkBricksForBall(b, brick2, false);
                if (b.getY() > screenHeight) {
                    it2.remove();
                }
            }
        }

        // Nếu không còn bóng của player1 => mất mạng / reset
        if (balls1.isEmpty()) {
            lives1--;
            if(lives1 != 0 ) {
                SoundManager.lostLive();
            }
            // reset lại 1 quả bóng dính paddle và dừng player
            Ball newBall = createBallOnPaddle(paddle1);
            balls1.add(newBall);
            gameStarted1 = false;
            powerUpManager1.clearAll();
        }

        // Nếu không còn bóng của player2 => mất mạng / reset
        if (balls2.isEmpty()) {
            lives2--;
            if (lives2 != 0) {
                SoundManager.lostLive();
            }
            Ball newBall = createBallOnPaddle(paddle2);
            balls2.add(newBall);
            gameStarted2 = false;
            powerUpManager2.clearAll();
        }

        // Cập nhật vị trí và trạng thái paddle
        paddle1.update(deltaTime, 0, screenWidth / 2);
        paddle2.update(deltaTime, screenWidth / 2 + 3, screenWidth);

        // Cập nhật PowerUp manager (rơi, va chạm với paddle, active effect, ...)
        powerUpManager1.update(deltaTime, paddle1);
        powerUpManager2.update(deltaTime, paddle2);
        if (lives1 * lives2 == 0) {
            if (lives1 == 0) {
                winPlayer = 2;
                winScores = score2;
            } else {
                winScores = score1;
                winPlayer = 1;
            }
            isGameOver = true;
            gameOver();
        }
    }

    private Ball createBallOnPaddle(Paddle paddle) {
        int ballSize = 15;
        double x = paddle.getX() + paddle.getWidth() / 2 - ballSize / 2;
        double y = paddle.getY() - ballSize - 2;
        Ball b = new Ball(x, y, ballSize, 4, false);
        return b;
    }

    private void checkBricksForBall(Ball ball, List<Brick> bricks, boolean isPlayer1) {
        int destroyed = -1;
        int index = 0;
        for (Brick br : bricks) {
            if (ball.checkCollision(br)) {
                if (!ball.isPiercing()) {
                    ball.bounceOff(br);
                    br.takeHit(ball);
                } else if (ball.isPiercing()) {
                    int hp = br.getHitPoints();
                    if (hp <= 2) {
                        br.destroy(); // phá gạch hoàn toàn
                        // xuyên qua, không đổi hướng
                    } else {
                        ball.bounceOff(br);
                        br.reduceHp(2);
                        // trừ 2 HP
                        // Sau đó bật lại như bình thường
                    }
                }
                if (br.isDestroyed()) {
                    destroyed = index;
                    if (isPlayer1) {
                        score1 += 10;
                        if (score1 != 0 && score1 % 30 == 0) {
                            powerUpManager1.spawnPowerUp((int) br.getX(), (int) br.getY());
                        }
                        if (score1 > score2 && score1 % 100 == 0) {
                            lives2--;
                            if(lives2 != 0) {
                                SoundManager.lostLive();
                            }
                        }
                    } else {
                        score2 += 10;
                        if (score2 != 0 && score2 % 30 == 0) {
                            powerUpManager2.spawnPowerUp((int) br.getX(), (int) br.getY());
                        }
                        if (score2 > score1 && score2 % 100 == 0) {
                            lives1--;
                            if (lives1 != 0){
                                SoundManager.lostLive();
                            }
                        }
                    }
                }
            }
            index++;
        }
        if (destroyed != -1) bricks.remove(destroyed);
    }

    private void resetBallOnPaddle(Ball ball, Paddle paddle) {
        int ballSize = 15;
        double x = paddle.getX() + paddle.getWidth() / 2 - ballSize / 2;
        double y = paddle.getY() - ballSize - 2;
        ball.setX(x);
        ball.setY(y);
    }

    public void render() {
        if (isGameOver) return;
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
            gc.fillText("PRESS SPACE TO START", 250, screenHeight / 2);
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
        gc.fillText("Lives: ", screenWidth - 360, 580);
        gc.strokeText("Lives: ", screenWidth - 360, 580);
        int start2 = 520;
        for (int i = 1; i <= lives2; i++) {
            renderer.getGc().drawImage(imageLife, start2, 565, 20, 20);
            start2 += 25;
        }

        gc.setEffect(null);

        for (Brick brick : brick1) renderer.draw(brick);
        for (Brick brick : brick2) renderer.draw(brick);

        // vẽ tất cả bóng của player1 và player2
        for (Ball b : balls1) renderer.draw(b);
        for (Ball b : balls2) renderer.draw(b);

        // vẽ paddle
        renderer.draw(paddle1);
        renderer.draw(paddle2);

        // vẽ powerups (hai manager)
        powerUpManager1.render(renderer.getGc());
        powerUpManager2.render(renderer.getGc());
    }

    public void gameOver() {
        SoundManager.stopMusic(SoundManager.MPlayer);
        SoundManager.GameOver();
        System.out.println("GAME OVER");
        SoundManager.playBackgroundMusic(true);
        // tuỳ bạn: reset level, show menu,...
        if (stage != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/GameOver2Player.fxml"));
                Scene menuScene = new Scene(loader.load());
                MenuController ct = loader.getController();
                ct.setWin2PL(winPlayer);
                ct.setScore2PL(winScores);
                ct.setMainApp(this.mainApp);
                stage.setScene(menuScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
