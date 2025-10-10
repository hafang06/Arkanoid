package Game;

public class ExpandPaddlePowerUp extends PowerUp {
    public ExpandPaddlePowerUp(int x, int y) {
        super(x, y, 20, 20, "ExpandPaddle", 10);
    }

    @Override
    public void applyEffect(Paddle paddle) {}
    @Override
    public void removeEffect(Paddle paddle) {}
}