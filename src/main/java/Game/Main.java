package Game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class Main extends Application {

    //init the game, game title,...
    @Override
    public void start(Stage stage){
        double width = 800;
        double height = 600;
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //GameManager gm = new GameManager(gc);
        GameManager2Player gm = new GameManager2Player(gc);
        Scene scene = new Scene(new StackPane(canvas));
        stage.setScene(scene);
        stage.setTitle("Arkanoid - JavaFX Skeleton");
        stage.show();

        //Key Pressed Handle
        scene.setOnKeyPressed((KeyEvent e) -> gm.onKeyPressed(e.getCode()));
        scene.setOnKeyReleased((KeyEvent e) -> gm.onKeyReleased(e.getCode()));

        //Game Loop
        final long[] lastNanoTime = {System.nanoTime()};

        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                double deltaTime = (currentNanoTime - lastNanoTime[0]) / 1e9;
                lastNanoTime[0] = currentNanoTime;

                gm.updateGame(deltaTime);
                gm.render();
            }
        }.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}