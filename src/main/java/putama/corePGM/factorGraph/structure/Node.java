package putama.corePGM.factorGraph.structure;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by prutama on 8/6/17.
 */
public abstract class Node implements Comparable<Node> {
    public String name;
    // list of of neighboring nodes (both factor or variable nodes)
    public ArrayList<Node> neighbors;
    // set of neighbors for which a message is pending to be sent
    public HashSet<Node> pendings;
    // tables that contain messages retrieved from neighbors
    public HashMap<Node, INDArray> messages;

    public Node(String name) {
        this.name = name;
        this.neighbors = new ArrayList<Node>();
        this.messages = new HashMap<Node, INDArray>();
        this.pendings = new HashSet<Node>();
    }

    /**
     * reset messages retrieved from neighboring nodes
     */
    public void reset() {
        this.messages.clear();
        this.pendings.clear();
    }

    public void addNeighbor(Node newNode) {
        this.neighbors.add(newNode);
    }

    public void receiveMessage(Node otherNode, INDArray message) {
        this.messages.put(otherNode, message);
        for (Node neighbor : neighbors) {
            // iterate trough all neighbors except otherNode
            if (neighbor == otherNode) {
                continue;
            }
            // if no message has been received from this neighbor
            if (!messages.containsKey(neighbor)){
                // node ready to send message to a single neighbor node x
                // once node received messages from all neighbors
                // except from node x
                if (neighbors.size()-1 == messages.size()) {
                    pendings.add(neighbor);
                }
            } else {
                // alternate condition when the node received
                // messages from all of its neighbors
                // this happens to root node during belief prop
                if (neighbors.size() == messages.size()) {
                    pendings.add(neighbor);
                }
            }
        }
    }

    public ArrayList<INDArray> getMessages(Node otherNode) throws Exception {
        // explicitly retrieve the value of the messages
        // from neighbors except from node other
        ArrayList<INDArray> messagesList = new ArrayList<INDArray>();
        for (Node neighbor : neighbors) {
            // iterate trough all neighbors except otherNode
            if (neighbor == otherNode) {
                continue;
            }
            if (messages.containsKey(neighbor)){
                messagesList.add(messages.get(neighbor));
            } else {
                throw new Exception("Missing messages from node: " + neighbor.toString());
            }
        }
        return messagesList;
    }

    public void passMessage(Node otherNode, INDArray message) {
        // explicitly pass the message from this node
        // to the other node
        otherNode.receiveMessage(this, message);
        this.pendings.remove(otherNode);
    }

    public abstract void sendSumProductMessage(Node otherNode);

    public abstract void sendMaxSumMessage(Node otherNode);

    public int compareTo(Node node) {
        if (this.name.equals(node.name)) {
            return 0;
        }
        return -1;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
