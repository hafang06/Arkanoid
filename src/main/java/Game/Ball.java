package Game;

import Game.Brick.Brick;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Ball extends MovableObject {
    private int speed;
    private double directionX, directionY;
    private transient Image image;
    private boolean piercing;

    private static transient Image IMG_NORMAL;
    private static transient Image IMG_FIRE;

    static {
        try {
            IMG_NORMAL = new Image(Ball.class.getResourceAsStream("/Image/ball.png"));
        } catch (Exception ignored) {}
        try {
            IMG_FIRE = new Image(Ball.class.getResourceAsStream("/Image/PowerUp/ballfire.png"));
        } catch (Exception ignored) {}
    }

    public Ball(double x, double y, int size, int speed, boolean piercing) {
        super(x, y, size, size, 0, 0);
        this.speed = speed;
        this.directionX = 0.5;
        this.directionY = -1;
        this.piercing = false;
        this.image = (IMG_NORMAL != null) ? IMG_NORMAL : null;

        double len = Math.sqrt(directionX * directionX + directionY * directionY);
        directionX /= len;
        directionY /= len;

    }

    public static void setGlobalNormalSkin(Image img) { if (img != null) IMG_NORMAL = img; }
    public static void setGlobalFireSkin(Image img)   { if (img != null) IMG_FIRE   = img; }

    public void setSkinNormal() { if (IMG_NORMAL != null) this.image = IMG_NORMAL; }
    public void setSkinFire()   { if (IMG_FIRE   != null) this.image = IMG_FIRE;   }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }

    public double getDirectionX() { return directionX; }
    public void setDirectionX(double directionX) { this.directionX = directionX; }

    public double getDirectionY() { return directionY; }
    public void setDirectionY(double directionY) { this.directionY = directionY; }

    public boolean isPiercing() { return piercing; }
    public void setPiercing(boolean v) { this.piercing = v; }

    public void bounceOff(GameObject other) {
        if (other instanceof Paddle) {
            Paddle paddle = (Paddle) other;
            double paddleCenter = paddle.x + paddle.width / 2.0;
            double ballCenter   = this.x + this.width / 2.0;

            double relativeIntersect = (ballCenter - paddleCenter) / (paddle.width / 2.0);
            relativeIntersect = Math.max(-1.0, Math.min(1.0, relativeIntersect));

            double maxBounceAngle = Math.toRadians(75);
            double bounceAngle = relativeIntersect * maxBounceAngle;

            directionX = Math.sin(bounceAngle);
            directionY = -Math.cos(bounceAngle);

            double len = Math.sqrt(directionX * directionX + directionY * directionY);
            directionX /= len;
            directionY /= len;

            y = paddle.y - height - 1;
            return;
        }
        else if (other instanceof Brick) {
            Brick brick = (Brick) other;

//

            // Xử lý bật lại
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
                else        x -= overlapX + 0.1;
            } else {
                directionY *= -1;
                if (dy > 0) y += overlapY + 0.1;
                else        y -= overlapY + 0.1;
            }

            return;
        }
        else {
            directionY *= -1;
        }
    }

    public boolean checkCollision(GameObject other) {
        double ballCenterX = x + width / 2.0;
        double ballCenterY = y + height / 2.0;

        double nearestX = Math.max(other.x, Math.min(ballCenterX, other.x + other.width));
        double nearestY = Math.max(other.y, Math.min(ballCenterY, other.y + other.height));

        double dx = ballCenterX - nearestX;
        double dy = ballCenterY - nearestY;

        return (dx * dx + dy * dy) < (width / 2.0) * (width / 2.0);
    }

    @Override
    public void update(double deltaTime, int leftWall, int rightWall) {
        move(leftWall, rightWall);
    }

    @Override
    public void render(GraphicsContext gc) {
        double scale = 1.5;
        double drawWidth = width * scale;
        double drawHeight = height * scale;

        if (image != null) {
            gc.drawImage(image, x, y, drawWidth, drawHeight);
        } else {
            gc.setFill(Color.WHITE);
            gc.fillOval(x, y, drawWidth, drawHeight);
            gc.setStroke(Color.GRAY);
            gc.strokeOval(x, y, drawWidth, drawHeight);
        }
    }

    @Override
    public void move(int leftWall, int rightWall) {
        x += directionX * speed;
        y += directionY * speed;

        if (x <= leftWall) {
            x = leftWall;
            directionX *= -1;
            x += directionX * speed;
        } else if (x + width >= rightWall) {
            x = rightWall - width;
            directionX *= -1;
            x += directionX * speed;
        }

        if (y <= 0) {
            y = 0;
            directionY *= -1;
            y += directionY * speed;
        }
    }
}