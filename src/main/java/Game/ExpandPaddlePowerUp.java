package Game;

public class ExpandPaddlePowerUp extends PowerUp {
    private final int deltaWidth = 40;
    private boolean isActive = false;

    public ExpandPaddlePowerUp(int x, int y) {
        super(x, y, 20, 20, "ExpandPaddle", 10); // kích thước 20x20, 10s
    }

    @Override
    public void applyEffect(Paddle paddle) {
        if (isActive) return;
        isActive = true;
        paddle.setWidth(paddle.getWidth() + deltaWidth);

    }

    @Override
    public void removeEffect(Paddle paddle) {
        if (!isActive) return;
        isActive = false;
        paddle.setWidth(Math.max(20, paddle.getWidth() - deltaWidth));
    }
}