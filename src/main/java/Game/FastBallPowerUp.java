package Game;

public class FastBallPowerUp extends PowerUp {
    public FastBallPowerUp(int x, int y) {
        super(x, y, 20, 20, "FastBall", 10);
    }

    @Override
    public void applyEffect(Paddle paddle) {}
    @Override
    public void removeEffect(Paddle paddle) {}
}
