package putama.corePGM.factorGraph.structure;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.ArrayList;

/**
 * Created by prutama on 8/6/17.
 */
public class FactorNode extends Node {
    public INDArray factors;

    public FactorNode(String name, INDArray factors, VariableNode [] neighbors) {
        super(name);

        int [] shape = factors.shape();
        // case when factors is a vector
        if (factors.shape().length==2 & factors.shape()[0]==1){
            shape = new int[] {factors.length()};
        }

        // factors shape assertion
        int dim = shape.length;
        if (neighbors.length != dim) {
            throw new IllegalArgumentException("factors shape should be matched with the number of neighbors");
        }

        // ndarray size assertion
        for (int i = 0; i < neighbors.length; i++) {
            if (((VariableNode) neighbors[i]).statesNum != shape[i]) {
                throw new IllegalArgumentException("factors shape should be matched with variable number of state");
            }

            this.addNeighbor(neighbors[i]);
        }

        this.factors = factors;
    }

    public void sendSumProductMessage(Node otherNode) {

    }

    public void sendMaxSumMessage(Node otherNode) {

    }
}
