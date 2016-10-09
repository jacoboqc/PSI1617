package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GraphicInterface extends Application {

    @Override
    public void start (Stage stage) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("style/main.fxml"));
        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("PSI Game 16/17");
        stage.setScene(scene);
        stage.show();
    }
    public static void main (String[] args) {
        launch(args);
    }
}