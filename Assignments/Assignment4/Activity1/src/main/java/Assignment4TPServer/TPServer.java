package Assignment4TPServer;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TPServer {
    public static StringList strings = new StringList();
    public static int intThreadNum = 0;

    //Main method
    public static void main(String args[]) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: TPServer <port> <threads>");
            System.exit(1);
        }

        int intPort = Integer.parseInt(args[0]);
        int intNumThreads = Integer.parseInt(args[1]);
        ExecutorService exeService = Executors.newFixedThreadPool(intNumThreads);
        try {
            ServerSocket socServer = new ServerSocket(intPort);
            System.out.println("Threaded Pool Server Starting on port " + intPort +  " with  " + intNumThreads + " threads...");
            while(true) {
                System.out.println("TPServer: Accepting a Request...");
                Socket soc = socServer.accept();
                intThreadNum++;
                System.out.println("TPServer: A client connection accepted");
                exeService.submit(new TPConnection(soc, strings, intThreadNum));
                System.out.println("TPServer: Assigned socket to the new TPConnection" + intThreadNum);
            } // end while loop
        } catch (Exception ex) {
            ex.printStackTrace();
        } //end of try-catch block
    } //end of main()

} // end of class TPServer
