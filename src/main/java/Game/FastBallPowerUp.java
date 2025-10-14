package Game;

public class FastBallPowerUp extends PowerUp {
    private final int deltaSpeed = 4;
    private boolean isActive = false;

    public FastBallPowerUp(int x, int y) {
        super(x, y, 20, 20, "FastBall", 10);
    }

    @Override
    public void applyEffect(Paddle paddle) {
        if (isActive) return;
        isActive = true;
        Ball ball = paddle.getBall();
        if (ball != null) {
            ball.setSpeed(ball.getSpeed() + deltaSpeed);
        }
    }

    @Override
    public void removeEffect(Paddle paddle) {
        if (!isActive) return;
        isActive = false;
        Ball ball = paddle.getBall();
        if (ball != null) {
            ball.setSpeed(Math.max(1, ball.getSpeed() - deltaSpeed));
        }
    }
}
