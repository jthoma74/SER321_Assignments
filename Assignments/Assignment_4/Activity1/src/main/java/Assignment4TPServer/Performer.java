package Assignment4TPServer;
import java.net.*;
import java.io.*;

class Performer {

    StringList  state;
    Socket      sock;

    public Performer(Socket sock, StringList strings) {
        this.sock = sock;
        this.state = strings;
    }

    public void doPerform() {

        BufferedReader in = null;
        PrintWriter out = null;
        try {

            in = new BufferedReader(
                        new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream(), true);
            out.println("Commands: add/remove/display/count/reverse");
            out.println("Enter <command> <text/index> (. to disconnect):");

            boolean done = false;
            String strInput;
            String strCmd;
            String strTxt;
            int intSpacePos;

            while (!done) {
                strInput = in.readLine();

                if (strInput == null || strInput.equals("."))
                    done = true;
                else {
                    intSpacePos = strInput.indexOf(" ");
                    if (intSpacePos <= 0) {
                        strCmd = strInput;
                        strTxt = "";
                    } else {
                        strCmd = strInput.substring(0,intSpacePos);
                        strTxt = strInput.substring(intSpacePos+1);
                    } //end space position check
                    out.println(runCommand(strCmd,strTxt));
                } //end StrInput check
            } //end while loop
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
            try {
                in.close();
            } catch (IOException e) {e.printStackTrace();}
        } //end finally
    } //end doPerform()

    public String runCommand(String strCommand, String strText) {
        int intPos = -1;
        String strOut = "";
        try {
            switch (strCommand.toLowerCase()) {
                case "add":
                    if (state.contains(strText)) {
                        strOut = "State already available";
                    } else {
                        state.add(strText);
                        strOut = "Server state is now: " + state.toString();
                    } //end if
                    break;
                case "remove":
                    try {
                        intPos = Integer.parseInt(strText);
                        if (intPos >= 0 && intPos <= state.size()-1) {
                            state.remove(intPos);
                            strOut = "Server state is now: " + state.toString();
                        } else {
                            strOut = "Invalid state index";
                        } // end if
                    } catch (NumberFormatException e) {
                        strOut = "Invalid state index";
                    } //end try-catch in remove case
                    break;
                case "display":
                    strOut = "Server state is now: " + state.toString();
                    break;
                case "reverse":
                    intPos = Integer.parseInt(strText);
                    if (intPos >= 0 && intPos <= state.size()-1) {
                        String strReverse = reverseString(state.getState(intPos));
                        state.replace(intPos, strReverse);
                        strOut = "Server state is now: " + state.toString();
                    } else {
                        strOut = "Invalid state index";
                    } //end if
                    break;
                case "count":
                    strOut = stateCounts();
                    break;
                default:
                    strOut = "Unknown Command. ";
                    break;
            } //end switch
        } catch (Exception e) {
        } //try-catch in runCommand()
        return strOut;
    } //end of runCommand()

    public String reverseString(String strIn) {
        StringBuilder sblIn = new StringBuilder();
        sblIn.append(strIn);
        sblIn = sblIn.reverse();
        return sblIn.toString();
    } //end of reverseString()

    public String stateCounts() {
        String strOutCounts = "";
        int intStateSize = state.size();
        if (intStateSize > 0) {
            strOutCounts = "[";
            for (int i = 0; i < intStateSize; i++) {
                strOutCounts += (state.getState(i)).length();
                if (i < intStateSize-1) {
                    strOutCounts += ",";
                } // end if
            } //end for loop
            strOutCounts += "]";
        } else {
            strOutCounts = "[]";
        } //end if
        return strOutCounts;
    } //end of stateCounts()

} //end Performer class
