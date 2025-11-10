package Game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class MenuController {
    private Main mainApp;

    @FXML
    private Button onePlayerButton;

    @FXML
    private Button twoPlayerButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button GuideButton;

    @FXML
    private Button BackButton;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private Label scoreLabel;

    @FXML
    private Button GOQuitButton;

    @FXML
    private Button GOMenuButton;

    @FXML
    public Button GO2QuitButton;

    @FXML
    public Label Win2PL;

    @FXML
    public Label Score2PL;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void onOnePlayerClick(ActionEvent actionEvent) {
        mainApp.startGame(false); // false = 1 người
    }

    @FXML
    private void onTwoPlayerClick(ActionEvent actionEvent) {
        mainApp.startGame(true); // true = 2 người
    }

    @FXML
    private void onExitClick(ActionEvent actionEvent) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onGuideClick(ActionEvent actionEvent) {
        try {
            mainApp.showGuide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onResume(){
        mainApp.resumeGame();
    }

    public void BackClick(ActionEvent actionEvent) {
        try {
            mainApp.showMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setScore(int score) {
        score = score;
        scoreLabel.setText("Scores: " + score);
    }


    public void GOQuitClick(ActionEvent actionEvent) {
        Stage stage = (Stage) GOQuitButton.getScene().getWindow();
        stage.close();
    }

    public void setWin2PL(int Player) {
        Win2PL.setText("PLAYER " + Player +" WIN");
    }

    public void setScore2PL(int score2PL){
        Score2PL.setText("HIGH SCORES : " + score2PL);
    }

    public void GO2QUitClick(ActionEvent actionEvent) {
        Stage stage = (Stage) GO2QuitButton.getScene().getWindow();
        stage.close();
    }
}
