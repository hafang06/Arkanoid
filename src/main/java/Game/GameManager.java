package Game;

import Game.Brick.Brick;
import Game.Brick.unBreakBrick;
import Game.Map.*;
import Game.PowerUp.PowerUp;
import Game.PowerUp.PowerUpManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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

    // => SỬ DỤNG 1 LIST DUY NHẤT CHO TẤT CẢ BÓNG
    private final List<Ball> balls = new ArrayList<>();

    private Paddle paddle;
    private List<Brick> bricks = new ArrayList<>();
    private List<PowerUp> powerUps;
    private int score = 0;
    private int lives = 5;
    private Image BackGround;
    private int currentLevel;
    private Image imageLife = new Image(getClass().getResourceAsStream("/Image/Life.png"));;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean spacePressed = false;
    private boolean gameStarted = false;
    private boolean isGameOver = false;
    private boolean isPaused = false;

    private Stage stage;
    private Main mainApp;

    private PowerUpManager powerUpManager;

    private double elapsedTime = 0; // thời gian trôi qua (giây)
    private final double RESET_INTERVAL = 180; // 3 phút
    private double timeRemaining = RESET_INTERVAL; // đếm ngược 3 phút


    public GameManager(GraphicsContext gc, Stage stage, Main mainApp) {
        this.gc = gc;
        this.renderer = new Renderer(gc);
        this.stage = stage;
        this.mainApp = mainApp;
        // Paddle khởi tạo
        paddle = new Paddle(screenWidth / 2.0 - 50, screenHeight - 40, 100, 20, 10);

        // Map & bricks
        currentLevel = 1;
        loadCurrentMap();
    }

    private void ensureSingleBallAttachedToPaddle() {
        // Dùng khi chưa start: chỉ giữ 1 bóng chính và nó dính theo paddle
        balls.clear();
        int ballSize = 15;
        double ballX = paddle.getX() + paddle.getWidth() / 2.0 - ballSize / 2.0;
        double ballY = paddle.getY() - ballSize - 2;
        Ball b = new Ball(ballX, ballY, ballSize, 10,false);

        if (currentLevel == 7) {
            b.setDirectionY(1); // bình thường: -1 (lên), bây giờ: +1 (xuống)
        }

        balls.add(b);
        paddle.setBall(b);
    }

    private void loadCurrentMap() {
        Map currentMap;
        switch (currentLevel) {
            case 1 -> currentMap = new Map1();
            case 2 -> currentMap = new Map2();
            case 3 -> currentMap = new Map3();
            case 4 -> currentMap = new Map4();
            case 5 -> currentMap = new Map5();
            case 6 -> currentMap = new Map6();
            case 7 -> currentMap = new Map7();
            default -> currentMap = new Map1();
        }
        bricks = currentMap.getBricks();
        BackGround = currentMap.getBackGround();

        // Reset vị trí paddle
        paddle.setX(screenWidth / 2 - paddle.getWidth() / 2);
        paddle.setY(screenHeight - 40);

        // Reset trạng thái game -> giữ 1 bóng dính paddle (chưa bắn)
        gameStarted = false;
        ensureSingleBallAttachedToPaddle();

        // Reset PowerUp trước khi tạo mới / hoặc khôi phục trạng thái
        if (powerUpManager != null) {
            powerUpManager.clearAll();
        }
        powerUpManager = new PowerUpManager(screenHeight);

        // (1) Bóng mới từ MultiBall sẽ thêm vào balls list
        powerUpManager.setBallSink(balls::add);

        // (2) Cho Manager biết cách lấy TẤT CẢ bóng hiện có (toàn bộ balls)
        powerUpManager.setBallsSupplier(() -> balls);

        // (3) Tuỳ chọn: chỉ định ảnh fire cho bóng (đổi được lúc runtime)
        powerUpManager.setFireBallSkin("/Image/ballfire.png");
        if (currentLevel != 1) {
            SoundManager.NextLevel();
        }
        SoundManager.stopMusic(SoundManager.MPlayer);
        SoundManager.playGameMusic(true);
    }

    //update all object every frame
    public void updateGame(double deltaTime) {
        if(isGameOver) return;
        if(isPaused) return;
        handleInput();
        if (!gameStarted) {
            // Giữ bóng đầu tiên bám theo paddle trước khi bắn; đảm bảo chỉ 1 bóng lúc này
            paddle.update(deltaTime, 0, screenWidth);
            Ball main = balls.isEmpty() ? null : balls.get(0);
            if (main != null) {
                int ballSize = main.getWidth();
                double x = paddle.getX() + paddle.getWidth() / 2.0 - ballSize / 2.0;
                double y = paddle.getY() - ballSize - 2;
                main.setX(x);
                main.setY(y);
                paddle.setBall(main);
            } else {
                ensureSingleBallAttachedToPaddle();
            }
            return;
        }

        // Cập nhật sprite/ảnh gạch theo HP

        // === Bóng vs Gạch ===
        // Duyệt qua tất cả bóng (sử dụng iterator để có thể xóa an toàn)
        for (Iterator<Ball> bit = balls.iterator(); bit.hasNext();) {
            Ball b = bit.next();

            // Va chạm với bricks
            for (java.util.ListIterator<Brick> it = bricks.listIterator(); it.hasNext();) {
                Brick br = it.next();
                if (!b.checkCollision(br)) continue;

                // Nếu Fireball bật xuyên: không bounce, chỉ gây damage
                if (!b.isPiercing()) {
                    b.bounceOff(br);
                    br.takeHit(b);
                } else if (b.isPiercing()) {
                    int hp = br.getHitPoints();
                    if (hp <= 2) {
                        br.destroy(); // phá gạch hoàn toàn
                         // xuyên qua, không đổi hướng
                    } else {
                        b.bounceOff(br);
                        br.reduceHp(2);
                        // trừ 2 HP
                        // Sau đó bật lại như bình thường
                    }
                }

                if (br.isDestroyed()) {
                    int cx = (int) (br.getX() + br.getWidth() / 2.0);
                    int cy = (int) (br.getY() + br.getHeight() / 2.0);
                    this.score += 10;
                    powerUpManager.maybeDropAt(cx, cy);
                    it.remove();
                }
            }

            // Va chạm với paddle
            if (b.checkCollision(paddle)) {
                b.bounceOff(paddle);
            }

            // Cập nhật vị trí bóng
            b.update(deltaTime, 0, screenWidth);

            // Nếu bóng rơi khỏi đáy -> remove
            if (b.getY() > screenHeight) {
                bit.remove();
            }
        }

        // Other objects
        for (GameObject obj : objects) {
            obj.update(deltaTime, 0, screenWidth);
        }

        paddle.update(deltaTime, 0, screenWidth);
        for (Brick brick : bricks) {
            brick.update(deltaTime, 0, screenWidth);
        }


        // Nếu không còn bóng => mất một mạng (hoặc gameover tuỳ logic)
        if (balls.isEmpty()) {
            lives--;
            if (lives > 0) {
                SoundManager.lostLive();
                // reset lại 1 bóng gắn paddle và không start
                ensureSingleBallAttachedToPaddle();
                gameStarted = false;
            } else {
                isGameOver = true;
                gameOver();
                return;
            }
        }

        boolean check = true;
        for (Brick br : bricks) {
            if (!(br instanceof unBreakBrick)) {
                check = false;
            }
        }
        if (check) {
            currentLevel++;
            loadCurrentMap();
            return;
        }

        // PowerUp: rơi -> nhặt -> (nếu có thời gian) đếm lùi
        powerUpManager.update(deltaTime, paddle);

        elapsedTime += deltaTime;
        timeRemaining = RESET_INTERVAL - elapsedTime;

        if (currentLevel == 6 && timeRemaining <= 0) {
            elapsedTime = 0;
            timeRemaining = RESET_INTERVAL;
            System.out.println("Reset lại map sau 3 phút!");
            loadCurrentMap(); // reset map + gạch
        }
    }

    private String formatTime(double seconds) {
        int total = (int) Math.max(seconds, 0);
        int minutes = total / 60;
        int secs = total % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    //render all object every frame
    public void render() {
        if(isPaused) return;
        renderer.clear(screenWidth, screenHeight);
        if (BackGround != null) {
            renderer.drawBackground(BackGround, screenWidth, screenHeight);
        }
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

        gc.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 20));
        gc.setFill(silverGradient);
        gc.setLineWidth(2);
        gc.fillText("Score: " + score, 600, 30);
        gc.fillText("Lives: ", 30, 30);

        int start1 = 115;
        for (int i = 1; i <= lives; i++) {
            renderer.getGc().drawImage(imageLife, start1, 15, 20, 20);
            start1 += 25;
        }

