import java.io.IOException;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.lang.Thread;
import java.net.SocketException;
import java.net.*;
import java.io.*;

public class DataSender {
    public static final int[] integers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};

    public static void main (String args[]) throws IOException, InterruptedException {
        ArrayList<Integer> rates = new ArrayList<>();



        // Verify that the correct number of arguments have been listed
        if (args.length < 2) {
            System.out.println("Usage: java DataSender [port] [d1] [d2] [d3] ... [dk]");
            System.exit(0);
        }
        int port = Integer.parseInt(args[0]);
        int interval = Integer.parseInt(args[1]);
        for (int i = 2; i < args.length; i++) {
            int rate = Integer.parseInt(args[i]);
            rates.add(rate);
        }
        System.out.println(rates);

        try {
			URL whatIsMyIp = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIp.openStream()));
			System.out.println("Running on IP: " + in.readLine());

			in.close();
		} catch (MalformedURLException murle) {
			System.err.println("Caught Exception: " + murle.getMessage());
			murle.printStackTrace();
		} catch (IOException ioe) {
			System.err.println("Caught Exception: " + ioe.getMessage());
			ioe.printStackTrace();
		}


        ServerSocket listener = new ServerSocket(port);
        System.out.println("DataSender listening on port " + String.valueOf(port) + ".");
        Socket socket = listener.accept();
        try {
            while (true) {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                int count = 1;
                try {
                    while (true) {
                        for (int rate : rates) {
                            for (int i = interval * rate; i > 0; i -= interval) {
                                out.writeObject(integers);    
                                count++;
                                Thread.sleep((1000*interval)/rate);
                            }
                        }
                    }
                } catch (SocketException se) {
                    System.out.println("Socket error.");
                } finally {
                    out.close();
                }
            }
        } finally {
            socket.close();
            listener.close();
        }

    }

}
