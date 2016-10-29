package agent;

import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class Random extends Agent {
    private int id; //Id
    private int numberPlayers; //N
    private int matrixSize; //S
    private int numberRounds; //R
    private int numberIterations; //I
    private int percentage; //P
    private int position;
    private int payoff = 0;

    @Override
    protected void setup() {
        System.out.print("> Registering in the DF... ");
        register();
        System.out.println("Done!");

        addBehaviour(new WakerBehaviour(this, 0) {
            protected void handleElapsedTimeout() {
                System.out.print("> Receiving ID message... ");
                ACLMessage msg = myAgent.blockingReceive();
                System.out.print("Done! ");
                if (msg != null) {
                    System.out.print("Processing message... ");
                    processIdMessage(msg);
                    System.out.println("Done!");
                    System.out.println("This is PLAYER #" + id);
                    System.out.println("The game is " + numberPlayers + " players, " + matrixSize + " matrix size, " + numberRounds + " number of rounds, " + numberIterations + " number of iterations and " + percentage + " percentage.");
                }

                System.out.print("> Receiving NewGame message... ");
                msg = myAgent.blockingReceive();
                System.out.print("Done! ");
                if (msg != null) {
                    System.out.println("Processing message... ");
                    processNewGameMessage(msg);
                    System.out.println("Done!");
                }

                System.out.print("Starting game.");
                for (int i = 0; i < numberRounds; i++) {
                    position = new java.util.Random().nextInt(matrixSize);
                    System.out.println("I have chosen to play " + position);
                    msg = myAgent.blockingReceive();
                    if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
                        ACLMessage rsp = new ACLMessage(ACLMessage.REQUEST);
                        rsp.addReceiver(msg.getSender());
                        rsp.setContent("Position#" + position);
                        send(rsp);
                    }
                    msg = myAgent.blockingReceive();
                    if(msg != null) {
                        payoff += Integer.parseInt(msg.getContent().split("#")[2].split(",")[id]);
                        System.out.println("Payoff for this round is " + payoff);
                    }

                }
            }
        });
        System.out.println("Hello! " + getAID().getName() + "is ready.");
    }

    private void processIdMessage (ACLMessage msg) {
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
        String content = msg.getContent();
        int player1 = Integer.parseInt(content.split("#")[1]);
        int player2 = Integer.parseInt(content.split("#")[2]);
        System.out.println("Players for this game are " + player1 + " and " + player2);
    }

    protected void takeDown () {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("Agent " + getAID().getName() + " terminating.");
    }

    private void register () {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Player");
        sd.setName("Fixed player");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
