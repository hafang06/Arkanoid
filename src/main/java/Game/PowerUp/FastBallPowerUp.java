package Game.PowerUp;

import Game.Ball;
import Game.Paddle;
import javafx.scene.image.Image;

public class FastBallPowerUp extends PowerUp {
    private final int deltaSpeed = 1;
    private boolean isActive = false;
    private int originalSpeed;

    private static final transient Image IMG = new Image(
            FastBallPowerUp.class.getResource("/Image/PowerUp/fastball.png").toExternalForm()
    );

    public FastBallPowerUp(int x, int y) {
        super(x, y, 24, 24, "FastBall", 10);
        this.image = IMG;
    }

    @Override
    public void applyEffect(Paddle paddle) {
        if (isActive) return;              // đang chạy -> bỏ qua
        Ball ball = paddle.getBall();
        if (ball == null) return;
        isActive = true;
        originalSpeed = ball.getSpeed();
        ball.setSpeed(originalSpeed + deltaSpeed);
    }

    @Override
    public void removeEffect(Paddle paddle) {
        if (!isActive) return;
        isActive = false;
        Ball ball = paddle.getBall();
        if (ball != null) {
            ball.setSpeed(originalSpeed); // trả về đúng giá trị trước khi áp
        }
    }
}