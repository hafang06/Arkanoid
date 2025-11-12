package Game.PowerUp;

import Game.Ball;
import Game.GameManager;
import Game.Paddle;
import Game.SoundManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Quản lý vòng đời PowerUp: rơi -> nhặt -> áp dụng -> (nếu có thời gian) hết hạn.
 * Hỗ trợ:
 *  - Bảng rơi FastBall/Expand/Fireball/MultiBall với tỉ lệ tùy chỉnh.
 *  - Giới hạn mỗi loại tối đa N lần/màn (mặc định 3).
 *  - MultiBall instant, nhân đôi TẤT CẢ bóng (mỗi bóng +2) lệch ±45°.
 *  - Fireball khi chạy đổi skin tất cả bóng -> 'ballfire.png', hết hạn trả về skin thường.
 *  - Ball sink/balls supplier để thêm bóng và lấy danh sách bóng hiện có.
 */
public class PowerUpManager {
    private static final double GLOBAL_DROP_RATE = 0.2; // 20% tổng xác suất rơi

    private double FASTBALL_DROP_RATE  = 0.1 * GLOBAL_DROP_RATE;
    private double EXPAND_DROP_RATE    = 0.1 * GLOBAL_DROP_RATE;
    private double FIREBALL_DROP_RATE  = 0.4 * GLOBAL_DROP_RATE;
    private double MULTI_DROP_RATE     = 0.4 * GLOBAL_DROP_RATE;

    private int MAX_PER_TYPE_PER_LEVEL = 2;
    private final Map<String, Integer> perTypeSpawned = new HashMap<>();

    private transient final Random rng = new Random();

    private final List<PowerUp> items = new ArrayList<>();

    // Các hiệu ứng có thời gian đang hoạt động
    private final List<ActiveEffect> active = new ArrayList<>();

    // Thống kê (tổng số đã spawn trong màn — chỉ để log)
    private int spawnedThisLevel = 0;

    // Kích thước màn (dùng để loại item rơi khỏi đáy)
    private final int screenHeight;

    // Chính sách stack cho hiệu ứng có thời gian
    private final boolean allowStackSameType = false;
    private boolean refreshDurationOnSameType = true;

    // ===== Ball plumbing =====
    /** Nơi thêm bóng mới (ví dụ: extraBalls::add từ GameManager) */
    private Consumer<Ball> ballSink;

    /** Nguồn lấy TẤT CẢ bóng hiện có (ball chính + extraBalls) */
    private Supplier<List<Ball>> ballsSupplier;


    private static PowerUpManager instance;

    public static PowerUpManager getInstance() {
        return instance;
    }

    // Getter cho ballsSupplier
    public Supplier<List<Ball>> getBallsSupplier() {
        return ballsSupplier;
    }

    // Gói hiệu ứng để đếm lùi thời gian
    private static class ActiveEffect {
        final PowerUp instance;
        double timeLeft;

        ActiveEffect(PowerUp p) {
            this.instance = p;
            this.timeLeft = p.getDuration();
        }
    }

    // --- Constructors ---
    public PowerUpManager(int screenHeight) {
        this.screenHeight = screenHeight;
        instance = this;
    }

    public PowerUpManager() {
        this.screenHeight = GameManager.screenHeight;
        instance = this;
    }

    // ====== Registration APIs ======

    /** Đăng ký nơi chứa bóng mới (để Manager bơm bóng phụ từ MultiBall vào game). */
    public void setBallSink(Consumer<Ball> sink) { this.ballSink = sink; }

    /** Đăng ký nguồn lấy danh sách TẤT CẢ bóng hiện có (ball chính + extraBalls). */
    public void setBallsSupplier(Supplier<List<Ball>> supplier) { this.ballsSupplier = supplier; }

    /** Bơm thêm bóng vào game qua ball sink. */
    public void addBalls(Ball... balls) {
        if (ballSink == null) return;
        for (Ball b : balls) ballSink.accept(b);
    }

    /**
     * Đổi ảnh skin 'fire' cho Ball từ resource path (ví dụ: "/Image/ballfire.png").
     * Nếu Fireball đang chạy, lập tức cập nhật skin cho TẤT CẢ bóng.
     */
    public void setFireBallSkin(String resourcePath) {
        try {
            var is = PowerUpManager.class.getResourceAsStream(resourcePath);
            if (is != null) {
                Image img = new Image(is);
                if (img.getWidth() > 0) {
                    Ball.setGlobalFireSkin(img);
                    if (isFireballActive()) {
                        setAllBallsFireSkin(true);
                    }
                }
            } else {
                System.out.println("[PU] setFireBallSkin: resource not found " + resourcePath);
            }
        } catch (Exception e) {
            System.out.println("[PU] setFireBallSkin error: " + e.getMessage());
        }
    }

    /** Đổi skin cho TẤT CẢ bóng hiện có: on=true -> fire, on=false -> normal. */
    private void setAllBallsFireSkin(boolean on) {
        if (ballsSupplier == null) return;
        List<Ball> all = ballsSupplier.get();
        if (all == null) return;
        for (Ball b : all) {
            if (on) {
                b.setSkinFire();
                b.setPiercing(true);  // quan trọng: cho tất cả bóng
            } else {
                b.setSkinNormal();
                b.setPiercing(false); // hết hiệu ứng
            }
        }
    }


