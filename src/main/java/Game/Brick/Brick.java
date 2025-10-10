package Game.Brick;

import Game.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;


public class Brick extends GameObject {
    protected int hitPoints;

    public Brick(int x, int y, int hitPoints) {
        super(x, y, 300, 100);
        this.hitPoints = hitPoints;
//        this.type = type;
    }

    public void takeHit() {
        if (hitPoints > 0) {
            this.hitPoints--;
        }
    }
    public boolean isDestroyed() { return hitPoints <= 0; }

    @Override
    public void update() {

    }
    @Override
    public void render(GraphicsContext gc) {
        switch (hitPoints){
            case 1:
                gc.setFill(Color.YELLOW);
                break;
            case 2:
                gc.setFill(Color.GREEN);
                break;
            case 3:
                gc.setFill(Color.BLUE);
                break;
            case 4:
                gc.setFill(Color.INDIGO);
                break;
            case 5:
                gc.setFill(Color.VIOLET);
                break;

        }
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, height);
    }
}