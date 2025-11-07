package Game.PowerUp;

import Game.Ball;
import Game.Paddle;

public class FastBallPowerUp extends PowerUp{
    private final int deltaSpeed = 4;
    private boolean isActive = false;
    private int originalSpeed;        // lưu speed gốc

    public FastBallPowerUp(int x, int y) {
        super(x, y, 20, 20, "FastBall", 10);
    }

    @Override
    public void applyEffect(Paddle paddle) {
        if (isActive) return;
        Ball ball = paddle.getBall();
        if (ball == null) return;
        isActive = true;
        originalSpeed = ball.getSpeed();
        ball.setSpeed(originalSpeed + deltaSpeed);
    }

    @Override
    public void removeEffect(Paddle paddle) {
        if (!isActive) return;
        Ball ball = paddle.getBall();
        isActive = false;
        if (ball != null) {
            ball.setSpeed(originalSpeed);
        }
    }
}
