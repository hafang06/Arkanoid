package Game;

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
    private ImageView backgroundImage;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void onOnePlayerClick() {
        mainApp.startGame(false); // false = 1 người
    }

    @FXML
    private void onTwoPlayerClick() {
        mainApp.startGame(true); // true = 2 người
    }

    @FXML
    private void onExitClick() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
