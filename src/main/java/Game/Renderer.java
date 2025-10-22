package Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Renderer {
    private GraphicsContext gc;

    //init renderer;
    public Renderer(GraphicsContext _gc){
        this.gc = _gc;
    }

    //clear the screen for next draw
    //if nothing happen, screen stays black
    public void clear(double width, double height){
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);
    }

    public void draw(GameObject obj) {
        // Vẽ object ra màn hình
        obj.render(gc);
    }

    public GraphicsContext getGc() {
        return gc;
    }

    public void setGc(GraphicsContext gc) {
        this.gc = gc;
    }
}
