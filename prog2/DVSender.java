//package Program2;

import java.net.*;
import java.io.*;
import java.util.*;

public class DVSender
{
	MulticastSocket multOutSocket;
	InetAddress address;
	
	public DVSender(MulticastSocket mos, InetAddress ip)
	{
		multOutSocket = mos;
		address = ip;
	}

	public void send2Neighbor(byte[] payload) throws IOException
	{
		DatagramPacket p = new DatagramPacket(payload, payload.length, address, 11488);
		
        // System.out.println("DVSender sends: " + new String(p.getData()).trim() + "\nto: " + p.getAddress().getHostAddress() + ":" + p.getPort());

		multOutSocket.send(p);
		
		System.out.println("Sender working.");	
		
	}
}
