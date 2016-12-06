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

import java.util.*;
import java.util.Random;

public class Main extends Agent {
    private int numberPlayers; //N
    private int matrixSize = 4; //S
    private int numberRounds = 10; //R
    private int numberIterations = 2; //I
    private int percentage = 25; //P
    private int[][][] matrix = new int[matrixSize][matrixSize][2];
    private List<Integer> payoffs;
    private List<AID> ids = new ArrayList<>();
    private static MainController controller;

    @Override
    protected void setup() {
        GraphicInterface gui = new GraphicInterface();
        new Thread(() -> gui.show()).start();
        doWait(5000);
        controller = gui.getController();

        System.out.println("> Press ENTER to begin.");
        new Scanner(System.in).nextLine();
        addBehaviour(new OneShotBehaviour() {
            private AID[] players;

            @Override
            public void action() {
                System.out.println("> Searching for players... ");
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
                        System.out.println(players[i]);
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
                System.out.println("Done!");
                List<AID[]> pairs = getPairs(players);

                System.out.print("> Sending ID message to the players...");
                for (int i = 0; i < numberPlayers; i++) {
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.addReceiver(players[i]);
                    msg.setContent("Id#" + i + "#" + numberPlayers + "," + matrixSize + "," + numberRounds + "," + numberIterations + "," + percentage);
                    send(msg);
                    ids.add(i, players[i]);
                }
                System.out.println("Done!");

                addBehaviour(new TickerBehaviour(myAgent, 2000) {
                    @Override
                    protected void onTick() {
                        createMatrix();
                        System.out.println("We are gonna play with this matrix:");
                        printMatrix();

                        AID[] pair = pairs.get(getTickCount() - 1);
                        System.out.print("> Sending NewGame message to the players... ");
                        int player1, player2;
                        player1 = ids.indexOf(pair[0]);
                        player2 = ids.indexOf(pair[1]);
                        for (int i = 0; i < 2; i++) {
                            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                            msg.addReceiver(pair[i]);
                            msg.setContent("NewGame#" + player1 + "#" + player2);
                            send(msg);
                        }
                        System.out.println("Done!");
                        payoffs = new ArrayList<>();
                        for (int i = 0; i < 2; i++) {
                            payoffs.add(0);
                        }

                        ACLMessage msg;
                        System.out.println("Starting game.");
                        for (int i = 0; i < numberRounds; i++) {
                            if ((i + 1) % numberIterations == 0 && i != 0) {
                                System.out.println("> Time to shuffle " + percentage + "% of the matrix in round " + (i + 1));
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
                                int value = payoffs.get(j);
                                value += payoff[j];
                                payoffs.set(j, value);
                            }
                            for (int j = 0; j < 2; j++) {
                                msg = new ACLMessage(ACLMessage.INFORM);
                                msg.addReceiver(pair[j]);
                                msg.setContent("Results#" + strategy[0] + "," + strategy[1] + "#" + payoff[0] + "," + payoff[1]);
                                send(msg);
                            }
                        }
                        System.out.println("Game over!.");
                        System.out.println("And the winner is...");
                        int score = Collections.max(payoffs);
                        int winner = payoffs.indexOf(score);
                        System.out.println("Player #" + ids.indexOf(pair[winner]) + " - Score: " + score);
                        for (int j = 0; j < 2; j++) {
                            msg = new ACLMessage(ACLMessage.INFORM);
                            msg.addReceiver(pair[j]);
                            msg.setContent("EndGame");
                            send(msg);
                        }
                        if (getTickCount() == pairs.size()) {
                            System.out.println("> All games have been played!.");
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

    private void createMatrix() {
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
    }

    private static List<AID[]> getPairs(AID[] players) {
        ArrayList<AID[]> combinations = new ArrayList<>();
        System.out.println("Calculating...");
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
        System.out.println("Number of cells to shuffle: " + cellsToShuffle * 2);
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
        printMatrix();
    }

    private void printMatrix() {
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                System.out.print(matrix[i][j][0] + "/" + matrix[i][j][1] + " ");
            }
            System.out.println();
        }
    }
}
