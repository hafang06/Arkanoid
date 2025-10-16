package Game;
import Game.Brick.Brick;
import Game.Brick.unBreakBrick;
import Game.Map.Map;
import Game.Map.Map1;
import Game.Map.Map2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private final int screenWidth = 800;
    private final int screenHeight = 600;
    private Renderer renderer;

    private List<GameObject> objects = new ArrayList<>();
    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks = new ArrayList<>();
    private List<PowerUp> powerUps;
    private int score;
    private int lives;
    private List<Map> maps=new ArrayList<>();

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    //init renderer and paddle's position and size
    public GameManager(GraphicsContext gc) {
        renderer = new Renderer(gc);
        paddle = new Paddle(screenWidth / 2 - 50, screenHeight - 40, 100, 20, 4);
        //ball = new Ball();
        int ballSize = 15;
        double ballX = paddle.getX() + paddle.getWidth() / 2 - ballSize / 2;
        double ballY = paddle.getY() - ballSize - 2; // đặt ngay trên paddle, cách 2px

        ball = new Ball(ballX, ballY, ballSize, 6);


        //add demo bricks for testing
        maps.add(new Map1());
        maps.get(0).addBricks(bricks);
    }

    //update all object every frame
    public void updateGame(double deltaTime) {
        handleInput();
        for(GameObject obj : objects){
            obj.update(deltaTime);
        }
        for(Brick brick : bricks){
            brick.update(deltaTime);
        }



        //check if ball is touching any brick
        for(Brick brick : bricks){
            if(ball.checkCollision(brick)){
                ball.bounceOff(brick);

                //if ball touched the brick then brick -1 hp
                brick.takeHit(ball);
            }
        }

        //check touching the paddle
        if(ball.checkCollision(paddle)){
            ball.bounceOff(paddle);
        }

        ball.update(deltaTime);
        paddle.update(deltaTime);
    }

    //render all object every frame
    public void render() {
        renderer.clear(screenWidth, screenHeight);
        for (Brick brick : bricks) {
            renderer.draw(brick);
        }
        renderer.draw(ball);
        renderer.draw(paddle);
    }

    //current movement
    public void handleInput() {
        if (leftPressed) {
            paddle.moveLeft();
        } else if (rightPressed) {
            paddle.moveRight();
        } else {
            paddle.stop();
        }
    }



    public void onKeyPressed(KeyCode key) {
        if (key == KeyCode.LEFT) leftPressed = true;
        if (key == KeyCode.RIGHT) rightPressed = true;
    }

    public void onKeyReleased(KeyCode key) {
        if (key == KeyCode.LEFT) leftPressed = false;
        if (key == KeyCode.RIGHT) rightPressed = false;
    }

    public void checkCollisions() {}
    public void gameOver() {}
}
