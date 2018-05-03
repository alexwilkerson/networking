import java.io.*;
import java.util.*;
import java.net.*;

public class DVObject implements Serializable {
    private static final long serialVersionUID = 6666666L;
    public int nodeID;
    public List<Integer> distanceVector = new ArrayList<>();
    public HashMap<Integer, InetAddress> neighborIPTable = new HashMap<>();

    public DVObject() {

    }

    public DVObject(int nodeID, List<Integer> distanceVector, HashMap<Integer, InetAddress> neighborIPTable) {
        this.nodeID = nodeID;
        this.distanceVector = distanceVector;
        this.neighborIPTable = neighborIPTable;
    }

}
