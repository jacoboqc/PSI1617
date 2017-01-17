package agent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Intelligent extends Agent {
    private int id; //Id
    private int numberPlayers; //N
    private int matrixSize; //S
    private int numberRounds; //R
    private int numberIterations; //I
    private int percentage; //P
    private int position;
    private int[][][] matrix;

    private boolean firstPlay = true;
    private String playFor;

    @Override
    protected void setup() {
        register();
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = myAgent.blockingReceive();
                if (msg != null && msg.getContent().startsWith("Id")) {
                    processIdMessage(msg);
                } else if (msg != null && msg.getContent().startsWith("NewGame")) {
                    processNewGameMessage(msg);
                    initializeMatrix();
                } else if (msg != null && msg.getContent().startsWith("Position")) {
                    position = choosePosition();
                    ACLMessage rsp = new ACLMessage(ACLMessage.REQUEST);
                    rsp.addReceiver(msg.getSender());
                    rsp.setContent("Position#" + position);
                    send(rsp);
                } else if (msg != null && msg.getContent().startsWith("Results")) {
                    processResultsMessage(msg);
                }else if (msg != null && msg.getContent().startsWith("Changed")) {
                    initializeMatrix();
                } else if (msg != null && msg.getContent().equals("EndGame")) {
                    while (myAgent.receive() != null) {}
                    firstPlay = true;
                }
            }
        });
        System.out.println("Hello! " + getAID().getName() + "is ready.");
    }

    private void initializeMatrix() {
        matrix = new int[matrixSize][matrixSize][2];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                Arrays.fill(matrix[i][j], 0);
            }
        }
    }

    private void processIdMessage(ACLMessage msg) {
        String content = msg.getContent();
        id = Integer.parseInt(content.split("#")[1]);
        String[] parameters = content.split("#")[2].split(",");
        numberPlayers = Integer.parseInt(parameters[0]);
        matrixSize = Integer.parseInt(parameters[1]);
        numberRounds = Integer.parseInt(parameters[2]);
        numberIterations = Integer.parseInt(parameters[3]);
        percentage = Integer.parseInt(parameters[4]);
    }

    private void processNewGameMessage(ACLMessage msg) {
        int id1, id2, mine, his;
        String content = msg.getContent();
        id1 = Integer.parseInt(content.split("#")[1].split(",")[0]);
        id2 = Integer.parseInt(content.split("#")[1].split(",")[1]);
        mine = id1 == id ? id1 : id2;
        his = mine == id1 ? id2 : id1;
        playFor = mine < his ? "ROWS" : "COLUMNS";
    }

    private void processResultsMessage(ACLMessage msg) {
        int pos1, pos2, pay1, pay2;
        String content = msg.getContent();
        pos1 = Integer.parseInt(content.split("#")[1].split(",")[0]);
        pos2 = Integer.parseInt(content.split("#")[1].split(",")[1]);
        pay1 = Integer.parseInt(content.split("#")[2].split(",")[0]);
        pay2 = Integer.parseInt(content.split("#")[2].split(",")[1]);
        matrix[pos1][pos2] = new int[] {pay1, pay2};
    }

    public int choosePosition() {
        if (firstPlay) {
            firstPlay = false;
            return new java.util.Random().nextInt(matrixSize);
        }
        List<Integer> diffs = new ArrayList<>();
        for (int i = 0; i < matrixSize; i++) {
            int pay1 = 0, pay2 = 0, diff;
            switch (playFor.toUpperCase()) {
                case "ROWS":
                    for (int j = 0; j < matrixSize; j++) {
                        pay1 += matrix[i][j][0];
                        pay2 += matrix[i][j][1];
                    }
                    diff = pay1 - pay2;
                    diffs.add(i, diff);
                    break;
                case "COLUMNS":
                    for (int j = 0; j < matrixSize; j++) {
                        pay1 += matrix[j][i][0];
                        pay2 += matrix[j][i][1];
                    }
                    diff = pay1 - pay2;
                    diffs.add(i, diff);
                    break;
            }
        }
        int maxDiff = Collections.max(diffs);
        return diffs.indexOf(maxDiff);
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("Agent " + getAID().getName() + " terminating.");
    }

    private void register() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Player");
        sd.setName("Intelligent player");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
