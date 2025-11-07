package Game.PowerUp;

import Game.Ball;
import Game.Paddle;
import javafx.scene.image.Image;

import java.util.List;

public class FireballPowerUp extends PowerUp {
    private boolean isActive = false;

    private static final Image IMG = new Image(
            FireballPowerUp.class.getResource("/Image/PowerUp/fireball.png").toExternalForm()
    );

    public FireballPowerUp(int x, int y) {
        super(x, y, 24, 24, "Fireball", 6.0);
        this.image = IMG;
    }

    @Override
    public void applyEffect(Paddle paddle) {
        if (isActive) return;
        isActive = true;

        Ball mainBall = paddle.getBall();
        if (mainBall != null) {
            mainBall.setPiercing(true);
        }

        PowerUpManager manager = PowerUpManager.getInstance();
        if (manager != null && manager.getBallsSupplier() != null) {
            List<Ball> allBalls = manager.getBallsSupplier().get();
            for (Ball b : allBalls) {
                b.setSkinFire();
            }
        }
    }

    @Override
    public void removeEffect(Paddle paddle) {
        if (!isActive) return;
        isActive = false;

        Ball mainBall = paddle.getBall();
        if (mainBall != null) {
            mainBall.setPiercing(false);
        }

        PowerUpManager manager = PowerUpManager.getInstance();
        if (manager != null && manager.getBallsSupplier() != null) {
            List<Ball> allBalls = manager.getBallsSupplier().get();
            for (Ball b : allBalls) {
                b.setSkinNormal();
            }
        }
    }
}