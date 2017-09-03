package putama.corePGM.factorGraph.structure;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;

/**
 * Created by prutama on 8/6/17.
 */
public class VariableNode extends Node {
    // the cardinality of a variable
    public int statesNum;
    // array of 0 or 1.0 that tells the state of the variable
    public double [] isStateObserved;

    public VariableNode(String name, int statesNum) {
        super(name);
        this.statesNum = statesNum;
        this.isStateObserved = new double[this.statesNum];
        this.setVariableLatent();
    }

    /**
     * only a single state is set to be 1.0
     * meaning that the others states will zero
     * other terms
     * @param stateIdx the state of the observed variable
     */
    public void setStateObserved(int stateIdx) {
        for (int i = 0; i < isStateObserved.length; i++) {
            this.isStateObserved[i] = 0;
        }
        this.isStateObserved[stateIdx] = 1.0;
    }

    /**
     * set the variable to be latent
     * i.e. all states contribute equally
     * during computing the message
     */
    public void setVariableLatent() {
        for (int i = 0; i < isStateObserved.length; i++) {
            this.isStateObserved[i] = 1.0;
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.setVariableLatent();
    }

    @Override
    public void sendSumProductMessage(Node otherNode) {
        if (otherNode.getNodeType().equals("VARIABLE")) {
            throw new IllegalArgumentException("factor can only send message to variable");
        }
        if (!neighbors.contains(otherNode)) {
            throw new IllegalArgumentException("other node should be in neighbors");
        }

        INDArray nodeMessage = Nd4j.ones(this.statesNum);

        // observed variable would have a single non-zero element in message vector
        nodeMessage = nodeMessage.mul(Nd4j.create(isStateObserved));

        if (neighbors.size() > 1) {
            if (!pendings.contains(otherNode)) {
                throw new IllegalArgumentException("incomplete received messages");
            }

            for (Node neighbor : neighbors) {
                if (messages.containsKey(neighbor)) {
                    nodeMessage = nodeMessage.mul(messages.get(neighbor));
                }
            }
        }

        this.passMessage(otherNode, nodeMessage);
    }

    @Override
    public void sendMaxSumMessage(Node otherNode) {

    }

    /**
     * get the marginal probability of the variable node
     * @return
     */
    public INDArray getMarginal() {
        // almost similar implementation with computing messages
        // except that the product should be normalized
        INDArray nodeMessage = Nd4j.ones(this.statesNum);
        // observed variable would have a single non-zero element in message vector
        nodeMessage = nodeMessage.mul(Nd4j.create(isStateObserved));
        for (Node neighbor : neighbors) {
            if (messages.containsKey(neighbor)) {
                nodeMessage = nodeMessage.mul(messages.get(neighbor));
            }
        }
        // compute normalizing constant
        double Z = nodeMessage.sumNumber().doubleValue();
        return nodeMessage.div(Z);
    }

    public String getNodeType(){
        return "VARIABLE";
    }
}
