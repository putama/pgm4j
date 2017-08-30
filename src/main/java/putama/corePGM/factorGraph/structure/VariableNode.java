package putama.corePGM.factorGraph.structure;

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

    }

    @Override
    public void sendMaxSumMessage(Node otherNode) {

    }

    /**
     * get the marginal probability of the variable node
     * @param Z: normalization constant
     * @return
     */
    public float getMarginal(float Z) {
        return 0;
    }

    public String getNodeType(){
        return "VARIABLE";
    }
}
