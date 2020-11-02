package Assignment3Game;
import java.net.*;
import java.io.*;

public class GameServer {
    public static void main (String[] args){
        if(args.length != 1){
            System.err.println ("Usage: java GameServer <port number>");
            System.exit(1);
        } //end if

        Socket clientSocket;
        int intPortNumber = Integer.parseInt(args[0]);
        try {
            // create socket:
            ServerSocket serv = new ServerSocket(intPortNumber);
            // create client socket:
            clientSocket = serv.accept();

            // Receive number of tiles 
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            Integer intNumTiles = (Integer) in.readObject();
            System.out.println("Received the Integer "+ intNumTiles);

        } catch (Exception e) {
            System.out.println("Error creating a socket");
        } //end catch

    } //end main
} //end of class