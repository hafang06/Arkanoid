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
        paddle = new Paddle(screenWidth / 2 - 50, screenHeight - 40, 100, 20, 50);
        objects.add(paddle);
    }

    //update all object every frame
    public void updateGame(double deltaTime) {
        handleInput();
        for(GameObject obj : objects){
            obj.update(deltaTime);
        }
    }

    //render all object every frame
    public void render() {
        renderer.clear(screenWidth, screenHeight);
        for (GameObject obj : objects) {
            renderer.draw(obj);
        }
    }

    //current movement
    public void handleInput() {
        if(leftPressed) paddle.moveLeft();
        if(rightPressed) paddle.moveRight();
        else paddle.stop();
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
