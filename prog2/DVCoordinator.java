import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays.*;

/**
 * @author 
 * Alex Wilkerson
 */
public class DVCoordinator {
    public static DatagramSocket socket;
    public static List<List<Integer>> adjacencyList = new ArrayList<>();
	public static HashMap<Integer, InetAddress> nodeIPTable = new HashMap<>();
	public static HashMap<Integer, Integer> nodePortTable = new HashMap<>();

    public static void main(String args[]){
        if (args.length != 1) {
            System.out.println("Usage: java DVCoordinator <adjacency list file>");
            System.exit(0);
        }

        File file = new File(args[0]);
        System.out.println("file: " + file);
        readAdjacencyFile(file);

        // get IP from amazonaws
        try {
			URL whatIsMyIp = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIp.openStream()));
			System.out.println("DVCoordinator running on IP: " + in.readLine());

			in.close();
		} catch (MalformedURLException murle) {
			System.err.println("Caught Exception: " + murle.getMessage());
			murle.printStackTrace();
		} catch (IOException ioe) {
			System.err.println("Caught Exception: " + ioe.getMessage());
			ioe.printStackTrace();
		}

        int nodeID = 0;

        // open socket
		try {
            int portNumber = 60666;
			socket = new DatagramSocket(portNumber);
            System.out.println("Port number: " + portNumber);
		} catch(SocketException se) {
			System.err.println("Caught Exception: " + se.getMessage());
			se.printStackTrace();	
		}

        // wait for all nodes to connect
        for (int i = 0; i < adjacencyList.size(); i++) {
            DatagramPacket dgp = listen();
            nodeIPTable.put(i, dgp.getAddress());
            nodePortTable.put(i, dgp.getPort());
        }

        // respond to each node in nodeIPTable
        for (int i = 0; i < nodeIPTable.size(); i++) {
            HashMap<Integer, InetAddress> neighborIPTable = new HashMap<Integer, InetAddress>();
            for (int j = 0; j < nodeIPTable.size(); j++) {
				if(adjacencyList.get(i).get(j) != 0 && adjacencyList.get(i).get(j) != Integer.MAX_VALUE) {
					neighborIPTable.put(j, nodeIPTable.get(j));
				}
            }

            DVObject dvo = new DVObject(i, adjacencyList.get(i), neighborIPTable);

            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(buffer);
                out.writeObject(dvo);
                out.close();
                buffer.close();
                DatagramPacket packet = new DatagramPacket(buffer.toByteArray(), buffer.size(), nodeIPTable.get(i), nodePortTable.get(i));
                System.out.println("Sending DVObject to node " + i + " on port " + nodePortTable.get(i) + ".");
                socket.send(packet);
            } catch (IOException ioe) {
                System.err.println("Caught Exception: " + ioe.getMessage());
                ioe.printStackTrace();	
            }
            neighborIPTable.clear();
        }

    }

    public static void readAdjacencyFile(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(",");
                List<Integer> distances = new ArrayList<>();
                for (String distance : splitLine) {
                    int d = Integer.parseInt(distance.trim());
                    distances.add(d == -1 ? Integer.MAX_VALUE : d);
                }
                adjacencyList.add(distances);
            }
        } catch (FileNotFoundException fnfe) {
			System.err.println("Caught Exception: " + fnfe.getMessage());
			fnfe.printStackTrace();	
        } catch (Exception e) {
			System.err.println("Caught Exception: " + e.getMessage());
			e.printStackTrace();	
        }
    }

	public static DatagramPacket listen() {
		byte[] buf = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		System.out.println("Listening...");
		
		try {
			socket.receive(packet);
            System.out.println("Received connection from node.");
		} catch(IOException ioe) {
			System.err.println("Caught Exception: " + ioe.getMessage());
			ioe.printStackTrace();	
		}

		return packet;
    }


}
