package Game;
import Game.Brick.Brick;
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
    private List<Brick> bricks;
    private List<PowerUp> powerUps;
    private int score;
    private int lives;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    //init renderer and paddle's position and size
    public GameManager(GraphicsContext gc) {
        renderer = new Renderer(gc);
        paddle = new Paddle(screenWidth / 2 - 50, screenHeight - 40, 100, 20, 4);
        //ball = new Ball();
        int ballSize = 15;
        int ballX = paddle.getX() + paddle.getWidth() / 2 - ballSize / 2;
        int ballY = paddle.getY() - ballSize - 2; // đặt ngay trên paddle, cách 2px

        ball = new Ball(ballX, ballY, ballSize, 2);


        //add demo bricks for testing
        for(int i = 1; i <= 5; i++) {
            objects.add(new Brick(100 * i, 120, 3, Color.BLUE));
        }
    }

    //update all object every frame
    public void updateGame(double deltaTime) {
        handleInput();
        for(GameObject obj : objects){
            obj.update(deltaTime);
        }

        //check if ball is touching any brick
        for(GameObject obj : objects){
            if(ball.checkCollision(obj)){
                ball.bounceOff(obj);

                //if ball touched the brick then brick -1 hp
                if(obj instanceof Brick){
                    ((Brick) obj).takeHit(ball);
                }
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
        for (GameObject obj : objects) {
            renderer.draw(obj);
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
