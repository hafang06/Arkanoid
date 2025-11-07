package Game.PowerUp;

import Game.Paddle;

public class ExpandPaddlePowerUp extends PowerUp{
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
        paddle.setWidth(originalWidth + deltaWidth);

        if (paddle.getX() < 0) paddle.setX(0);
        if (paddle.getX() + paddle.getWidth() > 800) paddle.setX(800 - paddle.getWidth());
    }

    @Override
    public void removeEffect(Paddle paddle) {
        if (!isActive) return;
        isActive = false;

        paddle.setWidth(originalWidth);

        if (paddle.getX() < 0) paddle.setX(0);
        if (paddle.getX() + paddle.getWidth() > 800) paddle.setX(800 - paddle.getWidth());
    }
}