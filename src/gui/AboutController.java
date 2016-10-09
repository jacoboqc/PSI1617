package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AboutController {
    @FXML
    private Button closeAbout;

    @FXML
    private void initialize () {
        closeAbout.setOnAction(event -> {
            Stage stage = (Stage) closeAbout.getScene().getWindow();
            stage.close();
        });
    }
}
