package Game.Brick;

import Game.Ball;
import Game.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class Brick extends GameObject {
    protected int hitPoints;
    protected Image image;
    private static final int DEFAULT_WIDTH = 50;
    private static final int DEFAULT_HEIGHT = 20;
    private static final Image Brick1 = new Image(Brick.class.getResource("/Image/violet.png").toExternalForm());
    private static final Image Brick2 = new Image(Brick.class.getResource("/Image/blue.png").toExternalForm());
    private static final Image Brick3 = new Image(Brick.class.getResource("/Image/green.png").toExternalForm());
    private static final Image Brick4 = new Image(Brick.class.getResource("/Image/yellow.png").toExternalForm());
    private static final Image Brick5 = new Image(Brick.class.getResource("/Image/red.png").toExternalForm());
    private static final Image unbreak_Brick = new Image(Brick.class.getResource("/Image/unbreak.png").toExternalForm());

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }


    public Brick(int x, int y, int hitPoints) {
        super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT );
        this.hitPoints = hitPoints;
    }

    public void takeHit(Ball ball) {
        if (hitPoints > 0 && !(this instanceof unBreakBrick)) {
            this.hitPoints--;
        }
    }
    public boolean isDestroyed() { return hitPoints <= 0; }


    @Override
    public void update(double deltaTime) {
        setImageByHitPoints();
    }

    public void setImageByHitPoints() {
        switch (hitPoints) {
            case 1:
                image = Brick1;
                break;
            case 2:
                image = Brick2;
                break;
            case 3:
                image = Brick3;
                break;
            case 4:
                image = Brick4;
                break;
            case 5:
                image = Brick5;
                break;
            default:
                image = unbreak_Brick;
                break;
        }
    }
    public void render(GraphicsContext gc) {

        if (!isDestroyed()) {
            setImageByHitPoints();
            gc.drawImage(image,x,y,DEFAULT_WIDTH,DEFAULT_HEIGHT);
        }
    }
}