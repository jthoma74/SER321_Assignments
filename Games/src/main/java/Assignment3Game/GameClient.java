package Assignment3Game;
import java.net.*;
import java.io.*;

public class GameClient {
    public static void main (String[] args) {
        // Check for two args: Host and port number
        if(args.length != 2){
            System.err.println ("Usage: java GameClient <host> <port number>.");
            System.exit(1);
        } //end if

        Socket socClient = null;
        String strHostName = args[0];
        int intPortNumber = Integer.parseInt(args[1]);

        try {
            socClient = new Socket(strHostName, intPortNumber);
            System.out.println("Socket created");
            System.out.println("Enter number of image tiles: ");

            ObjectInputStream in = new ObjectInputStream(socClient.getInputStream());
            int numTiles = (Integer) in.readObject();
            System.out.println("You entered: " + numTiles);

            
        } catch (Exception e) {
            System.out.println("Client Socket create error");
        } //end catch
    } // end main 
} //end class