import java.io.*;
import java.net.*;
import java.util.*;


public class FWNode {

    public static DatagramSocket socket;
    public static InetAddress address;
    public static int portNumber;
    public static DVObject dvo;
    public static int myPortNumber = 13200;
	public static List<PortUser> portUserArray = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
    		System.out.println("Usage: java DVNode <DVCoordinator IP> <DVCoordinator port number>");
    		System.exit(0);
        }

        
        // open socket
		try {
			URL whatIsMyIp = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIp.openStream()));
			String ip = in.readLine();
			System.out.println("Node running on IP: " + ip);

			address = InetAddress.getByName(args[0]);
			portNumber = Integer.parseInt(args[1]);

			socket = new DatagramSocket();
		} catch (MalformedURLException murle) {
			System.err.println("Caught Exception: " + murle.getMessage());
			murle.printStackTrace();	
		}	

        // create empty packet and send to coordinator
	    byte[] bBuffer = new byte[2048];
        DatagramPacket packet = new DatagramPacket(bBuffer, bBuffer.length, address, portNumber);
        socket.send(packet);
        System.out.println("Communication initialized.");

		socket.receive(packet);
		System.out.println("Distance Vector received from coordinator.");

        try {
            ByteArrayInputStream buffer = new ByteArrayInputStream(bBuffer);
            ObjectInputStream in = new ObjectInputStream(buffer);
            dvo = (DVObject) in.readObject();
            buffer.close();
            in.close();
		} catch (ClassNotFoundException cnfe) {
			System.err.println("Caught Exception: " + cnfe.getMessage());
			cnfe.printStackTrace();	
		}

        System.out.println("I am node number " + dvo.nodeID + ".");
        System.out.println(dvo.distanceVector);
        System.out.println(dvo.neighborIPTable);


        myPortNumber += dvo.nodeID;

		DataPlanePort dpp = new DataPlanePort(myPortNumber, dvo.neighborIPTable.size());

		Thread dppThread = new Thread(dpp);
		
		dppThread.start();
		
		myPortNumber -= dvo.nodeID;
        

		Integer[] keyArray = dvo.neighborIPTable.keySet().toArray(new Integer[dvo.neighborIPTable.size()]);
		
		//fill up portUserArray array with portusers according to how many neighbors we have. Need a portUser for each neighbor
		for(int i = 0; i < dvo.neighborIPTable.size(); i++)
		{
            System.out.println(keyArray[i]);
            System.out.println(dvo.neighborIPTable.get(keyArray[i]).getHostAddress());
            // PortUser portUser = new PortUser(0, "137.30.212.13", myPortNumber + keyArray[i]);
			portUserArray.add(new PortUser(keyArray[i], dvo.neighborIPTable.get(keyArray[i]).getHostAddress(), myPortNumber + keyArray[i]));

			try
			{
				portUserArray.get(i).initialize();
			}
			catch(InterruptedException ie)
			{
				System.err.println("Caught Exception: " + ie.getMessage());
				ie.printStackTrace();	
			}	
		}

        // int i = 0;
        // for (Map.Entry<Integer, InetAddress> entry : dvo.neighborIPTable.entrySet()) {
        //     portUserArray[i] = new PortUser(entry.getKey(), entry.getValue().toString(), myPortNumber + entry.getKey());
        //     i++;
        //     try {
        //         portUserArray[i].initialize();
        //     } catch (InterruptedException ie) {
				// System.err.println("Caught Exception: " + ie.getMessage());
				// ie.printStackTrace();	
        //     }
        // }

        String payload = "This is a test.";

        
		if(dvo.nodeID == 8)
		{
			MessageType message = new MessageType(dvo.nodeID, 6, payload.getBytes());

            int i = 0;
            while(true) {
				if (portUserArray.get(i).getNodeID() == dvo.nodeForwardingTable[6]) {
					portUserArray.get(i).send(message);
                    break;
				}
				i++;
            }
		}

		while(true)
		{
			
			MessageType message = new MessageType(0, 0, null);
			
			try {
				message = dpp.receive();
			} catch(InterruptedException ie) {
				System.err.println("Caught Exception: " + ie.getMessage());
				ie.printStackTrace();	
			}	

			int destNode = message.getDestNode();

			//ifit for us we print a message
			if(dvo.nodeID == destNode)
			{
				System.out.println("Message Received.");
			}
			else //if it is not for us we forward it using our forwarding table
			{
				for(int i = 0; i < portUserArray.size(); i++)
				{
					if(portUserArray.get(i).getNodeID() == dvo.nodeForwardingTable[6])
					{
						portUserArray.get(i).send(message);
					}
				}	
			}
		}	

		// for(int i = 0; i < dvo.neighborIPTable.size(); i++)
		// {
		// 	portUserArray[i] = new PortUser(keyArray[i], dvi.getNeighborIPTable().get(keyArray[i]).toString(), myPortNumber + keyArray[i]);

		// 	try
		// 	{
		// 		portUserArray[i].initialize();
		// 	}
		// 	catch(InterruptedException ie)
		// 	{
		// 		System.err.println("Caught Exception: " + ie.getMessage());
		// 		ie.printStackTrace();	
		// 	}	
		// }



    
        // multiInSocket = new MulticastSocket(13288);

        // multiOutSocket = new MulticastSocket(13288);

		// ipMultiCastOut = "230.132.0.00";

		// ipMultiCastIn = "230.132.7.00";

        // initMultiCastIn();

        // initMultiCastOut();

        // DVSender sender = new DVSender(multiOutSocket, InetAddress.getByName(ipMultiCastOut));

		// DVReceiver receiver = new DVReceiver(multiInSocket);

		// byte[] dvByteArray = new byte[1024];

        // ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        // ObjectOutputStream out = new ObjectOutputStream(buffer);
        // out.writeObject(dvo);
        // byte[] bytesOut = buffer.toByteArray();

		// sender.send2Neighbor(bytesOut);

		// while(true)
		// {	
		// 	dvByteArray = receiver.receiveFromNeighbor(dvByteArray.length);
            // List<Integer> dv = new ArrayList<Integer>();
            // for (Byte b : dvByteArray) {
                // dv.add(b == null ? null : b.intValue());
            // }
            // System.out.println(dv);
            // System.out.println("test");
		// }
    }

    // public static void initMultiCastIn() {
    //     for (int i = 0; i < dvo.neighborIPTable.size(); i++) {
			// try {
    //             ipMultiCastIn = ipMultiCastIn.concat(String.valueOf(dvo.nodeID));
				// multiInSocket.joinGroup(InetAddress.getByName(ipMultiCastIn));
    //             System.out.println("Multi In IP: " + ipMultiCastIn);
			// } catch (UnknownHostException uhe) {
				// System.err.println("Caught Exception: " + uhe.getMessage());
				// uhe.printStackTrace();	
			// } catch (IOException ioe) {
				// System.err.println("Caught Exception: " + ioe.getMessage());
				// ioe.printStackTrace();	
			// }
    //     }
    //     ipMultiCastIn = "230.132.7.00";
    // }
    
	// public static void initMultiCastOut() {
		// ipMultiCastOut = ipMultiCastOut.concat(String.valueOf(dvo.nodeID));

		// try {
			// multiOutSocket.joinGroup(InetAddress.getByName(ipMultiCastOut));
    //         System.out.println("Multi Out IP: " + ipMultiCastOut);
		// } catch (UnknownHostException uhe) {
			// System.err.println("Caught Exception: " + uhe.getMessage());
			// uhe.printStackTrace();	
		// } catch(IOException ioe) {
			// System.err.println("Caught Exception: " + ioe.getMessage());
			// ioe.printStackTrace();	
		// }
		// ipMultiCastOut = "230.132.0.00";
	// }

}
