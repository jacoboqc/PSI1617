package gui.controller;

import jade.core.*;
import jade.core.behaviours.Behaviour;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;

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
    private Text numberRounds;
    @FXML
    private Text numberGames;
    @FXML
    private Text log;
    @FXML
    private Text globalStats;
    @FXML
    private Text localStats;
    @FXML
    private Text matrix;

    private int matrixSizeParam;
    private int roundsParam;
    private int iterationsParam;
    private int percentageParam;

    private boolean doReset = false;

    private Agent main;
    private Behaviour mainBehaviour;
    private Behaviour gameBehaviour;

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
        reset.setOnAction(event -> {
            printLog("Resetting players.");
            doReset = true;
        });
        newGame.setOnAction(event -> {
            printLog("Starting new game.");
            restart();
        });
        stopGame.setOnAction(event -> {
            printLog("Stopping current game.");
            pause();
        });
        continueGame.setOnAction(event -> {
            printLog("Continuing current game.");
            resume();
        });
        verbose.setSelected(true);
        verbose.setOnAction(event -> {
            if (verbose.isSelected()) log.setText(log.getText() + "\nLogging system activated.");
            else log.setText(log.getText() + "\nLogging system deactivated.");
        });
        newButton.setOnAction(event -> {
            printLog("Starting new game.");
            restart();
        });
        stopButton.setOnAction(event -> {
            printLog("Stopping current game.");
            pause();
        });
        continueButton.setOnAction(event -> {
            printLog("Continuing current game.");
            resume();
        });
        matrixSize.setOnAction(event -> {
            matrixSizeParam = Integer.parseInt(matrixSize.getText());
            printLog("Setting matrix size to: " + matrixSizeParam);
            restart();
        });
        rounds.setOnAction(event -> {
            roundsParam = Integer.parseInt(rounds.getText());
            printLog("Setting number of rounds to: " + roundsParam);
            restart();
        });
        iterations.setOnAction(event -> {
            iterationsParam = Integer.parseInt(iterations.getText());
            printLog("Setting number of iterations to: " + iterationsParam);
            restart();
        });
        percentage.setOnAction(event -> {
            percentageParam = Integer.parseInt(percentage.getText());
            printLog("Setting percentage to: " + percentageParam);
            restart();
        });
        setDefaultParams();
    }

    public void printLog(String value) {
        if (verbose.isSelected()) {
            log.setText(log.getText() + "\n" + value);
        }
    }

    private void setDefaultParams() {
        matrixSizeParam = 4;
        matrixSize.setText(Integer.toString(matrixSizeParam));
        roundsParam = 10;
        rounds.setText(Integer.toString(roundsParam));
        iterationsParam = 2;
        iterations.setText(Integer.toString(iterationsParam));
        percentageParam = 25;
        percentage.setText(Integer.toString(percentageParam));
    }

    public void setGlobalStats(String[][] globalStats) {
        this.globalStats.setText("Name-Type-ID-Won-Lost-Draw-Total Payoff");
        for (String[] stat : globalStats) {
            this.globalStats.setText(this.globalStats.getText() + "\n" + Arrays.toString(stat));
        }
    }

    public void setLocalStats(String[][] localStats) {
        this.localStats.setText("Player-Won-Lost-Draw-Total Payoff");
        for (String[] stat : localStats) {
            this.localStats.setText(this.localStats.getText() + "\n" + Arrays.toString(stat));
        }
    }

    public void setNumberRounds(int num) {
        numberRounds.setText(numberRounds.getText().split(": ")[0] + ": " + num);
    }

    public void setNumberGames(int num) {
        numberGames.setText(numberGames.getText().split(": ")[0] + ": " + num);
    }

    public void passAgentReference(Agent agent) {
        main = agent;
    }

    public void passMainBehaviourReference(Behaviour behaviour) {
        mainBehaviour = behaviour;
    }

    public void passGameBehaviourReference(Behaviour behaviour) {
        gameBehaviour = behaviour;
    }

    private void pause() {
        main.doSuspend();
    }

    private void resume() {
        main.doActivate();
    }

    private void restart() {
        main.removeBehaviour(gameBehaviour);
        main.removeBehaviour(mainBehaviour);
        main.addBehaviour(mainBehaviour);
    }

    public int[] getParameters() {
        return new int[]{matrixSizeParam, roundsParam, iterationsParam, percentageParam};
    }

    public boolean doReset() {
        if (doReset) {
            doReset = false;
            return true;
        } else return false;
    }

    public void printMatrix(int[][][] matrix) {
        this.matrix.setText("Payoff matrix\n");
        for (int i = 0; i < matrixSizeParam; i++) {
            for (int j = 0; j < matrixSizeParam; j++) {
                this.matrix.setText(this.matrix.getText() + matrix[i][j][0] + "/" + matrix[i][j][1] + " ");
            }
            this.matrix.setText(this.matrix.getText() + "\n");
        }
    }
}
