package gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutController {
    @FXML
    private Button closeAbout;
    @FXML
    private Hyperlink link;

    @FXML
    private void initialize () {
        closeAbout.setOnAction(event -> {
            Stage stage = (Stage) closeAbout.getScene().getWindow();
            stage.close();
        });
        link.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("http://www.flaticon.com"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }
}
