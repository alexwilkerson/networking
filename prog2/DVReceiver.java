//package Program2;

import java.net.*;
import java.io.*;
import java.util.*;

public class DVReceiver
{
	MulticastSocket multInSocket;
	
	public DVReceiver(MulticastSocket mis)
	{
		multInSocket = mis;
	}
	
	public byte[] receiveFromNeighbor(int packSize)
	{
		DatagramPacket inPack = null;
		
		byte[] buffer = new byte[packSize];
		
		try
		{ 
			inPack = new DatagramPacket(buffer, buffer.length);

			System.out.println("Listening...");	

			multInSocket.receive(inPack);

			System.out.println("Receieved a packet.");	
		}
		catch(UnknownHostException uhe)
		{
			uhe.printStackTrace();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		byte[] data = inPack.getData();

		return data;
	}
} 
