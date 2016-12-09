package agent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Arrays;
import java.util.Random;

public class Fixed extends Agent {
    private int id; //Id
    private int numberPlayers; //N
    private int matrixSize; //S
    private int numberRounds; //R
    private int numberIterations; //I
    private int percentage; //P
    private int position;
    private int payoff;

    @Override
    protected void setup() {
        System.out.print("> Registering in the DF... ");
        register();
        System.out.println("Done!");
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                MessageTemplate mt = null;
                ACLMessage msg = myAgent.blockingReceive(mt);
                if (msg != null && msg.getContent().startsWith("Id#")) {
                    System.out.print("Processing ID message... ");
                    processIdMessage(msg);
                    System.out.println("Done!");
                    System.out.println("This is PLAYER #" + id);
                    System.out.println("The game is " + numberPlayers + " players, " + matrixSize + " matrix size, " + numberRounds + " number of rounds, " + numberIterations + " number of iterations and " + percentage + " percentage.");
                    mt = MessageTemplate.not(MessageTemplate.MatchContent("Changed#" + percentage));
                    msg = myAgent.blockingReceive(mt);
                    System.out.println("Processing NewGame message... ");
                    processNewGameMessage(msg);
                    System.out.println("Done!");
                } else if (msg != null && msg.getContent().startsWith("NewGame#")) {
                    mt = MessageTemplate.not(MessageTemplate.MatchContent("Changed#" + percentage));
                    System.out.println("Processing NewGame message... ");
                    processNewGameMessage(msg);
                    System.out.println("Done!");
                }

                position = new Random().nextInt(matrixSize);
                System.out.println("I have chosen to play " + position);

                System.out.println("Starting game.");
                payoff = 0;
                for (int i = 0; i < numberRounds; i++) {
                    msg = myAgent.blockingReceive(mt);
                    if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
                        ACLMessage rsp = new ACLMessage(ACLMessage.REQUEST);
                        rsp.addReceiver(msg.getSender());
                        rsp.setContent("Position#" + position);
                        send(rsp);
                    }
                    msg = myAgent.blockingReceive(mt);
                    if(msg != null) {
                        String[] positions = msg.getContent().split("#")[1].split(",");
                        int payoffIndex = Arrays.asList(positions).indexOf(Integer.toString(position));
                        payoff += Integer.parseInt(msg.getContent().split("#")[2].split(",")[payoffIndex]);
                        System.out.println("Payoff after this round is " + payoff);
                    }
                }
                msg = myAgent.blockingReceive(mt);
                if (msg != null && msg.getContent().equals("EndGame")) {
                    System.out.println("Game over!.");
                    while (myAgent.receive() != null) {}
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
