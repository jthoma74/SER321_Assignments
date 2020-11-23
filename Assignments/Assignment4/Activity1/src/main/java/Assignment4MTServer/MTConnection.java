package Assignment4MTServer;
import java.net.*;

public class MTConnection extends Thread {
    Socket socMT;
    StringList strings;
    int intThreadNum;
    //Constructor with socket and  server thread
    public MTConnection(Socket socket, StringList strings, int iThread) {
        super("MTServerConnectionThread");
        this.socMT = socket;
        this.strings = strings;
        this.intThreadNum = iThread;
    } //end of constructor 

    //Override the run method
    public void run() {
        System.out.println("MTConnection-run: Server connection thread started");
        boolean boolContinue = true;
        String strConnLabel = "MTConnection" + intThreadNum;
        while (boolContinue) {
            try {
                Thread.sleep(1);
            } catch (Exception ex) {
                ex.printStackTrace();
            } //end try-catch block1
            System.out.println(strConnLabel + ": Creating a performer for connection thread");
            Performer performer = new Performer(socMT, strings);
            System.out.println(strConnLabel + ": Performer running processing");
            performer.doPerform();
            boolContinue = false;   
        } //end of while loop
        try {
            socMT.close();
            System.out.println(strConnLabel + ": Socket closed");
        } catch (Exception ex) {
            ex.printStackTrace();
        } //end of try-catch block2
    } //end of run()
} //end of class MTConnection
