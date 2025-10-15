package Game;
import Game.Brick.Brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.geometry.Rectangle2D;

public class Ball extends MovableObject {
    private int speed;
    private double  directionX, directionY;

    public Ball(int x, int y, int size, int speed) {
        super(x, y, size, size, 0, 0);
        this.speed = speed;
        this.directionX = 1;
        this.directionY = -1;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public double getDirectionX() {
        return directionX;
    }

    public void setDirectionX(double directionX) {
        this.directionX = directionX;
    }

    public double getDirectionY() {
        return directionY;
    }

    public void setDirectionY(double directionY) {
        this.directionY = directionY;
    }

    public void bounceOff(GameObject other) {
        if (other instanceof Paddle) {
            Paddle paddle = (Paddle) other;

            double paddleCenter = paddle.x + paddle.width / 2.0;
            double ballCenter = this.x + this.width / 2.0;

            double relativeIntersect = (ballCenter - paddleCenter) / (paddle.width / 2.0);
            relativeIntersect = Math.max(-1.0, Math.min(1.0, relativeIntersect));

            double maxBounceAngle = Math.toRadians(75);
            double bounceAngle = relativeIntersect * maxBounceAngle;

            directionX = Math.sin(bounceAngle);
            directionY = -Math.cos(bounceAngle);

            y = paddle.y - height - 1;

            return;
        }
        else if (other instanceof Brick) {
            double ballCenterX = x + width / 2.0;
            double ballCenterY = y + height / 2.0;
            double brickCenterX = other.x + other.width / 2.0;
            double brickCenterY = other.y + other.height / 2.0;

            double dx = ballCenterX - brickCenterX;
            double dy = ballCenterY - brickCenterY;

            double overlapX = (width / 2.0 + other.width / 2.0) - Math.abs(dx);
            double overlapY = (height / 2.0 + other.height / 2.0) - Math.abs(dy);

            if (overlapX < overlapY) {
                directionX *= -1;
                if (dx > 0) x += overlapX + 0.1;
                else x -= overlapX + 0.1;
            } else {
                directionY *= -1;
                if (dy > 0) y += overlapY + 0.1;
                else y -= overlapY + 0.1;
            }

            return;
        }
        else {
            directionY *= -1;
        }
    }

    //Kiem tra va cham vs cac vat the khac
    public boolean checkCollision(GameObject other) {
        //Lay tam qua bong
        double ballCenterX = x + width / 2.0;
        double ballCenterY = y + height / 2.0;

        //Lay vi tri gan nhat cua doi tuong so sanh vs ball
        double nearestX = Math.max(other.x, Math.min(ballCenterX, other.x + other.width));
        double nearestY = Math.max(other.y, Math.min(ballCenterY, other.y + other.height));

        double dx = ballCenterX - nearestX;
        double dy = ballCenterY - nearestY;

        //Kiem tra khoang cach neu co va cham thi khoang cach nho hon ban kinh
        return (dx * dx + dy * dy) < (width / 2.0) * (width / 2.0);
    }

    @Override
    // Cap nhat vi tri sau moi frame
    public void update(double deltaTime) {
        move();
    }

    @Override
    // ve hinh qua bong
    public void render(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillOval(x, y, width, height);
        gc.setStroke(Color.GRAY);
        gc.strokeOval(x, y, width, height);
    }

    @Override
    public void move() {
        // Cập nhật vị trí theo hướng và tốc độ
        x += directionX * speed;
        y += directionY * speed;

        if (x <= 0) {
            x = 0;
            directionX *= -1;
        }
        //them sau khi co screen width
        else if (x + width >= 800) {
            x = 800 - width;
            directionX *= -1;
        }

        if (y <= 0) {
            y = 0;
            directionY *= -1;
        }
    }
}