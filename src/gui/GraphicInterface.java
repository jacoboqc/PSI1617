package gui;

import gui.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class GraphicInterface extends Application{
    private static final CountDownLatch latch = new CountDownLatch(1);
    private static MainController controller = null;

    @Override
    public void start (Stage stage) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("style/main.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        latch.countDown();
        Scene scene = new Scene(root);
        stage.setTitle("PSI Game 16/17");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static MainController waitForGraphicInterface() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return controller;
    }
}