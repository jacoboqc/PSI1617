package agent;

import gui.GraphicInterface;
import gui.controller.MainController;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import javafx.application.Application;
import javafx.application.Platform;

import java.util.*;
import java.util.Random;

public class Main extends Agent {
    private int numberPlayers; //N
    private int matrixSize; //S
    private int numberRounds; //R
    private int numberIterations; //I
    private int percentage; //P
    private int[][][] matrix;
    private AID[] players;
    private int[] payoffs;
    private List<AID> ids = new ArrayList<>();
    private String[][] globalStats;
    private String[][] localStats;
    private static MainController controller;

    @Override
    protected void setup() {
        new Thread(() -> Application.launch(GraphicInterface.class)).start();
        controller = GraphicInterface.waitForGraphicInterface();
        System.out.println("> Press ENTER to begin.");
        new Scanner(System.in).nextLine();

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                int[] params = controller.getParameters();
                matrixSize = params[0];
                numberRounds = params[1];
                numberIterations = params[2];
                percentage = params[3];

                printLog("> Searching for players... ");
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("Player");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    players = new AID[result.length];
                    numberPlayers = players.length;
                    for (int i = 0; i < result.length; ++i) {
                        players[i] = result[i].getName();
                        printLog(players[i].toString());
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
                printLog("Done!");
                List<AID[]> pairs = getPairs(players);

                printLog("> Sending ID message to the players...");
                for (int i = 0; i < numberPlayers; i++) {
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.addReceiver(players[i]);
                    msg.setContent("Id#" + i + "#" + numberPlayers + "," + matrixSize + "," + numberRounds + "," + numberIterations + "," + percentage);
                    send(msg);
                    ids.add(i, players[i]);
                }
                printLog("Done!");

                createGlobalStats(players);
                Platform.runLater(() -> controller.passAgentReference(myAgent));
                Platform.runLater(() -> controller.passBehaviourReference(this));

                addBehaviour(new TickerBehaviour(myAgent, 2000) {
                    @Override
                    protected void onTick() {
                        createMatrix();

                        AID[] pair = pairs.get(getTickCount() - 1);
                        printLog("> Sending NewGame message to the players... ");
                        int player1, player2;
                        player1 = ids.indexOf(pair[0]);
                        player2 = ids.indexOf(pair[1]);
                        for (int i = 0; i < 2; i++) {
                            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                            msg.addReceiver(pair[i]);
                            msg.setContent("NewGame#" + player1 + "#" + player2);
                            send(msg);
                        }
                        printLog("Done!");
                        payoffs = new int[2];
                        Arrays.fill(payoffs, 0);

                        ACLMessage msg;
                        printLog("Starting game.");
                        for (int i = 0; i < numberRounds; i++) {
                            if ((i + 1) % numberIterations == 0 && i != 0) {
                                printLog("> Time to shuffle " + percentage + "% of the matrix in round " + (i + 1));
                                shuffleMatrix();
                                for (int j = 0; j < 2; j++) {
                                    msg = new ACLMessage(ACLMessage.INFORM);
                                    msg.addReceiver(pair[j]);
                                    msg.setContent("Changed#" + percentage);
                                    send(msg);
                                }
                            }

                            int[] strategy = new int[2];
                            for (int j = 0; j < 2; j++) {
                                msg = new ACLMessage(ACLMessage.REQUEST);
                                msg.addReceiver(pair[j]);
                                msg.setContent("Position");
                                send(msg);
                                ACLMessage rsp = myAgent.blockingReceive();
                                strategy[j] = Integer.parseInt(rsp.getContent().split("#")[1]);
                            }
                            int[] payoff = matrix[strategy[0]][strategy[1]];
                            for (int j = 0; j < 2; j++) {
                                int value = payoffs[j];
                                value += payoff[j];
                                payoffs[j] = value;
                            }
                            for (int j = 0; j < 2; j++) {
                                msg = new ACLMessage(ACLMessage.INFORM);
                                msg.addReceiver(pair[j]);
                                msg.setContent("Results#" + strategy[0] + "," + strategy[1] + "#" + payoff[0] + "," + payoff[1]);
                                send(msg);
                            }
                            setNumberRounds(i + 1);
                        }
                        setNumberGames(getTickCount());
                        printLog("Game over!.");
                        printLog("And the winner is...");
                        int score = 0, winner = 0;
                        for (int i = 0; i < payoffs.length; i++) {
                            if (payoffs[i] > score) {
                                score = payoffs[i];
                                winner = i;
                            }
                        }
                        printLog("Player #" + ids.indexOf(pair[winner]) + " - Score: " + score);
                        updateGlobalStats(payoffs, pair, ids.indexOf(pair[winner]));
                        for (int j = 0; j < 2; j++) {
                            msg = new ACLMessage(ACLMessage.INFORM);
                            msg.addReceiver(pair[j]);
                            msg.setContent("EndGame");
                            send(msg);
                        }
                        if (getTickCount() == pairs.size()) {
                            printLog("> All games have been played!.");
                            removeBehaviour(this);
                        }
                    }


                });
            }
        });
        System.out.println("Hello! " + getAID().getName() + "is ready.");
    }



    protected void takeDown() {
        System.out.println("Agent " + getAID().getName() + " terminating.");
    }

    private void printLog(String log) {
        Platform.runLater(() -> controller.printLog(log));
    }

    private void createGlobalStats(AID[] players) {
        globalStats = new String[players.length][7];
        for (int i = 0; i < players.length; i++) {
            AID player = players[i];
            String name = player.getLocalName();
            int id = ids.indexOf(player);
            globalStats[i] = new String[] {name, "Player", Integer.toString(id), "0", "0", "0", "0"};
        }
        Platform.runLater(() -> controller.setGlobalStats(globalStats));
    }

    private void updateGlobalStats(int[] payoffs, AID[] pair, int winner) {
        for (int i = 0; i < 2; i++) {
            int id = ids.indexOf(pair[i]);
            int payoff = payoffs[i];
            globalStats[id][6] = Integer.toString(Integer.parseInt(globalStats[i][6]) + payoff);
            if (id == winner) globalStats[id][3] = Integer.toString(Integer.parseInt(globalStats[i][3]) + 1);
            else globalStats[id][4] = Integer.toString(Integer.parseInt(globalStats[i][4]) + 1);
        }
        Platform.runLater(() -> controller.setGlobalStats(globalStats));
    }

    private void setNumberRounds(int num) {
        Platform.runLater(() -> controller.setNumberRounds(num));
    }

    private void setNumberGames(int num) {
        Platform.runLater(() -> controller.setNumberGames(num));
    }

    private void createMatrix() {
        matrix = new int[matrixSize][matrixSize][2];
        int payoff1, payoff2;
        Random r = new Random();
        for (int i = 0; i < matrixSize; i++) {
            for (int j = i; j < matrixSize; j++) {
                payoff1 = r.nextInt(10);
                matrix[i][j][0] = payoff1;
                matrix[j][i][1] = payoff1;
                payoff2 = r.nextInt(10);
                matrix[i][j][1] = payoff2;
                matrix[j][i][0] = payoff2;
            }
        }
        Platform.runLater(() -> controller.printMatrix(matrix));
    }

    private List<AID[]> getPairs(AID[] players) {
        ArrayList<AID[]> combinations = new ArrayList<>();
        for (int player1 = 0; player1 < players.length - 1; player1++) {
            for (int player2 = player1 + 1; player2 <= players.length - 1; player2++) {
                AID[] pair = {players[player1], players[player2]};
                combinations.add(pair);
            }
        }
        return combinations;
    }

    private void shuffleMatrix() {
        double cellsToShuffle = (Math.pow(matrixSize, 2) * (percentage / 100F)) / 2;
        printLog("Number of cells to shuffle: " + cellsToShuffle * 2);
        for (int i = 0; i < (int) cellsToShuffle; i++) {
            Random r = new Random();
            int row = r.nextInt(matrixSize);
            int column = r.nextInt(matrixSize);
            int payoff1 = r.nextInt(10);
            matrix[row][column][0] = payoff1;
            matrix[column][row][1] = payoff1;
            int payoff2 = r.nextInt(10);
            matrix[row][column][1] = payoff2;
            matrix[column][row][0] = payoff2;
        }
        Platform.runLater(() -> controller.printMatrix(matrix));
    }
}
