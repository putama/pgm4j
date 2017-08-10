package putama.corePGM.factorGraph.structure;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Created by prutama on 8/6/17.
 */
public class FactorGraph {
    public TreeSet<Node> allNodes;

    public static void main(String [] args) {
        FactorGraph graph = new FactorGraph();
        VariableNode node1 = new VariableNode("P", 2);
        VariableNode node2 = new VariableNode("S", 2);

        graph.addNode(node1);
        graph.addNode(node2);

        INDArray a = Nd4j.zeros(3,3,3);
        System.out.println(a.shape()[0]);
    }

    public FactorGraph() {
        allNodes = new TreeSet<Node>();
    }

    public void addNode(Node newNode) {
        this.allNodes.add(newNode);
    }
}
