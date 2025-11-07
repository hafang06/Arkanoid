package Game.PowerUp;

import Game.Paddle;
import javafx.scene.image.Image;

public class MultiBallPowerUp extends PowerUp {
    private boolean executed = false;
    private final PowerUpManager manager;

    private static final Image IMG = new Image(
            MultiBallPowerUp.class.getResource("/Image/PowerUp/multiball.png").toExternalForm()
    );

    public MultiBallPowerUp(int x, int y, PowerUpManager manager) {
        super(x, y, 24, 24, "MultiBall", 0.0); // instant, không có thời gian
        this.image = IMG;
        this.manager = manager;
    }

    @Override public boolean isInstant()   { return true; }   // áp dụng ngay
    @Override public boolean isStackable() { return true; }   // cho phép nhặt nhiều lần

    @Override
    public void applyEffect(Paddle paddle) {
        if (executed) return;      // tránh chạy đôi trong cùng frame
        executed = true;

        if (manager != null) {
            // Nhân đôi TẤT CẢ bóng hiện có: mỗi bóng tạo thêm 2 bóng lệch ±45°
            manager.replicateAllBalls(Math.toRadians(45));
        }
    }

    @Override
    public void removeEffect(Paddle paddle) {
        // instant -> không có remove
    }
}