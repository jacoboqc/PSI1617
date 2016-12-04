package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    private MenuItem exit;
    @FXML
    private MenuItem reset;
    @FXML
    private MenuItem newGame;
    @FXML
    private MenuItem stopGame;
    @FXML
    private MenuItem continueGame;
    @FXML
    private CheckMenuItem verbose;
    @FXML
    private MenuItem about;
    @FXML
    private Button newButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button continueButton;
    @FXML
    private TextField matrixSize;
    @FXML
    private TextField rounds;
    @FXML
    private TextField iterations;
    @FXML
    private TextField percentage;
    @FXML
    private Text log;

    private int matrixSizeParam;
    private int roundsParam;
    private int iterationsParam;
    private int percentageParam;

    @FXML
    public void initialize() {
        exit.setOnAction(event -> System.exit(0));
        about.setOnAction(event -> {
            Stage stage = new Stage();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("../style/about.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene scene = new Scene(root);
            stage.setTitle("About");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        });
        reset.setOnAction(event -> printLog("Resetting players."));
        newGame.setOnAction(event -> printLog("Starting new game."));
        stopGame.setOnAction(event -> printLog("Stopping current game."));
        continueGame.setOnAction(event -> printLog("Continuing current game."));
        verbose.setSelected(true);
        verbose.setOnAction(event -> {
            if (verbose.isSelected()) log.setText(log.getText() + "\nLogging system activated.");
            else log.setText(log.getText() + "\nLogging system deactivated.");
        });
        newButton.setOnAction(event -> printLog("Starting new game."));
        stopButton.setOnAction(event -> printLog("Stopping current game."));
        continueButton.setOnAction(event -> printLog("Continuing current game."));
        matrixSize.setText("3");
        matrixSize.setOnAction(event -> {
            matrixSizeParam = Integer.parseInt(matrixSize.getText());
            printLog("Setting matrix size to: " + matrixSizeParam);
        });
        rounds.setText("5");
        rounds.setOnAction(event -> {
            roundsParam = Integer.parseInt(rounds.getText());
            printLog("Setting number of rounds to: " + roundsParam);
        });
        iterations.setText("4");
        iterations.setOnAction(event -> {
            iterationsParam = Integer.parseInt(iterations.getText());
            printLog("Setting number of iterations to: " + iterationsParam);
        });
        percentage.setText("25");
        percentage.setOnAction(event -> {
            percentageParam = Integer.parseInt(percentage.getText());
            printLog("Setting percentage to: " + percentageParam);
        });
    }

    private void printLog(String value) {
        if (verbose.isSelected()) {
            log.setText(log.getText() + "\n" + value);
        }
    }
}
