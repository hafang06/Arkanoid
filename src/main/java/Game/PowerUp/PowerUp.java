package Game.PowerUp;

import Game.GameObject;
import Game.Paddle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class PowerUp extends GameObject {

    protected String type;
    protected double duration;       // giây
    protected double dy = 120.0;     // tốc độ rơi px/giây
    protected Image image;           // ảnh hiển thị item rơi

    public PowerUp(int x, int y, int width, int height, String type, double duration) {
        super(x, y, width, height);
        this.type = type;
        this.duration = duration;
    }

    // === NEW: đặc tính quản lý trong PowerUpManager ===
    /** Hiệu ứng tức thời: áp dụng ngay khi nhặt, không bị quản lý thời gian/active list. */
    public boolean isInstant() { return false; }

    /** Cho phép stack nhiều bản cùng loại (áp dụng nhiều lần liên tiếp). */
    public boolean isStackable() { return false; }

    protected void loadImage(String fileName) {
        String[] candidates = new String[] {
                "/image/PowerUp/" + fileName,
                "/rsc/image/PowerUp/" + fileName,
                "/Image/PowerUp/" + fileName
        };
        for (String path : candidates) {
            try {
                var is = getClass().getResourceAsStream(path);
                if (is != null) {
                    Image img = new Image(is);
                    if (img.getWidth() > 0) {
                        this.image = img;
                        return;
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    public String getType() { return type; }
    public double getDuration() { return duration; }

    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }

    public abstract void applyEffect(Paddle paddle);
    public abstract void removeEffect(Paddle paddle);

    @Override
    public void update(double deltaTime, int leftWall, int rightWall) {
        this.y += dy * deltaTime;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (image != null) {
            gc.drawImage(image, x, y, width, height);
        } else {
            gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
            gc.fillRect(x, y, width, height);
            gc.setStroke(javafx.scene.paint.Color.BLACK);
            gc.strokeRect(x, y, width, height);
        }
    }
}