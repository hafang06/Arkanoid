package Game;

public class Ball extends MovableObject {
    private int speed;
    private int directionX, directionY;

    public Ball(int x, int y, int size, int speed) {
        super(x, y, size, size, 0, 0);
        this.speed = speed;
    }

    public void bounceOff(GameObject other) {}
    public boolean checkCollision(GameObject other) { return false; }

    @Override
    public void update() {}
    @Override
    public void render() {}
    @Override
    public void move() {}
}