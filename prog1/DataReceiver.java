import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.net.ConnectException;
import java.io.ObjectInputStream;
import java.lang.ClassNotFoundException;

public class DataReceiver {
    private static int count = 0;
    private static boolean started = false;

    public static void main(String args[]) throws IOException, ConnectException, ClassNotFoundException {


        if (args.length < 3) {
            System.out.println("Usage: java DataReceiver [ip] [port] [interval]");
            System.exit(1);
        }

        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        int interval = Integer.parseInt(args[2]);

        Socket socket = new Socket(ip, port);
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        Timer intervalTimer = new Timer();
        intervalTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (started == false) {
                    System.out.println("Recording data stream. Reporting every " + String.valueOf(interval) + " seconds.");
                    started = true;
                } else {
                    System.out.println("Received " + String.valueOf(count) + " packets in " + String.valueOf(interval) + " second(s) at an average rate of " + String.valueOf(count/interval) + " packets per second.");
                }
                count = 0;
            }
        }, 0, interval*1000);

        int[] integers;
        while ((integers = (int[]) in.readObject()) != null) {
            count++;
        }

    }
}
