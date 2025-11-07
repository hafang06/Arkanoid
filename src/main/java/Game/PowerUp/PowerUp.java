package Game.PowerUp;

import Game.GameObject;
import Game.Paddle;
import javafx.scene.canvas.GraphicsContext;

public abstract class PowerUp extends GameObject {

    protected String type;
    protected double duration; // int -=> double
    protected double dy = 2.0; // tốc độ trơi

    public PowerUp(int x, int y, int width, int height, String type, double duration) {
        super(x, y, width, height);
        this.type = type;
        this.duration = duration;
    }

    public String getType() {
        return type;
    }
    public double getDuration() {
        return  duration;
    }

    public abstract void applyEffect(Paddle paddle);
    public abstract void removeEffect(Paddle paddle);

    @Override
    public void update(double deltaTime, int leftWall, int rightWall) {
        this.y += dy;
    }

    @Override
    public void render(GraphicsContext gc) {
        // thêm ảnh ở đây
    }
}