package Game;

import javafx.scene.canvas.GraphicsContext;

public abstract class PowerUp extends GameObject {
    protected String type;
    protected int duration;

    public PowerUp(int x, int y, int width, int height, String type, int duration) {
        super(x, y, width, height);
        this.type = type;
        this.duration = duration;
    }

    public abstract void applyEffect(Paddle paddle);
    public abstract void removeEffect(Paddle paddle);

    @Override
    public void update() {}
    @Override
    public void render(GraphicsContext gc) {}
}