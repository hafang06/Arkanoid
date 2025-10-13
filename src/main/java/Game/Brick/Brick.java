package Game.Brick;

import Game.Ball;
import Game.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class Brick extends GameObject {
    protected int hitPoints;
    protected Color color;
    private static final int DEFAULT_WIDTH = 50;
    private static final int DEFAULT_HEIGHT = 20;

    public Brick(int x, int y, int hitPoints, Color color) {
        super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT );
        this.hitPoints = hitPoints;
        this.color=color;
    }

    public boolean intersects(Ball ball){
        //if(điểm cao nhất của đường tròn nằm trong brick) return true;
        return true;
    }
    public void takeHit(Ball ball) {
        if (hitPoints > 0 && intersects(ball)) {
            this.hitPoints--;
        }
    }
    public boolean isDestroyed() { return hitPoints <= 0; }

    @Override
    public void update() {

    }

    public void setColorByHitPoints() {
        switch (hitPoints) {
            case 1:
                color = Color.YELLOW;
                break;
            case 2:
                color = Color.GREEN;
                break;
            case 3:
                color = Color.BLUE;
                break;
            case 4:
                color = Color.INDIGO;
                break;
            case 5:
                color = Color.VIOLET;
                break;
            default:
                color = Color.GRAY;
                break;
        }
    }
    public void render(GraphicsContext gc) {

        if (!isDestroyed()) {
            setColorByHitPoints();
            gc.setFill(color);
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, width, height);
        }
    }
}