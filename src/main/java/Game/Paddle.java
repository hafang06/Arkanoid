package Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Paddle extends MovableObject {
    private int speed;
    private PowerUp currentPowerUp;

    /**
     * Phuong thuc khoi tao
     */
    public Paddle(int x, int y, int width, int height, int speed) {
        super(x, y, width, height, 0, 0);
        this.speed = speed;
    }

    // Di chuyen sang trai
    public void moveLeft() {
        dx -= speed;
    }

    // Di chuyen sang phai
    public void moveRight() {
        dx += speed;
    }

    // Ngung di chuyen
    public void stop() {
        dx = 0;
    }
    public void applyPowerUp(PowerUp p) {}

    @Override
    // cap nhat vi tri
    public void update(double deltaTime) {
        move();
    }
    @Override
    // ve qua bong
    public void render(GraphicsContext gc) {
        gc.setFill(Color.DEEPSKYBLUE);
        gc.fillRect(x, y, width, height);
    }
    @Override
    // cap nhat vi tri thanh paddle
    public void move() {
        x += dx;
        if ( x < 0) x = 0;
        // Bo sung sau khi da co chieu dai man hinh
//        if (x + width > Game.Width) {
//            x = Game.Width - width;
//        }
    }
}