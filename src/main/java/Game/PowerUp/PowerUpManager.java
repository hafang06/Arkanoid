package Game.PowerUp;

import Game.Paddle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Quản lý:
 *  - Spawn PowerUp khi gạch bị phá (theo tỉ lệ, max 3/màn).
 *  - Danh sách item đang rơi (update + render).
 *  - Nhặt item khi va chạm với Paddle -> applyEffect().
 *  - Theo dõi thời gian hiệu lực -> removeEffect() khi hết.
 */
public class PowerUpManager {

    // ===== Cấu hình tỉ lệ rơi =====
    private double FASTBALL_DROP_RATE = 0.20;    // 20%
    private double EXPAND_DROP_RATE  = 0.20;     // 20%
    private int MAX_DROPS_PER_LEVEL  = 3;        // tối đa 3 item/màn

    private final Random rng = new Random();

    // Items đang rơi trên màn
    private final List<PowerUp> items = new ArrayList<>();

    // Hiệu ứng đang hoạt động trên Paddle
    private final List<ActiveEffect> active = new ArrayList<>();

    // Giới hạn theo màn
    private int spawnedThisLevel = 0;

    // Nếu nhặt trùng loại khi đang hoạt động -> refresh thời gian (không stack)
    private boolean refreshDurationOnSameType = true;

    /** Gói hiệu ứng để track thời gian còn lại */
    private static class ActiveEffect {
        final PowerUp instance;
        double timeLeft;
        ActiveEffect(PowerUp p) {
            this.instance = p;
            this.timeLeft = p.getDuration();
        }
    }

    // ===== API =====

    /** Gọi khi bắt đầu màn mới / đổi Map. */
    public void resetLevel() {
        spawnedThisLevel = 0;
        items.clear();
        active.clear();
    }

    /** Gọi ngay khi một gạch bị phá để xét rơi PowerUp tại vị trí (tâm gạch). */
    public void onBrickDestroyed(double brickCenterX, double brickCenterY) {
        if (spawnedThisLevel >= MAX_DROPS_PER_LEVEL) return;

        double roll = rng.nextDouble(); // [0,1)
        PowerUp toSpawn = null;

        if (roll < FASTBALL_DROP_RATE) {
            toSpawn = new FastBallPowerUp((int) brickCenterX, (int) brickCenterY);
        } else if (roll < FASTBALL_DROP_RATE + EXPAND_DROP_RATE) {
            toSpawn = new ExpandPaddlePowerUp((int) brickCenterX, (int) brickCenterY);
        }

        if (toSpawn != null) {
            items.add(toSpawn);
            spawnedThisLevel++;
        }
    }

    /** Update mỗi frame: items rơi + nhặt + hiệu ứng đang hoạt động. */
    public void update(double deltaTime, Paddle paddle, int screenWidth, int screenHeight) {
        // Items rơi
        Iterator<PowerUp> it = items.iterator();
        while (it.hasNext()) {
            PowerUp p = it.next();
            p.update(deltaTime, 0, screenWidth);

            // Va chạm với Paddle -> nhặt
            if (intersects(p, paddle)) {
                ActiveEffect same = findActiveByType(p.getType());
                if (same != null && refreshDurationOnSameType) {
                    same.timeLeft = Math.max(same.timeLeft, p.getDuration());
                } else {
                    p.applyEffect(paddle);
                    active.add(new ActiveEffect(p));
                }
                it.remove();
                continue;
            }

            // Rơi ra khỏi màn -> xoá
            if (p.getY() > screenHeight) {
                it.remove();
            }
        }

        // Hiệu ứng đang hoạt động -> đếm lùi và remove khi hết
        Iterator<ActiveEffect> ai = active.iterator();
        while (ai.hasNext()) {
            ActiveEffect ae = ai.next();
            ae.timeLeft -= deltaTime;
            if (ae.timeLeft <= 0) {
                ae.instance.removeEffect(paddle);
                ai.remove();
            }
        }
    }

    /** Vẽ item rơi đơn giản (màu + nhãn) */
    public void render(GraphicsContext gc) {
        for (PowerUp p : items) {
            if ("FastBall".equals(p.getType())) {
                gc.setFill(Color.ORANGE);
            } else if ("ExpandPaddle".equals(p.getType())) {
                gc.setFill(Color.CYAN);
            } else {
                gc.setFill(Color.LIGHTGRAY);
            }
            gc.fillRect(p.getX(), p.getY(), p.getWidth(), p.getHeight());
            gc.setStroke(Color.BLACK);
            gc.strokeRect(p.getX(), p.getY(), p.getWidth(), p.getHeight());

            gc.setFill(Color.BLACK);
            String label = "FastBall".equals(p.getType()) ? "F" :
                    "ExpandPaddle".equals(p.getType()) ? "E" : "?";
            gc.fillText(label, p.getX() + 6, p.getY() + 14);
        }
    }

    // ===== Helpers =====

    /** AABB paddle vs powerup */
    private boolean intersects(PowerUp p, Paddle paddle) {
        double px = paddle.getX(), py = paddle.getY();
        int pw = paddle.getWidth(), ph = paddle.getHeight();

        double x = p.getX(), y = p.getY();
        int w = p.getWidth(), h = p.getHeight();

        return (px < x + w) && (px + pw > x) && (py < y + h) && (py + ph > y);
    }

    private ActiveEffect findActiveByType(String type) {
        for (ActiveEffect ae : active) {
            if (ae.instance.getType().equals(type)) return ae;
        }
        return null;
    }

    // ===== Tuỳ chỉnh lúc runtime (không bắt buộc) =====
    public void setDropRates(double fastBallRate, double expandRate) {
        this.FASTBALL_DROP_RATE = fastBallRate;
        this.EXPAND_DROP_RATE = expandRate;
    }
    public void setMaxDropsPerLevel(int max) { this.MAX_DROPS_PER_LEVEL = max; }
    public void setRefreshDurationOnSameType(boolean v) { this.refreshDurationOnSameType = v; }
}