package gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class GraphicInterface extends Application {

    @Override
    public void start (Stage stage) {
        stage.setTitle("PSI Game 2016 0.1");
        Scene scene = new Scene(new VBox(), 1000, 850);
        MenuBar menuBar = drawMenuBar(stage);
        ((VBox) scene.getRoot()).getChildren().addAll(menuBar);
        stage.setScene(scene);
        stage.show();
    }

    private MenuBar drawMenuBar (Stage stage) {
        MenuBar menuBar = new MenuBar();

        Menu menuFile = new Menu("File");
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> System.exit(0));
        menuFile.getItems().add(exit);

        Menu menuEdit = new Menu("Edit");
        MenuItem resetPlayers = new MenuItem("Reset players");
        menuEdit.getItems().add(resetPlayers);

        Menu menuRun = new Menu("Run");
        MenuItem newGame = new MenuItem("New game");
        MenuItem stopGame = new MenuItem("Stop game");
        MenuItem continueGame = new MenuItem("Continue game");
        MenuItem nRounds = new MenuItem("Number of rounds");
        menuRun.getItems().addAll(newGame, stopGame, continueGame, nRounds);

        Menu menuWindow = new Menu("Window");
        MenuItem verbose = new MenuItem("Verbose");
        menuWindow.getItems().add(verbose);

        Menu menuHelp = new Menu("Help");
        MenuItem about = new MenuItem("About");
        about.setOnAction(event -> {
            Popup popup = new Popup();
            popup.setX(300);
            popup.setY(200);
            popup.setAutoHide(true);
            popup.getContent().addAll(new Circle(25, 25, 50, Color.AQUAMARINE), new Label("About screen."));
            popup.show(stage);
        });
        menuHelp.getItems().add(about);

        menuBar.getMenus().addAll(menuFile, menuEdit, menuRun, menuWindow, menuHelp);
        return menuBar;
    }

    public static void main (String[] args) {
        launch(args);
    }
}