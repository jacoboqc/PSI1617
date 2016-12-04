package agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.*;
import java.util.Random;

public class Main extends Agent{
    private int numberPlayers; //N
    private int matrixSize = 4; //S
    private int numberRounds = 10; //R
    private int numberIterations = 2; //I
    private int percentage = 25; //P
    private int[][][] matrix = new int[matrixSize][matrixSize][2];
    private List<Integer> payoffs;

    @Override
    protected void setup() {
        createMatrix();
        System.out.println("> Press ENTER to begin.");
        new Scanner(System.in).nextLine();

        System.out.println("We are gonna play with this matrix:");
        printMatrix();
        addBehaviour(new WakerBehaviour(this, 0) {
            private AID[] players;
            protected void handleElapsedTimeout() {
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

                System.out.print("> Sending ID message to the players... ");
                for (int i = 0; i < numberPlayers; i++) {
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.addReceiver(players[i]);
                    msg.setContent("Id#" + i + "#" + numberPlayers + "," + matrixSize + "," + numberRounds + "," + numberIterations + "," + percentage);
                    send(msg);
                }
                System.out.println("Done!");

                System.out.print("> Sending NewGame message to the players... ");
                for (int i = 0; i < numberPlayers; i++) {
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.addReceiver(players[i]);
                    msg.setContent("NewGame#0#1");
                    send(msg);
                }
                System.out.println("Done!");
                payoffs = new ArrayList<>();
                for (int i = 0; i < numberPlayers; i++) {
                    payoffs.add(0);
                }

                ACLMessage msg;
                System.out.println("Starting game.");
                for (int i = 0; i < numberRounds; i++) {
                    if ((i + 1) % numberIterations == 0 && i != 0) {
                        System.out.println("> Time to shuffle " + percentage + "% of the matrix in round " + (i + 1 ));
                        shuffleMatrix();
                        for (int j = 0; j < numberPlayers; j++) {
                            msg = new ACLMessage(ACLMessage.INFORM);
                            msg.addReceiver(players[j]);
                            msg.setContent("Changed#" + percentage);
                            send(msg);
                        }
                    }

                    int[] strategy = new int[2];
                    for (int j = 0; j < numberPlayers; j++) {
                        msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.addReceiver(players[j]);
                        msg.setContent("Position");
                        send(msg);
                        ACLMessage rsp = myAgent.blockingReceive();
                        strategy[j] = Integer.parseInt(rsp.getContent().split("#")[1]);
                    }
                    int[] payoff = matrix[strategy[0]][strategy[1]];
                    for (int j = 0; j < numberPlayers; j++) {
                        int value = payoffs.get(j);
                        value += payoff[j];
                        payoffs.set(j, value);
                    }
                    for (int j = 0; j < players.length; j++) {
                        msg = new ACLMessage(ACLMessage.INFORM);
                        msg.addReceiver(players[j]);
                        msg.setContent("Results#" + strategy[0] + "," + strategy[1] + "#" + payoff[0] + "," + payoff[1]);
                        send(msg);
                    }
                }
                System.out.println("Game over!.");
                System.out.println("And the winner is...");
                int score = Collections.max(payoffs);
                int winner = payoffs.indexOf(score);
                System.out.println("Player #" + winner + " - Score: " + score);
                for (int j = 0; j < numberPlayers; j++) {
                    msg = new ACLMessage(ACLMessage.INFORM);
                    msg.addReceiver(players[j]);
                    msg.setContent("EndGame");
                    send(msg);
                }
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
