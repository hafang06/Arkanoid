package Game;

public abstract class MovableObject extends GameObject {
    protected int dx, dy; // tốc độ theo trục X, Y

    /**
     * Phuong thuc khoi tao
     */
    public MovableObject(int x, int y, int width, int height, int dx, int dy) {
        super(x, y, width, height);
        this.dx = dx; this.dy = dy;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public abstract void move();
}
