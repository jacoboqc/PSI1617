package gui;

import gui.controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GraphicInterface extends Application{
    private static MainController controller;

    @Override
    public void start (Stage stage) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("style/main.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        Scene scene = new Scene(root);
        stage.setTitle("PSI Game 16/17");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }


    public static MainController getController() {
        return controller;
    }

    public void show () {
        String[] args = {};
        launch(args);
    }
}