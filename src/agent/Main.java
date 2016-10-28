package agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.Random;
import java.util.Scanner;

public class Main extends Agent{
    private int numberPlayers = 6; //N
    private int matrixSize = 3; //S
    private int numberRounds = 2; //R
    private int numberIterations = 5; //I
    private int percentage = 25; //P
    private int[][][] matrix = new int[matrixSize][matrixSize][2];

    @Override
    protected void setup() {
        createMatrix();
        printMatrix();
        System.out.println("> Press ENTER to begin.");
        new Scanner(System.in).nextLine();
        addBehaviour(new WakerBehaviour(this, 0) {
            private AID[] players;
            protected void handleElapsedTimeout() {
                System.out.print("> Searching for agents... ");
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("Player");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    players = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        players[i] = result[i].getName();
                        System.out.println(players[i]);
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
                System.out.println("Done!");

                System.out.print("> Informing agents... ");
                for (int i = 0; i < players.length; i++) {
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.addReceiver(players[i]);
                    msg.setContent("Id#" + i + "#" + numberPlayers + "," + matrixSize + "," + numberRounds + "," + numberIterations + "," + percentage);
                    send(msg);
                }
                System.out.println("Done!");
            }
        });
        System.out.println("Hello! " + getAID().getName() + "is ready.");
    }

    protected void takeDown() {
        System.out.println("Agent " + getAID().getName() + " terminating.");
    }

    private void createMatrix () {
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

    private void printMatrix () {
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                System.out.print(matrix[i][j][0] + "/" + matrix[i][j][1] + " ");
            }
            System.out.println();
        }
    }
}
