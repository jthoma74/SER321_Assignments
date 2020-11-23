package Assignment4TPServer;
import java.net.*;

public class TPConnection extends Thread {
    Socket socTP;
    StringList strings;
    int intThreadNum;
    //Constructor with socket and  server thread
    public TPConnection(Socket socket, StringList strings, int iThread) {
        super("TPServerConnectionThread");
        this.socTP = socket;
        this.strings = strings;
        this.intThreadNum = iThread;
    } //end of constructor 

    //Override the run method
    public void run() {
        System.out.println("TPConnection-run: Server connection thread started");
        boolean boolContinue = true;
        String strConnLabel = "TPConnection" + intThreadNum;
        while (boolContinue) {
            try {
                Thread.sleep(1);
            } catch (Exception ex) {
                ex.printStackTrace();
            } //end try-catch block1
            System.out.println(strConnLabel + ": Creating a performer for connection thread");
            Performer performer = new Performer(socTP, strings);
            System.out.println(strConnLabel + ": Performer running processing");
            performer.doPerform();
            boolContinue = false;   
        } //end of while loop
        try {
            socTP.close();
            System.out.println(strConnLabel + ": Socket closed");
        } catch (Exception ex) {
            ex.printStackTrace();
        } //end of try-catch block2
    } //end of run()
} //end of class TPConnection
