package Game;

import Game.Brick.Brick;
import Game.Brick.unBreakBrick;
import Game.Map.*;
import Game.PowerUp.PowerUp;
import Game.PowerUp.PowerUpManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;


public class GameManager {
    public static final int screenWidth = 800;
    public static final int SW = 900;
    public static final int screenHeight = 600;

    private final GraphicsContext gc;
    private final Renderer renderer;

    private final List<GameObject> objects = new ArrayList<>();
    private final List<Map> maps = new ArrayList<>();
    private final List<Ball> extraBalls = new ArrayList<>(); // bóng phụ từ MultiBall

    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks = new ArrayList<>();
    private List<PowerUp> powerUps;
    private int score = 0;
    private int lives = 5;
    private Image BackGround;
    private int currentLevel;

    //private List<Map> maps=new ArrayList<>();

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean spacePressed = false;
    private boolean gameStarted = false;

    private PowerUpManager powerUpManager;

    public GameManager(GraphicsContext gc) {
        this.gc = gc;
        this.renderer = new Renderer(gc);

        // Paddle & Ball khởi tạo
        paddle = new Paddle(screenWidth / 2.0 - 50, screenHeight - 40, 100, 20, 4);

        int ballSize = 15;
        double ballX = paddle.getX() + paddle.getWidth() / 2.0 - ballSize / 2.0;
        double ballY = paddle.getY() - ballSize - 2;
        ball = new Ball(ballX, ballY, ballSize, 4);
        paddle.setBall(ball);

        // Map & bricks
        maps.add(new Map1());
        maps.add(new Map2());
        maps.add(new Map3());
        maps.add(new Map4());
        maps.add(new Map5());

        currentLevel = 1;
        loadCurrentMap();
    }

    private void loadCurrentMap() {
        Map currentMap = maps.get(currentLevel-1);
        bricks = currentMap.getBricks();
        BackGround = currentMap.getBackGround();

        // Reset vị trí paddle & bóng
        paddle.setX(screenWidth / 2 - paddle.getWidth() / 2);
        paddle.setY(screenHeight - 40);

        int ballSize = 15;
        double ballX = paddle.getX() + paddle.getWidth() / 2 - ballSize / 2;
        double ballY = paddle.getY() - ballSize - 2;
        ball = new Ball(ballX, ballY, ballSize, 4);

        gameStarted = false; // chờ người chơi nhấn space để bắt đầu lại
        bricks = maps.get(0).getBricks();

        // PowerUpManager
        powerUpManager = new PowerUpManager(screenHeight);

        // (1) Bóng mới từ MultiBall sẽ thêm vào extraBalls
        powerUpManager.setBallSink(extraBalls::add);

        // (2) Cho Manager biết cách lấy TẤT CẢ bóng hiện có (ball chính + extraBalls)
        powerUpManager.setBallsSupplier(() -> {
            ArrayList<Ball> all = new ArrayList<>();
            all.add(ball);          // bóng chính
            all.addAll(extraBalls); // bóng phụ
            return all;
        });

        // (3) Tuỳ chọn: chỉ định ảnh fire cho bóng (đổi được lúc runtime)
        // Nếu Fireball đang chạy khi gọi, Manager sẽ cập nhật skin cho toàn bộ bóng ngay.
        powerUpManager.setFireBallSkin("/Image/ballfire.png");

        // (tuỳ chọn test)
        // powerUpManager.setDropRates(0.50, 0.20, 0.20, 0.10); // Fast/Expand/Fire/Multi
        // powerUpManager.setMaxPerTypePerLevel(3);            // mỗi loại tối đa 3 lần
    }

