package agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Random;

public class Main extends Agent{
    private int numberPlayers = 6;
    private int[][][] matrix = new int[numberPlayers][numberPlayers][2];
    @Override
    protected void setup() {
        int payoff1, payoff2;
        Random r = new Random();
        for (int i = 0; i < numberPlayers; i++) {
            for (int j = i; j < numberPlayers; j++) {
                payoff1 = r.nextInt(10);
                matrix[i][j][0] = payoff1;
                matrix[j][i][1] = payoff1;
                payoff2 = r.nextInt(10);
                matrix[i][j][1] = payoff2;
                matrix[j][i][0] = payoff2;
            }
        }
        printMatrix();

        addBehaviour(new WakerBehaviour(this, 15000) {
            private AID[] players;
            protected void handleElapsedTimeout() {
                System.out.println("> Searching...");
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
            }
        });
        System.out.println("Hello! " + getAID().getName() + "is ready.");
    }

    protected void takeDown() {
        System.out.println("Agent " + getAID().getName() + " terminating.");
    }

    private void printMatrix () {
        for (int i = 0; i < numberPlayers; i++) {
            for (int j = 0; j < numberPlayers; j++) {
                System.out.print(matrix[i][j][0] + "/" + matrix[i][j][1] + " ");
            }
            System.out.println();
        }
    }
}
