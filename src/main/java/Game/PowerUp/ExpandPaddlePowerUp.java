package Game.PowerUp;


import Game.Paddle;
import javafx.scene.image.Image;

public class ExpandPaddlePowerUp extends PowerUp {
    private final double factor = 1.5;  // hệ số phóng to
    private boolean isActive = false;
    private int originalWidth;
    private int originalHeight;

    private static final transient Image IMG = new Image(
            ExpandPaddlePowerUp.class.getResource("/Image/PowerUp/expand.png").toExternalForm()
    );

    public ExpandPaddlePowerUp(int x, int y) {
        super(x, y, 24, 24, "ExpandPaddle", 10);
        this.image = IMG;
    }

    @Override
    public void applyEffect(Paddle paddle) {
        if (isActive) return;
        isActive = true;

        // Lưu lại kích thước gốc
        originalWidth  = paddle.getWidth();
        originalHeight = paddle.getHeight();

        // Tâm hiện tại của paddle
        double cx = paddle.getX() + originalWidth  / 2.0;
        double cy = paddle.getY() + originalHeight / 2.0;

        // Kích thước mới (1.5x)
        int newWidth  = (int) Math.round(originalWidth  * factor);
        int newHeight = (int) Math.round(originalHeight * factor);

        // Đặt kích thước mới
        paddle.setWidth(newWidth);
        paddle.setHeight(newHeight);

        // Giữ tâm: x,y dịch theo nửa kích thước mới
        paddle.setX(cx - newWidth  / 2.0);
        paddle.setY(cy - newHeight / 2.0);

        // Clamp để không vượt màn (nếu Paddle có phương thức clampToScreen)
        paddle.clampToScreen();
    }

    @Override
    public void removeEffect(Paddle paddle) {
        if (!isActive) return;
        isActive = false;

        // Giữ nguyên tâm hiện tại, thu về kích thước gốc
        double cx = paddle.getX() + paddle.getWidth()  / 2.0;
        double cy = paddle.getY() + paddle.getHeight() / 2.0;

        paddle.setWidth(originalWidth);
        paddle.setHeight(originalHeight);

        paddle.setX(cx - originalWidth  / 2.0);
        paddle.setY(cy - originalHeight / 2.0);

        paddle.clampToScreen();
    }
}