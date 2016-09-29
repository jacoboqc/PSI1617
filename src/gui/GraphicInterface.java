package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GraphicInterface extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("PSI Game 2016 0.1");
        Scene scene = new Scene(new VBox(), 1000, 850);
        MenuBar menuBar = new MenuBar();

        Menu menuFile = new Menu("File");
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(t -> System.exit(0));
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
        MenuItem help = new MenuItem("Help");
        menuHelp.getItems().add(help);

        menuBar.getMenus().addAll(menuFile, menuEdit, menuRun, menuWindow, menuHelp);

        ((VBox) scene.getRoot()).getChildren().addAll(menuBar);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}