package putama.corePGM.factorGraph.structure;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.ArrayList;

/**
 * Created by prutama on 8/6/17.
 */
public class FactorNode extends Node {
    public INDArray factors;

    public FactorNode(String name, INDArray factors, ArrayList<VariableNode> neighbors) {
        super(name);

        // factors shape assertion
        int dim = factors.shape().length;
        if (neighbors.size() != dim) {
            throw new IllegalArgumentException("factors shape should be matched with the number of neighbors");
        }

        // ndarray size assertion
        for (int i = 0; i < neighbors.size(); i++) {
            if (((VariableNode) neighbors.get(i)).statesNum != factors.shape()[i]) {
                throw new IllegalArgumentException("factors shape should be matched with variable number of state");
            }

            this.addNeighbor(neighbors.get(i));
        }

        this.factors = factors;
    }

    public void sendSumProductMessage(Node otherNode) {

    }

    public void sendMaxSumMessage(Node otherNode) {

    }
}
