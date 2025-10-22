package Game;

public abstract class MovableObject extends GameObject {
    protected double dx, dy; // tốc độ theo trục X, Y

    /**
     * Phuong thuc khoi tao
     */
    public MovableObject(double x, double y, int width, int height, double dx, double dy) {
        super(x, y, width, height);
        this.dx = dx; this.dy = dy;
    }

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public abstract void move(int leftWall, int rightWall);
}
