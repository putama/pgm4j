package putama.corePGM.factorGraph.structure;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.accum.Dot;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Created by prutama on 8/6/17.
 */
public class FactorGraph {
    public TreeSet<Node> allNodes;

    public static void main(String [] args) {
        FactorGraph graph = new FactorGraph();
        VariableNode I = new VariableNode("Influenza", 2);
        VariableNode S = new VariableNode("Smokes", 2);
        VariableNode ST = new VariableNode("Sore Throat", 2);
        VariableNode F = new VariableNode("Fever", 2);
        VariableNode B = new VariableNode("Bronchitis", 2);
        VariableNode C = new VariableNode("Coughing", 2);
        VariableNode W = new VariableNode("Wheezing", 2);

        FactorNode F_I = new FactorNode(
                "F_I",
                Nd4j.create(new double[] {0.95, 0.05}),
                new VariableNode[] {I}
        );
        FactorNode F_S = new FactorNode(
                "F_S",
                Nd4j.create(new double[] {0.8, 0.2}),
                new VariableNode[] {S}
        );
        FactorNode F_IST = new FactorNode(
                "F_IST",
                Nd4j.create(new double[][] {{0.999, 0.001}, {0.7, 0.3}}),
                new VariableNode[] {I, ST}
        );
        FactorNode F_IF = new FactorNode(
                "F_IF",
                Nd4j.create(new double[][] {{0.1, 0.9}, {0.95, 0.05}}),
                new VariableNode[] {I, F}
        );
        FactorNode F_ISB = new FactorNode(
                "F_ISB",
                Nd4j.create(
                        new double[] {0.999,0.001,0.3,0.7,0.1,0.9,0.01,0.99},
                        new int[] {2,2,2}
                ),
                new VariableNode[] {I, S, B}
        );
        FactorNode F_BC = new FactorNode(
                "F_BC",
                Nd4j.create(new double[][] {{0.999, 0.001}, {0.7, 0.3}}),
                new VariableNode[] {I, ST}
        );
        graph.addNodes(I, S, ST, F, B, C, W);
        graph.addNodes(F_I, F_S, F_IST, F_IF, F_ISB, F_BC);

        F_ISB.reset();
        F_ISB.receiveMessage(I, Nd4j.create(new double[] {1, 2}));
        F_ISB.receiveMessage(B, Nd4j.create(new double[] {1, 2}));
        F_ISB.sendSumProductMessage(S);
    }

    public static double [] toPrimitive(ArrayList<Double> arr) {
        double [] ret = new double[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            ret[i] = arr.get(i);
        }
        return ret;
    }



    public FactorGraph() {
        allNodes = new TreeSet<Node>();
    }

    public void addNode(Node newNode) {
        this.allNodes.add(newNode);
    }

    public void addNodes(Node... newNodes) {
        for (Node newNode : newNodes) {
            addNode(newNode);
        }
    }
}
