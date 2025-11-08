package Game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

    public void BackClick(ActionEvent actionEvent) {
        try {
            mainApp.showMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
