import java.io.*;
import java.util.*;
import java.net.*;

public class DVObject implements Serializable {
    private static final long serialVersionUID = 6666666L;
    public int nodeID;
    public List<Integer> distanceVector = new ArrayList<>();
    public HashMap<Integer, InetAddress> neighborIPTable = new HashMap<>();
    public int[][] forwardingTable = new int[][]{
                                {0, 1, 3, 3, 3, 3, 3, 1, 8},
                                {0, 1, 7, 0, 0, 7, 7, 7, 0},
                                {3, 7, 2, 3, 3, 5, 5, 7, 3},
                                {0, 0, 2, 3, 4, 2, 2, 2, 0},
                                {3, 3, 3, 3, 4, 3, 3, 3, 3},
                                {2, 2, 2, 2, 2, 5, 6, 2, 2},
                                {5, 5, 5, 5, 5, 5, 6, 5, 5},
                                {1, 1, 2, 2, 2, 2, 2, 7, 1},
                                {0, 0, 0, 0, 0, 0, 0, 0, 8},
                            };
    public int[] nodeForwardingTable;

    public DVObject() {

    }

    public DVObject(int nodeID, List<Integer> distanceVector, HashMap<Integer, InetAddress> neighborIPTable) {
        this.nodeID = nodeID;
        this.distanceVector = distanceVector;
        this.neighborIPTable = neighborIPTable;
        this.nodeForwardingTable = forwardingTable[this.nodeID];
    }

}