// === Hiển thị timer cho map 6 ===
        if (currentLevel == 6) {
            String timerText = formatTime(timeRemaining);

            // Khi còn <= 10 giây thì chữ nhấp nháy đỏ
            if (timeRemaining <= 11) {
                // Nhấp nháy mỗi giây (sáng tắt xen kẽ)
                if (((int) timeRemaining) % 2 == 0)
                    gc.setFill(Color.RED);
                else
                    gc.setFill(silverGradient);
            } else {
                gc.setFill(silverGradient);
            }

            gc.setEffect(glow);

            // Canh giữa chính xác (sử dụng đo độ rộng text)
            Text tempText = new Text(timerText);
            tempText.setFont(gc.getFont());
            double textWidth = tempText.getLayoutBounds().getWidth();
//
            gc.fillText(timerText, (screenWidth - textWidth) / 2, 40);
            gc.setEffect(null);
        }

        if (currentLevel == 7) {
            gc.save();                     // Lưu trạng thái hiện tại
            gc.translate(0, screenHeight); // Dịch gốc tọa độ xuống đáy màn hình
            gc.scale(1, -1);               // Lật ngược trục Y (trên <-> dưới)
        }

        for (Brick brick : bricks) {
            renderer.draw(brick);
        }

        // Vẽ tất cả bóng
        for (Ball b : balls) {
            renderer.draw(b);
        }

        // Vẽ paddle
        renderer.draw(paddle);

        // Vẽ item power-up đang rơi
        powerUpManager.render(gc);

        if (currentLevel == 7) {
            gc.restore(); // Trả lại trạng thái bình thường
        }
    }

    //current movement
    public void handleInput() {
        if (spacePressed) {
            // Space: bắn bóng nếu chưa bắn, hoặc nhảy qua nếu đã bắn
            if (!gameStarted) {
                gameStarted = true;
                // nếu chỉ có 1 bóng thì cho nó bật lên 1 lần (giữ hướng mặc định)
                if (!balls.isEmpty()) {
                    Ball main = balls.get(0);
                    // nếu muốn set velocity mặc định khi bắn, có thể set ở đây
                    // ví dụ main.setDirectionX(0.2); main.setDirectionY(-0.8);
                }
            }
        }

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

        //save balls
        if (!balls.isEmpty()) {
            for(int i = 0; i < balls.size(); i++){
                curState.balls.add(balls.get(i));
            }

        }

        //save paddle state
        curState.setPaddleX(paddle.getX());
        curState.setPaddleY(paddle.getY());
        curState.setPaddleSpeed(paddle.getSpeed());

        //save lives and score
        curState.setLives(lives);
        curState.setScore(score);

        //save current level
        curState.setLevel(currentLevel);

        //save brick state
        for(Brick brick : bricks){
            curState.bricks.add(new GameState.BrickState(brick.getX(), brick.getY(), brick.isDestroyed(), brick.getHitPoints()));
        }

        //save powerUp
        //curState.setPowerUpManager(powerUpManager);

        //save to json file in user's home directory
        String userHome = System.getProperty("user.home");
        File saveDir = new File(userHome, "ArkanoidSave");
        if (!saveDir.exists()) saveDir.mkdirs(); // tạo thư mục nếu chưa có

        File saveFile = new File(saveDir, "save.json");

        try (FileWriter writer = new FileWriter(saveFile)) {
            Gson gson = new Gson();
            gson.toJson(curState, writer);
            System.out.println("Game saved to: " + saveFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Load Game
    public void loadGame(String fileName){
        String userHome = System.getProperty("user.home");
        File saveFile = new File(userHome, "ArkanoidSave/save.json");

        if (!saveFile.exists()) {
            System.out.println("⚠Save file not found: " + saveFile.getAbsolutePath());
            return;
        }

        try (FileReader reader = new FileReader(saveFile)) {
            Gson gson = new Gson();
            GameState state = gson.fromJson(reader, GameState.class);

            //load ball (đặt lại 1 bóng chính theo file)
            ensureSingleBallAttachedToPaddle();
            balls.clear();
            for(int i = 0; i < state.balls.size(); i++){
                balls.add(state.balls.get(i));
            }

            //load powerUpManager
            powerUpManager = new PowerUpManager(screenHeight);
            powerUpManager.setBallSink(balls::add);
            powerUpManager.setBallsSupplier(() -> balls);
            powerUpManager.setFireBallSkin("/Image/PowerUp/ballfire.png");
//            powerUpManager.setBallSink(balls::add);
//            powerUpManager.setBallsSupplier(() -> balls);

            //load level
            currentLevel = state.getLevel();

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
        if (key == KeyCode.SPACE) spacePressed = true; // SPACE để bắt đầu
        if (key == KeyCode.ESCAPE){
            togglePause();
            mainApp.showPauseMenu();
        }
        System.out.println("Pressed: " + key);

    }

    public void onKeyReleased(KeyCode key) {
        if (key == KeyCode.LEFT)  leftPressed = false;
        if (key == KeyCode.RIGHT) rightPressed = false;
        if (key == KeyCode.SPACE) spacePressed = false; // nhả SPACE -> false

    }

    public void checkCollisions() {}
    public void gameOver() {
        SoundManager.stopMusic(SoundManager.MPlayer);
        SoundManager.GameOver();
        System.out.println("GAME OVER");
        SoundManager.playBackgroundMusic(true);
        // tuỳ bạn: reset level, show menu,...
        if(stage != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/GameOver.fxml"));
                Scene menuScene = new Scene(loader.load());
                MenuController ct = loader.getController();
                ct.setScore(this.score);
                ct.setMainApp(this.mainApp);
                stage.setScene(menuScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Pause game
    public void togglePause() {
        isPaused = !isPaused;
    }

    public void pauseGame() {
        isPaused = true;
        //SoundManager.pauseMusic();
        System.out.println("Game paused");
    }

    public void resumeGame() {
        isPaused = false;
        //SoundManager.resumeMusic();

        System.out.println("Game resumed");
    }

    public boolean isPaused() {
        return isPaused;
    }

}
