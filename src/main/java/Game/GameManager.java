package Game;
import Game.Brick.Brick;
import Game.Brick.unBreakBrick;
import Game.Map.Map;
import Game.Map.Map1;
import Game.Map.Map2;
import Game.Map.Map3;
import Game.PowerUp.PowerUp;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;


public class GameManager {
    public static final int screenWidth = 800;
    public static final int screenHeight = 600;
    private Renderer renderer;

    private List<GameObject> objects = new ArrayList<>();
    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks = new ArrayList<>();
    private List<PowerUp> powerUps;
    private int score = 0;
    private int lives = 3;

    private List<Map> maps=new ArrayList<>();

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean spacePressed = false;
    private boolean gameStarted = false;//check if game started or not

    //init renderer and paddle's position and size
    public GameManager(GraphicsContext gc) {
        renderer = new Renderer(gc);
        paddle = new Paddle(screenWidth / 2 - 50, screenHeight - 40, 100, 20, 4);
        //ball = new Ball();
        int ballSize = 15;
        double ballX = paddle.getX() + paddle.getWidth() / 2 - ballSize / 2;
        double ballY = paddle.getY() - ballSize - 2; // đặt ngay trên paddle, cách 2px

        ball = new Ball(ballX, ballY, ballSize, 4);


        //add demo bricks for testing
        maps.add(new Map3());
        bricks=maps.get(0).getBricks();
    }

    //update all object every frame
    public void updateGame(double deltaTime) {
        handleInput();
        if(!gameStarted){
            int ballSize = 15;
            paddle.update(deltaTime,0,screenWidth);
            double x = paddle.getX() + paddle.getWidth() / 2 - ballSize / 2;;
            double y = paddle.getY() - ballSize - 2;
            ball = new Ball(x, y, ballSize, 4);
            return;
        }
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


        for(GameObject obj : objects){
            obj.update(deltaTime,0,screenWidth);
        }
        //check touching the paddle
        if(ball.checkCollision(paddle)){
            ball.bounceOff(paddle);
        }

            ball.update(deltaTime,0,screenWidth);
            paddle.update(deltaTime,0,screenWidth);
    }

    //render all object every frame
    public void render() {
        renderer.clear(screenWidth, screenHeight);
        renderer.getGc().drawImage(maps.get(0).getBackGround(), 0, 0, screenWidth, screenHeight);
        for (Brick brick : bricks) {
            renderer.draw(brick);
        }
        renderer.draw(ball);
        renderer.draw(paddle);
    }

    //current movement
    public void handleInput() {
        if(spacePressed) gameStarted = true;
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
        if (key == KeyCode.LEFT) leftPressed = true;
        if (key == KeyCode.RIGHT) rightPressed = true;
        if(key == KeyCode.TAB) spacePressed = true;
        if (key == KeyCode.S) saveGame("save.json");
        if (key == KeyCode.L) loadGame("save.json");
    }

    public void onKeyReleased(KeyCode key) {
        if (key == KeyCode.LEFT) leftPressed = false;
        if (key == KeyCode.RIGHT) rightPressed = false;
        if(key == KeyCode.SPACE) spacePressed = true;
    }

    public void checkCollisions() {}
    public void gameOver() {}
}
