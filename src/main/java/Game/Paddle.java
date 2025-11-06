package Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Paddle extends MovableObject {
    private int speed;
    private PowerUp currentPowerUp;
    private Ball ball;// 👈 Thêm biến này để Paddle biết quả bóng nó đang tương tác
    private Image image;

    public Paddle(double x, double y, int width, int height, int speed) {
        super(x, y, width, height, 0, 0);
        this.speed = speed;
        image = new Image(getClass().getResourceAsStream("/Image/Paddle.png"));
    }


    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    // 👇 Thêm getter & setter cho ball
    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

    // Di chuyển sang trái
    public void moveLeft() {
        dx = -speed;
    }

    // Di chuyển sang phải
    public void moveRight() {
        dx = speed;
    }

    // Ngừng di chuyển
    public void stop() {
        dx = 0;
    }

    // Áp dụng PowerUp
    public void applyPowerUp(PowerUp p) {
        this.currentPowerUp = p;
        p.applyEffect(this);
    }

    @Override
    public void update(double deltaTime,int leftWall, int rightWall) {
        move(leftWall, rightWall);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width , height );

    }

    @Override
    public void move(int leftWall, int rightWall) {
        x += dx;
        if (x < leftWall) x = leftWall;
        // Bổ sung sau khi có chiều rộng màn hình
        if (x + width > rightWall ) x = rightWall - width;
    }
}