    /** Kiểm tra có hiệu ứng Fireball đang hoạt động không (để refresh skin nếu cần). */
    private boolean isFireballActive() {
        for (ActiveEffect ae : active) {
            if ("Fireball".equals(ae.instance.getType())) return true;
        }
        return false;
    }

    // ====== MultiBall replication ======

    /**
     * Nhân đôi TẤT CẢ bóng hiện có: mỗi bóng tạo thêm 2 bóng lệch ±angleRad.
     * Ví dụ: angleRad = Math.toRadians(45) -> ±45°.
     */
    public void replicateAllBalls(double angleRad) {
        if (ballsSupplier == null || ballSink == null) return;

        List<Ball> currents = new ArrayList<>(ballsSupplier.get()); // copy để tránh lặp trên list đang thay đổi
        if (currents.isEmpty()) return;

        double sin = Math.sin(angleRad), cos = Math.cos(angleRad);
        boolean fireballActive = isFireballActive();

        for (Ball src : currents) {
            int size = 15;
            int speed = src.getSpeed();
            double bx = src.getX(), by = src.getY();
            double dx = src.getDirectionX(), dy = src.getDirectionY();

            double dx1 = dx * cos - dy * sin;
            double dy1 = dx * sin + dy * cos;
            double dx2 = dx * cos + dy * sin;
            double dy2 = -dx * sin + dy * cos;

            Ball b1 = new Ball(bx, by, size, speed,false);
            b1.setDirectionX(dx1);
            b1.setDirectionY(dy1);

            Ball b2 = new Ball(bx, by, size, speed,false);
            b2.setDirectionX(dx2);
            b2.setDirectionY(dy2);

            if (fireballActive) {
                b1.setPiercing(true);
                b2.setPiercing(true);
                b1.setSkinFire();
                b2.setSkinFire();
            }

            addBalls(b1, b2);
        }
    }


    /** Reset khi bắt đầu/đổi màn. */
    public void resetLevel() {
        spawnedThisLevel = 0;
        items.clear();
        active.clear();
        perTypeSpawned.clear();
    }

    // Helpers cho per-type cap
    private boolean canSpawnType(String type) {
        return perTypeSpawned.getOrDefault(type, 0) < MAX_PER_TYPE_PER_LEVEL;
    }
    private void countSpawn(String type) {
        perTypeSpawned.put(type, perTypeSpawned.getOrDefault(type, 0) + 1);
    }

    /**
     * Gọi khi gạch vỡ (nhận TÂM gạch). Dịch tâm -> góc trên-trái khi tạo item 24x24.
     */
    public void maybeDropAt(int centerX, int centerY) {
        double roll = rng.nextDouble(); // [0, 1)
        PowerUp toSpawn = null;

        int spawnX = centerX - 12;
        int spawnY = centerY - 12;

        // Bảng rơi tích luỹ + tôn trọng per-type cap
        double acc = 0.0;

        acc += FASTBALL_DROP_RATE;
        if (roll < acc && canSpawnType("FastBall")) {
            toSpawn = new FastBallPowerUp(spawnX, spawnY);
        } else {
            acc += EXPAND_DROP_RATE;
            if (roll < acc && canSpawnType("ExpandPaddle")) {
                toSpawn = new ExpandPaddlePowerUp(spawnX, spawnY);
            } else {
                acc += FIREBALL_DROP_RATE;
                if (roll < acc && canSpawnType("Fireball")) {
                    toSpawn = new FireballPowerUp(spawnX, spawnY);
                } else {
                    acc += MULTI_DROP_RATE;
                    if (roll < acc && canSpawnType("MultiBall")) {
                        toSpawn = new MultiBallPowerUp(spawnX, spawnY, this);
                    }
                }
            }
        }

        // Fallback nếu loại được chọn đã đạt cap
//        if (toSpawn == null) {
//            if (canSpawnType("FastBall"))           toSpawn = new FastBallPowerUp(spawnX, spawnY);
//            else if (canSpawnType("ExpandPaddle"))  toSpawn = new ExpandPaddlePowerUp(spawnX, spawnY);
//            else if (canSpawnType("Fireball"))      toSpawn = new FireballPowerUp(spawnX, spawnY);
//            else if (canSpawnType("MultiBall"))     toSpawn = new MultiBallPowerUp(spawnX, spawnY, this);
//        }

        if (toSpawn != null) {
            items.add(toSpawn);
            countSpawn(toSpawn.getType());
            spawnedThisLevel++;
            System.out.printf("[PU DROP] type=%s @(%d,%d) perType=%d/%d total=%d%n",
                    toSpawn.getType(), centerX, centerY,
                    perTypeSpawned.get(toSpawn.getType()), MAX_PER_TYPE_PER_LEVEL, spawnedThisLevel);
        } else {
            System.out.println("[PU DROP] No available type (per-type cap reached)");
        }
    }

