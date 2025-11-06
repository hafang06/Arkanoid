package Game;

public class ExpandPaddlePowerUp extends PowerUp {
    private final int deltaWidth = 40;
    private boolean isActive = false;
    private int originalWidth; // lưu width gốc

    public ExpandPaddlePowerUp(int x, int y) {
        super(x, y, 20, 20, "ExpandPaddle", 10); // kích thước 20x20, 10s
    }

    @Override
    public void applyEffect(Paddle paddle) {
        if (isActive) return;
        isActive = true;

        originalWidth = paddle.getWidth();
        paddle.width = originalWidth + deltaWidth;

        if (paddle.x < 0) paddle.x = 0;
        if (paddle.x + paddle.width > 800) paddle.x = 800 - paddle.width;
    }

    @Override
    public void removeEffect(Paddle paddle) {
        if (!isActive) return;
        isActive = false;

        paddle.width = originalWidth;

        if (paddle.x < 0) paddle.x = 0;
        if (paddle.x + paddle.width > 800) paddle.x = 800 - paddle.width;
    }
}