package putama.corePGM.factorGraph.structure;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

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
        if (otherNode.getNodeType().equals("FACTOR")) {
            throw new IllegalArgumentException("factor can only send message to variable");
        }
        if (!neighbors.contains(otherNode)) {
            throw new IllegalArgumentException("other node should be in neighbors");
        }

        INDArray nodeMessage = null;
        if (neighbors.size() == 1) {
            Nd4j.copy(factors, nodeMessage);
        } else {
            double [] messagesProduct = allMessagesProduct();
            int dim = neighbors.indexOf(otherNode);
            int dimSize = factors.shape()[dim];
            double [] toSend = new double[dimSize];
            for (int i = 0; i < dimSize; i++) {
                toSend[i] = factors.slice(i, dim).reshape(1, messagesProduct.length)
                        .mmul(Nd4j.create(messagesProduct, new int [] {messagesProduct.length, 1}))
                        .getDouble(0);
            }
        }
    }

    public void sendMaxSumMessage(Node otherNode) {

    }

    public String getNodeType(){
        return "FACTOR";
    }
}