    // ====== Update & Render ======

    /** Overload tiện lợi: dùng screenWidth mặc định. */
    public void update(double deltaTime, Paddle paddle) {
        update(deltaTime, paddle, GameManager.screenWidth, this.screenHeight);
    }

    /** Core update: rơi → nhặt → (nếu có thời gian) đếm lùi. */
    public void update(double deltaTime, Paddle paddle, int screenWidth, int screenHeight) {
        double dt = deltaTime;

        // Items rơi + nhặt
        for (Iterator<PowerUp> it = items.iterator(); it.hasNext();) {
            PowerUp p = it.next();
            p.update(dt, 0, screenWidth);

            // Nhặt -> áp hiệu ứng
            if (intersects(p, paddle)) {
                SoundManager.PowerUpMusic();
                if (p.isInstant()) {
                    // MultiBall: áp ngay, không vào active
                    p.applyEffect(paddle);
                    System.out.println("[PU] Apply INSTANT: " + p.getType());

                    // MultiBall logic bên trong MultiBallPowerUp -> manager.replicateAllBalls(...)
                } else {
                    // Hiệu ứng có thời gian (FastBall / Expand / Fireball)
                    ActiveEffect same = findActiveByType(p.getType());
                    if (!allowStackSameType) {
                        if (same != null && refreshDurationOnSameType) {
                            same.timeLeft = Math.max(same.timeLeft, p.getDuration());
                            System.out.println("[PU] Refresh duration: " + p.getType());
                        } else {
                            p.applyEffect(paddle);
                            active.add(new ActiveEffect(p));
                            System.out.println("[PU] Apply: " + p.getType());

                            // Nếu là Fireball -> set skin Fire cho TẤT CẢ bóng
                            if ("Fireball".equals(p.getType())) {
                                setAllBallsFireSkin(true);
                            }
                        }
                    } else {
                        p.applyEffect(paddle);
                        active.add(new ActiveEffect(p));
                        System.out.println("[PU] Apply STACK: " + p.getType());
                    }
                }

                it.remove();
            } else if (p.getY() > screenHeight) {
                it.remove();
            }
        }

        // Đếm lùi các hiệu ứng có thời gian
        for (Iterator<ActiveEffect> ai = active.iterator(); ai.hasNext();) {
            ActiveEffect ae = ai.next();
            ae.timeLeft -= dt;
            if (ae.timeLeft <= 0) {
                // Hết hạn -> removeEffect()
                ae.instance.removeEffect(paddle);
                System.out.println("[PU] Remove: " + ae.instance.getType());

                // Nếu là Fireball -> trả skin Normal cho TẤT CẢ bóng
                if ("Fireball".equals(ae.instance.getType())) {
                    setAllBallsFireSkin(false);
                    // 🔥 Tắt xuyên gạch cho tất cả bóng hiện có
                    if (ballsSupplier != null) {
                        List<Ball> allBalls = ballsSupplier.get();
                        if (allBalls != null) {
                            for (Ball b : allBalls) {
                                b.setPiercing(false);
                            }
                        }
                    }
                }

                ai.remove();
            }
        }
    }

    /** Vẽ các item rơi. */
    public void render(GraphicsContext gc) {
        for (PowerUp p : items) {
            p.render(gc);
        }
    }

    // ====== Utils ======
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

    // ====== Setters tiện lợi ======

    /** Thiết lập tỉ lệ rơi (tổng <= 1.0 là đẹp). */
    public void setDropRates(double fastBallRate, double expandRate, double fireballRate, double multiRate) {
        this.FASTBALL_DROP_RATE  = fastBallRate;
        this.EXPAND_DROP_RATE    = expandRate;
        this.FIREBALL_DROP_RATE  = fireballRate;
        this.MULTI_DROP_RATE     = multiRate;
    }

    public void clearAll() {
        items.clear();     // xoá toàn bộ item đang rơi
        active.clear();    // xoá toàn bộ hiệu ứng đang chạy
        perTypeSpawned.clear(); // reset giới hạn số lần spawn
        spawnedThisLevel = 0;   // reset bộ đếm
        System.out.println("[PU] Cleared all PowerUps for new map");
    }

    /** Thiết lập giới hạn mỗi loại tối đa n lần trong một màn (mặc định 3). */
    public void setMaxPerTypePerLevel(int maxPerType) {
        this.MAX_PER_TYPE_PER_LEVEL = Math.max(0, maxPerType);
    }

    public void setRefreshDurationOnSameType(boolean v) { this.refreshDurationOnSameType = v; }
    public void spawnPowerUp(int x, int y) {
        PowerUp newpw;
        int r = (int)(Math.random() * (8  + 1)) ;
        if (r < 2) {
            newpw = new ExpandPaddlePowerUp(x,y);
        } else if (r < 4) {
            newpw = new FastBallPowerUp(x,y);
        } else if (r < 6) {
            newpw = new FireballPowerUp(x,y);
         } else {
            newpw = new MultiBallPowerUp(x,y, this);
        }
        items.add(newpw);
    }



}