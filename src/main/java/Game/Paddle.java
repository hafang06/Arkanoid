package Game;

public class Paddle extends MovableObject {
    private int speed;
    private PowerUp currentPowerUp;

    public Paddle(int x, int y, int width, int height, int speed) {
        super(x, y, width, height, 0, 0);
        this.speed = speed;
    }

    public void moveLeft() {}
    public void moveRight() {}
    public void applyPowerUp(PowerUp p) {}

    @Override
    public void update() {}
    @Override
    public void render() {}
    @Override
    public void move() {}
}