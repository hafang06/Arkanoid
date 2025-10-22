package Game;
import Game.Brick.Brick;
import Game.Brick.unBreakBrick;
import Game.Map.Map;
import Game.Map.Map1;
import Game.Map.Map2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.util.ArrayList;
import java.util.List;

public class GameManager2Player {
    public static final int screenWidth = 800;
    public static final int screenHeight = 600;
    private Renderer renderer;

    private List<GameObject> objects1 = new ArrayList<>();
    private List<GameObject> objects2 = new ArrayList<>();
    private Paddle paddle1;
    private Paddle paddle2;
    private Ball ball1;
    private Ball ball2;
    private List<Brick> brick1 = new ArrayList<>();
    private List<Brick> brick2 = new ArrayList<>();
    private List<PowerUp> powerUps;
    private int score;
    private int lives;
    private List<Map> mapPlayer1=new ArrayList<>();
    private List<Map> mapPlayer2=new ArrayList<>();
    private Image image = new Image(getClass().getResourceAsStream("/Image/anhnenBattle.jpg"));
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean spacePressed = false;
    private boolean gameStarted = false;
    private boolean APressed = false;
    private boolean DPressed = false;
    private boolean EnterPressed = false;

    public GameManager2Player(GraphicsContext gc) {
        renderer = new Renderer(gc);
        paddle1 = new Paddle(screenWidth / 4 - 50, screenHeight - 40, 100, 20, 4);
        paddle2 = new Paddle((3*screenWidth) / 4 - 50, screenHeight - 40, 100, 20, 4);
        //ball = new Ball();
        int ballSize = 15;
        double ballX1 = paddle1.getX() + paddle1.getWidth() / 2 - ballSize / 2;
        double ballY1 = paddle1.getY() - ballSize - 2; // đặt ngay trên paddle, cách 2px

        ball1 = new Ball(ballX1, ballY1, ballSize, 4);
        double ballX2 = paddle2.getX() + paddle2.getWidth() / 2 - ballSize / 2;
        double ballY2 = paddle2.getY() - ballSize - 2; // đặt ngay trên paddle, cách 2px

        ball1 = new Ball(ballX1, ballY1, ballSize, 4);
        ball2 = new Ball(ballX2,ballY2,ballSize,4);

        Map1 leftMap = new Map1();
        Map1 rightMap = new Map1();

        // Thu nhỏ map về nửa màn hình (từ 800 → 400)
        for (Brick b : leftMap.getBricks()) {
            b.setX(b.getX() * 0.5); // scale nửa kích thước
        }

        for (Brick b : rightMap.getBricks()) {
            b.setX(b.getX() * 0.5 + screenWidth / 2 + 3); // scale + dịch sang phải
        }

        brick1.addAll(leftMap.getBricks());
        brick2.addAll(rightMap.getBricks());

    }

    public void onKeyPressed(KeyCode key) {
        if (key == KeyCode.LEFT) leftPressed = true;
        if (key == KeyCode.RIGHT) rightPressed = true;
        if (key == KeyCode.A) APressed = true;
        if (key == KeyCode.D) DPressed = true;
        if(key == KeyCode.TAB) spacePressed = true;
    }

    public void onKeyReleased (KeyCode key) {
        if (key == KeyCode.LEFT) leftPressed = false;
        if (key == KeyCode.RIGHT) rightPressed = false;
        if (key == KeyCode.A) APressed = false;
        if (key == KeyCode.D) DPressed = false;
        if(key == KeyCode.TAB) spacePressed = false;
        if(key == KeyCode.SPACE) spacePressed = true;
    }

    public void handleInput() {
        if(spacePressed) gameStarted = true;
        if (leftPressed) {
            paddle2.moveLeft();
        } else if (rightPressed) {
            paddle2.moveRight();
        } else {
            paddle2.stop();
        }

        if (APressed) {
            paddle1.moveLeft();
        } else if (DPressed) {
            paddle1.moveRight();
        } else {
            paddle1.stop();
        }

    }
    public void checkBrick(double deltaTime, List<Brick> bricks, Ball ball) {
        for(Brick brick : bricks){
            brick.update(deltaTime,0,screenWidth);
        }

        //check if ball is touching any brick
        int destroyedBrick = -1;
        int index = 0;
        for(Brick br : bricks){
            if(ball.checkCollision(br)){
                ball.bounceOff(br);

                //if ball touched the brick then brick -1 hp
                br.takeHit(ball);
                if(br.isDestroyed()){
                    destroyedBrick = index;
                }
            }
            index++;
        }
        if(destroyedBrick != -1) bricks.remove(destroyedBrick);

    }
    public void updateGame (double deltaTime) {
        handleInput();
        if(!gameStarted){
            int ballSize = 15;
            paddle1.update(deltaTime,0,screenWidth/2);
            double x1 = paddle1.getX() + paddle1.getWidth() / 2 - ballSize / 2;;
            double y1 = paddle1.getY() - ballSize - 2;
            ball1 = new Ball(x1, y1, ballSize, 4);
            paddle2.update(deltaTime,screenWidth/2 + 3, screenWidth);
            double x2 = paddle2.getX() + paddle2.getWidth() / 2 - ballSize / 2;;
            double y2 = paddle2.getY() - ballSize - 2;
            ball2 = new Ball(x2, y2, ballSize, 4);
            return;
        }
        ball1.update(deltaTime, 0, screenWidth / 2);
        ball2.update(deltaTime, screenWidth / 2 + 3, screenWidth);
        checkBrick(deltaTime,brick1,ball1);
        checkBrick(deltaTime,brick2,ball2);
        for(GameObject obj : objects1){
            obj.update(deltaTime,0,screenWidth/2);
        }
        if(ball1.checkCollision(paddle1)){
            ball1.bounceOff(paddle1);
        }
        if(ball2.checkCollision(paddle2)) {
            ball2.bounceOff(paddle2);
        }
        paddle1.update(deltaTime,0,screenWidth/2);
        paddle2.update(deltaTime,screenWidth/2 + 3, screenWidth);
    }

    public void render() {
        renderer.clear(screenWidth, screenHeight);
        LinearGradient lineGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.BLUE),
                new Stop(1, Color.web("#eb1a2f"))
        );
        renderer.getGc().drawImage(image,0,0,screenWidth,screenHeight);
        renderer.getGc().setStroke(lineGradient);
        renderer.getGc().setLineWidth(3);
        renderer.getGc().strokeLine(screenWidth / 2.0, 0, screenWidth / 2.0, screenHeight);
        for (Brick brick : brick1) {
            renderer.draw(brick);
        }
        for (Brick brick : brick2) {
            renderer.draw(brick);
        }
        renderer.draw(ball1);
        renderer.draw(paddle1);
        renderer.draw(ball2);
        renderer.draw(paddle2);

    }
}
