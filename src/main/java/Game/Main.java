package Game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

import java.io.IOException;

public class Main extends Application {
    private Stage mainStage;
    private Object gm;
    private double width = 800;
    private double height = 600;
    private Canvas canvas = new Canvas(width, height);
    private GraphicsContext gc = canvas.getGraphicsContext2D();
    //SoundManager soundManager = new SoundManager();

    private AnimationTimer gameLoop;
    private long lastNanoTime;

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
        SoundManager.playBackgroundMusic(true); // loop nhạc nền

        MenuController controller = loader.getController();
        controller.setMainApp(this);
    }

    public void showGuide() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Guide.fxml"));
        Scene guideScene = new Scene(loader.load());
        mainStage.setScene(guideScene);
        mainStage.setTitle("Arkanoid - Guide");
        mainStage.show();
        SoundManager.playBackgroundMusic(true);
        MenuController controller = loader.getController();
        controller.setMainApp(this);
    }

    public void startGame(boolean isTwoPlayer) {
        // Nếu người chơi chọn 2 người → dùng GameManager2Player
        // Nếu không → dùng GameManager

        if (isTwoPlayer) {
            gm = new GameManager2Player(gc,mainStage,this);
            SoundManager.stopMusic(SoundManager.bgPlayer);
        } else {
            SoundManager.stopMusic(SoundManager.bgPlayer);
            gm = new GameManager(gc,mainStage,this);
        }

        Scene gameScene = new Scene(new StackPane(canvas));
        mainStage.setScene(gameScene);
        mainStage.setTitle(isTwoPlayer ? "Arkanoid - 2 Player" : "Arkanoid - 1 Player");
        mainStage.show();

        // Gán phím điều khiển cho từng loại game manager
        if (gm instanceof GameManager gm1) {
            gameScene.setOnKeyPressed(e -> gm1.onKeyPressed(e.getCode()));
            gameScene.setOnKeyReleased(e -> gm1.onKeyReleased(e.getCode()));

            lastNanoTime = System.nanoTime();
            gameLoop = new AnimationTimer(){
                @Override
                public void handle(long currentNanoTime) {
                    double deltaTime = (currentNanoTime - lastNanoTime) / 1e9;
                    lastNanoTime = currentNanoTime;

                    gm1.updateGame(deltaTime);
                    gm1.render();
                }
            };
            gameLoop.start();
        } else if (gm instanceof GameManager2Player gm2) {
            gameScene.setOnKeyPressed(e -> gm2.onKeyPressed(e.getCode()));
            gameScene.setOnKeyReleased(e -> gm2.onKeyReleased(e.getCode()));

            lastNanoTime = System.nanoTime();
            gameLoop = new AnimationTimer(){
                @Override
                public void handle(long currentNanoTime) {
                    double deltaTime = (currentNanoTime - lastNanoTime) / 1e9;
                    lastNanoTime = currentNanoTime;

                    gm2.updateGame(deltaTime);
                    gm2.render();
                }
            };
            gameLoop.start();
        }
    }

    //Show Pause Menu
    public void showPauseMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/PauseMenu.fxml"));
            Scene pauseScene = new Scene(loader.load());
            mainStage.setScene(pauseScene);
            mainStage.setTitle("Game Paused");

            // Truyền tham chiếu Main cho controller
            PauseController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Resume Game
    public void resumeGame(){
        //get previous canvas
        if(gm == null){
            gm = new GameManager(gc, mainStage,this);
            ((GameManager) gm).loadGame("save.json");
            //System.out.println(((GameManager) gm).isPaused());
        }

        Scene gameScene = new Scene(new StackPane(canvas));
        mainStage.setScene(gameScene);
        mainStage.setTitle("Arkanoid - 1 Player");

        canvas.requestFocus();
        if(gm instanceof GameManager gm1){
            gameScene.setOnKeyPressed(e -> gm1.onKeyPressed(e.getCode()));
            gameScene.setOnKeyReleased(e -> gm1.onKeyReleased(e.getCode()));

            lastNanoTime = System.nanoTime();
            if(!((GameManager) gm).isPaused()){
                gameLoop = new AnimationTimer(){
                    @Override
                    public void handle(long currentNanoTime) {
                        double deltaTime = (currentNanoTime - lastNanoTime) / 1e9;
                        lastNanoTime = currentNanoTime;

                        gm1.updateGame(deltaTime);
                        gm1.render();
                    }
                };
            }
        }

        if(gm instanceof GameManager gm1){
            gm1.resumeGame();
        }

        gameLoop.start();
    }

    //Save and back to main menu
    public void saveAndQuit() {
        try {
            // Gọi saveGame tương ứng
            if (gm instanceof GameManager gm1) {
                gm1.saveGame("saves/save.json");
            }

            // Trở về menu chính
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/gameMenu.fxml"));
            Scene menuScene = new Scene(loader.load());
            mainStage.setScene(menuScene);
            mainStage.setTitle("Arkanoid Menu");

            MenuController controller = loader.getController();
            controller.setMainApp(this);
            SoundManager.playBackgroundMusic(true);

            gm = null; // reset game
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Quit the program
    public void quitGame() {
        SoundManager.stopMusic(SoundManager.bgPlayer);
        mainStage.close();
        System.exit(0);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
