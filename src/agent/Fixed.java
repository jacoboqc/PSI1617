package agent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class Fixed extends Agent {
    private int id; //Id
    private int numberPlayers; //N
    private int matrixSize; //S
    private int numberRounds; //R
    private int numberIterations; //I
    private int percentage; //P
    private int position;

    @Override
    protected void setup() {
        register();
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = myAgent.blockingReceive();
                if (msg != null && msg.getContent().startsWith("Id")) {
                    processIdMessage(msg);
                } else if (msg != null && msg.getContent().startsWith("NewGame")) {
                    position = new Random().nextInt(matrixSize);
                } else if (msg != null && msg.getContent().startsWith("Position")) {
                    ACLMessage rsp = new ACLMessage(ACLMessage.REQUEST);
                    rsp.addReceiver(msg.getSender());
                    rsp.setContent("Position#" + position);
                    send(rsp);
                } else if (msg != null && msg.getContent().equals("EndGame")) {
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
