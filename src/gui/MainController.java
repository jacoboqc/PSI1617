package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    private MenuItem close;
    @FXML
    private MenuItem about;

    @FXML
    public void initialize() {
        close.setOnAction(event -> System.exit(0));
        about.setOnAction(event -> {
                Stage stage = new Stage();
                Parent root = null;
                try {
                    root = FXMLLoader.load(getClass().getResource("about.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Scene scene = new Scene(root, 250, 100);
                stage.setTitle("About");
                stage.setScene(scene);
                stage.show();
            });
    }
}
