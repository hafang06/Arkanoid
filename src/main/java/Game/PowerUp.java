package Game;

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
    public void update() {
        this.y += dy;
    }

    @Override
    public void render() {
        // thêm ảnh ở đây
    }
}