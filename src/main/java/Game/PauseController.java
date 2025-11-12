package Game;

import javafx.fxml.FXML;

public class PauseController {
    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void onResumeClicked() {
        mainApp.resumeGame();
    }

    @FXML
    private void onSaveAndQuitClicked() {
        mainApp.saveAndQuit();
    }

    @FXML
    private void onQuitClicked(){
        mainApp.quitGame();
    }
}