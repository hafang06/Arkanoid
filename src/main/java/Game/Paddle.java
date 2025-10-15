package Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Paddle extends MovableObject {
    private int speed;
    private PowerUp currentPowerUp;
    private Ball ball; // 👈 Thêm biến này để Paddle biết quả bóng nó đang tương tác

    public Paddle(int x, int y, int width, int height, int speed) {
        super(x, y, width, height, 0, 0);
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
        dx -= speed;
    }

    // Di chuyển sang phải
    public void moveRight() {
        dx += speed;
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
    public void update(double deltaTime) {
        move();
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.DEEPSKYBLUE);
        gc.fillRect(x, y, width, height);
    }

    @Override
    public void move() {
        x += dx;
        if (x < 0) x = 0;
        // Bổ sung sau khi có chiều rộng màn hình
        if (x + width > 800) x = 800 - width;
    }
}