    //update all object every frame
    public void updateGame(double deltaTime) {
        handleInput();

        if (!gameStarted) {
            // Giữ bóng bám theo paddle trước khi bắn
            int ballSize = 15;
            paddle.update(deltaTime, 0, screenWidth);
            double x = paddle.getX() + paddle.getWidth() / 2.0 - ballSize / 2.0;
            double y = paddle.getY() - ballSize - 2;
            ball = new Ball(x, y, ballSize, 4);
            paddle.setBall(ball);
            return;
        }

        // Cập nhật sprite/ảnh gạch theo HP
        for (Brick brick : bricks) {
            brick.update(deltaTime, 0, screenWidth);
        }

        // === Bóng chính vs Gạch ===
        for (java.util.ListIterator<Brick> it = bricks.listIterator(); it.hasNext();) {
            Brick br = it.next();
            if (!ball.checkCollision(br)) continue;

            // Nếu Fireball bật xuyên: không bounce, chỉ gây damage
            if (!ball.isPiercing()) {
                ball.bounceOff(br);
            }
            br.takeHit(ball);

            if (br.isDestroyed()) {
                int cx = (int) (br.getX() + br.getWidth()  / 2.0);
                int cy = (int) (br.getY() + br.getHeight() / 2.0);
                powerUpManager.maybeDropAt(cx, cy);
                it.remove();
            }
        }

        // === Mỗi bóng phụ vs Gạch + Paddle ===
        for (int i = 0; i < extraBalls.size(); i++) {
            Ball b = extraBalls.get(i);

            for (java.util.ListIterator<Brick> it = bricks.listIterator(); it.hasNext();) {
                Brick br = it.next();
                if (!b.checkCollision(br)) continue;

                if (!b.isPiercing()) {
                    b.bounceOff(br);
                }
                br.takeHit(b);

                if (br.isDestroyed()) {
                    int cx = (int) (br.getX() + br.getWidth()  / 2.0);
                    int cy = (int) (br.getY() + br.getHeight() / 2.0);
                    powerUpManager.maybeDropAt(cx, cy);
                    it.remove();
                }
            }

            if (b.checkCollision(paddle)) {
                b.bounceOff(paddle);
            }

            b.update(deltaTime, 0, screenWidth);

            // rơi khỏi đáy -> loại bóng phụ
            if (b.getY() > screenHeight) {
                extraBalls.remove(i);
                i--;
            }
        }

        // Other objects
        for (GameObject obj : objects) {
            obj.update(deltaTime, 0, screenWidth);
        }

        // Bóng chính vs Paddle
        if (ball.checkCollision(paddle)) {
            ball.bounceOff(paddle);
        }

        ball.update(deltaTime,0,screenWidth);
        paddle.update(deltaTime,0,screenWidth);

        boolean check = true;
        for(Brick br : bricks){
            if(!(br instanceof unBreakBrick)){
                check = false;
            }
        }
        if (check) {
            currentLevel++;
            loadCurrentMap();
        }

        // PowerUp: rơi -> nhặt -> (nếu có thời gian) đếm lùi
        powerUpManager.update(deltaTime, paddle);
    }

    //render all object every frame
    public void render() {
        renderer.clear(screenWidth, screenHeight);
        if (BackGround != null) {
            renderer.drawBackground(BackGround, screenWidth, screenHeight);
        }
        for (Brick brick : bricks) {
            renderer.draw(brick);
        }
        renderer.draw(ball);
        for (Ball b : extraBalls) {
            renderer.draw(b);
        }

        // Vẽ paddle
        renderer.draw(paddle);

        // Vẽ item power-up đang rơi
        powerUpManager.render(gc);
    }

    //current movement
    public void handleInput() {
        if (spacePressed) gameStarted = true;

        if (leftPressed) {
            paddle.moveLeft();
        } else if (rightPressed) {
            paddle.moveRight();
        } else {
            paddle.stop();
        }
    }

    //Save game
    public void saveGame(String fileName){
        GameState curState = new GameState();
        //save ball state
        curState.setBallX(ball.getX());
        curState.setBallY(ball.getY());
        curState.setBallDirectionX(ball.getDirectionX());
        curState.setBallDirectionY(ball.getDirectionY());
        curState.setBallDX(ball.getDx());
        curState.setBallDY(ball.getDy());

        //save paddle state
        curState.setPaddleX(paddle.getX());
        curState.setPaddleY(paddle.getY());
        curState.setPaddleSpeed(paddle.getSpeed());

        //save lives and score
        curState.setLives(lives);
        curState.setScore(score);

        //save brick state
        for(Brick brick : bricks){
            curState.bricks.add(new GameState.BrickState(brick.getX(), brick.getY(), brick.isDestroyed(), brick.getHitPoints()));
        }

        //save to json file
        try (FileWriter writer = new FileWriter(fileName)) {
            Gson gson = new Gson();
            gson.toJson(curState, writer);
            System.out.println("Game saved to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Load Game
    public void loadGame(String fileName){
        try (FileReader reader = new FileReader(fileName)) {
            Gson gson = new Gson();
            GameState state = gson.fromJson(reader, GameState.class);

            //load ball
            ball.setX(state.getBallX());
            ball.setY(state.getBallY());
            ball.setDx(state.getBallDX());
            ball.setDy(state.getBallDY());
            ball.setDirectionX(state.getBallDirectionX());
            ball.setDirectionY(state.getBallDirectionY());

            //load paddle
            paddle.setX(state.getPaddleX());
            paddle.setY(state.getPaddleY());
            paddle.setSpeed(state.getPaddleSpeed());

            //load lives and scores
            lives = state.getLives();
            score = state.getScore();

            bricks.clear();
            for(GameState.BrickState brickstate : state.bricks){
                bricks.add(new Brick(brickstate.getX(), brickstate.getY(), brickstate.getHitPoints()));
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void onKeyPressed(KeyCode key) {
        if (key == KeyCode.LEFT)  leftPressed = true;
        if (key == KeyCode.RIGHT) rightPressed = true;
        if(key == KeyCode.TAB) spacePressed = true;
        if (key == KeyCode.S) saveGame("save.json");
        if (key == KeyCode.L) loadGame("save.json");
        if (key == KeyCode.SPACE) spacePressed = true; // SPACE để bắt đầu
    }

    public void onKeyReleased(KeyCode key) {
        if (key == KeyCode.LEFT)  leftPressed = false;
        if (key == KeyCode.RIGHT) rightPressed = false;
        if (key == KeyCode.SPACE) spacePressed = false; // nhả SPACE -> false
    }

    public void checkCollisions() {}
    public void gameOver() {}
}
