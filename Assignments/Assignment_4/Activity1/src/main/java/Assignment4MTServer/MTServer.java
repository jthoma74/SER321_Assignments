package Assignment4MTServer;
import java.net.*;
import java.util.ArrayList;

public class MTServer {
    public static StringList strings = new StringList();
    public static ArrayList<MTConnection> lstConnections = new ArrayList<MTConnection>();
    public static int intThreadNum = 0;

    //Main method
    public static void main(String args[]) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: MTServer <port>");
            System.exit(1);
        } 

        try {
            ServerSocket socServer = new ServerSocket(Integer.parseInt(args[0]));
            System.out.println("Multithreaded Server Started...");
            while(true) {
                System.out.println("MTServer: Accepting a Request...");
                Socket soc = socServer.accept();
                System.out.println("MTServer: A client connection accepted");
                intThreadNum++;
                MTConnection mtConn = new MTConnection(soc,strings,intThreadNum);
                System.out.println("MTServer: Assigned socket to the new MTConnection");
                lstConnections.add(mtConn);
                System.out.println("MTServer: Added an MTConnection to the connections list");
                new Thread(mtConn).start();
                System.out.println("MTServer: Started MTConnection thread " + intThreadNum);
            } // end while loop
        } catch (Exception ex) {
            ex.printStackTrace();
        } //end of try-catch block
    } //end of main()

}
