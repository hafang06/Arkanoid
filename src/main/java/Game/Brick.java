package Game;

public class Brick extends GameObject {
    protected int hitPoints;
    protected String type;

    public Brick(int x, int y, int width, int height, int hitPoints, String type) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.type = type;
    }

    public void takeHit() {}
    public boolean isDestroyed() { return hitPoints <= 0; }

    @Override
    public void update() {}
    @Override
    public void render() {}
}