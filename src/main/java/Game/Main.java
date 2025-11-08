package Game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class Main extends Application {
    private Stage mainStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.mainStage = stage;
        showMenu();
    }

    public void showMenu() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/gameMenu.fxml"));
        Scene menuScene = new Scene(loader.load());
        mainStage.setTitle("Arkanoid Menu");
        mainStage.setScene(menuScene);
        mainStage.show();

        MenuController controller = loader.getController();
        controller.setMainApp(this);
    }

    public void showGuide() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Guide.fxml"));
        Scene guideScene = new Scene(loader.load());
        mainStage.setScene(guideScene);
        mainStage.setTitle("Arkanoid - Guide");
        mainStage.show();
        MenuController controller = loader.getController();
        // 🔹 Truyền lại Main để nó gọi showMenu() được
        controller.setMainApp(this);
    }

    public void startGame(boolean isTwoPlayer) {
        double width = 800;
        double height = 600;

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Nếu người chơi chọn 2 người → dùng GameManager2Player
        // Nếu không → dùng GameManager
        Object gm;
        if (isTwoPlayer) {
            gm = new GameManager2Player(gc);
        } else {
            gm = new GameManager(gc);
        }

        Scene gameScene = new Scene(new StackPane(canvas));
        mainStage.setScene(gameScene);
        mainStage.setTitle(isTwoPlayer ? "Arkanoid - 2 Player" : "Arkanoid - 1 Player");
        mainStage.show();

        // Gán phím điều khiển cho từng loại game manager
        if (gm instanceof GameManager gm1) {
            gameScene.setOnKeyPressed(e -> gm1.onKeyPressed(e.getCode()));
            gameScene.setOnKeyReleased(e -> gm1.onKeyReleased(e.getCode()));

            final long[] lastNanoTime = {System.nanoTime()};
            new AnimationTimer() {
                @Override
                public void handle(long currentNanoTime) {
                    double deltaTime = (currentNanoTime - lastNanoTime[0]) / 1e9;
                    lastNanoTime[0] = currentNanoTime;

                    gm1.updateGame(deltaTime);
                    gm1.render();
                }
            }.start();
        } else if (gm instanceof GameManager2Player gm2) {
            gameScene.setOnKeyPressed(e -> gm2.onKeyPressed(e.getCode()));
            gameScene.setOnKeyReleased(e -> gm2.onKeyReleased(e.getCode()));

            final long[] lastNanoTime = {System.nanoTime()};
            new AnimationTimer() {
                @Override
                public void handle(long currentNanoTime) {
                    double deltaTime = (currentNanoTime - lastNanoTime[0]) / 1e9;
                    lastNanoTime[0] = currentNanoTime;

                    gm2.updateGame(deltaTime);
                    gm2.render();
                }
            }.start();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
