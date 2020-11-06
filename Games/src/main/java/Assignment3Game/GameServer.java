package Assignment3Game;
import java.net.*;
import Assignment3Game.GameProtocol;
import java.io.*;

public class GameServer {

    public static Message message;
    public static String strClientMsg;
    public static int intClientNumber;
    public static int intClientState;
    public static int intDimension;


    public String getClientMsg(){
        return strClientMsg;
    }

    public int getClientsNumber(){
        return intClientNumber;
    }

    public int getClientState(){
        return intClientState;
    }

    /**
     * Parse client message into it's pieces and copy it into the message on the server
     * for processing
     * 
     * @param clientMessage the message received fromt the client
     * @param serverMessage the server-side message for processing
     */
    public static void parseClientMessage(Message clientMessage){
        strClientMsg = clientMessage.getText();
        intClientNumber = clientMessage.getNumber();
        intClientState = clientMessage.getState();
    } //end parse Client Msg

    public static void main (String[] args){
        if(args.length != 1) {
            System.err.println ("Usage: java GameServer <port number>");
            System.exit(1);
        } //end if

        GameProtocol objProtocol = new GameProtocol();
        int intParcelCount = 0;
        Socket clientSocket;
        int intPortNumber = Integer.parseInt(args[0]);

        try {
            // create server socket:
            ServerSocket serv = new ServerSocket(intPortNumber);
            System.out.println("Server socket created.");
            
            // accept client connection:
            clientSocket = serv.accept();

            ObjectOutputStream objOutStream;
            ObjectInputStream objInStream;

            // Create object output stream to send messages to client socket
            objOutStream = new ObjectOutputStream(clientSocket.getOutputStream());
            System.out.println("objOutStream created");

            // Create Object Input Stream to read messages from server socket
            objInStream = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("objInStream created");

            // keep server running on loop until game is won or lost:

            // First argument received from client is the number of tiles:
            message = (Message) objInStream.readObject();
            intParcelCount++;
            System.out.println("Message parcel number :" +intParcelCount+ "received.");

            // Parse the message into its corresponding variables
            parseClientMessage(message);
            System.out.println("Parsed the Integer for "+ intClientNumber); //contains the dimensions

            // Set the dimensions for the image splitting:
            intDimension = intClientNumber;
            
            // Process the first message received: 
            message = objProtocol.protoProcess(message);            
            if (message.getState() == GameProtocol.FIRST_QN_SENT) {                    
                // Set message state to "SERVER_SENT" : 
                message.setState(GameProtocol.SERVER_SENT);
                System.out.println("\nSending String Message: " + message.getText());  
                System.out.println("Sending int Answer Flag: " + message.getNumber());
                System.out.println("Sending int Message State : " + message.getState());

                // No other changes to message. Push to socket:
                objOutStream.writeObject(message);
                System.out.println("Successfully Sent Message Packet");
            }

            while(true){  
                // First argument received from client is the number of tiles:
                message = (Message) objInStream.readObject();
                parseClientMessage(message);
                System.out.println("\nReceived String Message: " + message.getText());  
                System.out.println("Received int Answer Flag: " + message.getNumber());
                System.out.println("Received int Message State : " + message.getState());

                intParcelCount++;
                System.out.println("Message parcel number :" +intParcelCount+ " received.");

                // Process the message from client using the protocol and print to verify:
                if (message.getState() == GameProtocol.ANS_SENT) {
                    message = objProtocol.protoProcess(message);
                    System.out.println("\nProtocol Processed String Message: " + message.getText());  
                    System.out.println("Protocol processed int Answer Flag: " + message.getNumber());
                    System.out.println("Protocol processed int Message State : " + message.getState());
                }

                // If the protocol shows that the game is won or lost, break:
                if (message.getState() == GameProtocol.WIN || message.getState() == GameProtocol.LOST){
                    objOutStream.writeObject(message);
                    break; //end connection
                } else if (message.getState() == GameProtocol.QN_SENT && message.getNumber() == 1) { //if answer is correct AND a new qn has been sent                      
                        /**  
                         * STUB: Add image to the message
                         **/

                        message.setState(GameProtocol.SERVER_SENT);    
                        System.out.println("\nSending String Message: " + message.getText());  
                        System.out.println("Sending int Answer Flag: " + message.getNumber());
                        System.out.println("Sending int Message State : " + message.getState()); 
                        objOutStream.writeObject(message);                      
                    } else if (message.getState() == GameProtocol.QN_SENT && message.getNumber() == 0) { //if answer is incorrect AND a new qn has been sent                      
                        // Do NOT add a new image... Just push the message through socket:

                        message.setState(GameProtocol.SERVER_SENT);    
                        System.out.println("\nSending String Message: " + message.getText());  
                        System.out.println("Sending int Answer Flag: " + message.getNumber());
                        System.out.println("Sending int Message State : " + message.getState()); 
                        objOutStream.writeObject(message);                   
                    }               
            } //while loop
        } catch (Exception e) {
                e.printStackTrace();
        } //end catch
    } //end main

    public void sendImage (){
        System.out.println("STUB: To DO");
    }

} //end of class