package Assignment4P2P;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import org.json.*;

/**
 * SERVER
 * This is the ServerThread class that has a socket where we accept clients contacting us.
 * We save the clients ports connecting to the server into a List in this class. 
 * When we wand to send a message we send it to all the listening ports
 */


public class ServerThread extends Thread{
	private ServerSocket serverSocket;
	private Set<Socket> listeningSockets = new HashSet<Socket>();
    private String strCurAnswer;
    private String strCurQuestion;
	private int intMyScore = 0;
	private boolean iAmTheHost = false;
	
	public ServerThread(String portNum) throws IOException {
		serverSocket = new ServerSocket(Integer.valueOf(portNum));
	}
	
	/**
	 * Starting the thread, we are waiting for clients wanting to talk to us, then save the socket in a list
	 */
	public void run() {
		try {
			Thread.sleep(500);
			while (true) {
				Socket sock = serverSocket.accept();
				listeningSockets.add(sock);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sending the message to the OutputStream for each socket that we saved
	 */
	void sendMessage(String message) {
		try {
			for (Socket s : listeningSockets) {
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				out.println(message); //send message to every peer if it is a question, or if we are chatting
		     } //end for
		} catch(Exception e) {
			e.printStackTrace();
		} //end try catch
	} //end send Message
	
	void sendQuestion(String strMessage) {
		//Capture current question and answer in variables and send question out to all
		//System.out.println("sendQuestion: " + strMessage);
		JSONObject json = new JSONObject(strMessage);
		//strCurAnswer = json.getString("cur_answer");
		strCurQuestion = json.getString("message"); 
		String strMsgToSend = "{'username': '" + json.getString("username") + "'"
						    	+ ",'message': '" + strCurQuestion + "'"
								+ ",'game_active': '" + json.getString("game_active") + "'"
								+ ",'message_type': '" + json.getString("message_type") + "'"
							+ "}";
		System.out.println("sendQuestion: " + strCurQuestion);
		try {
			for (Socket s : listeningSockets) {
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				out.println(strMsgToSend);
			} //end for
		} catch (Exception e) {
			e.printStackTrace();
		}
	} //end sendQuestion()


	void sendAnswer (String strMessage) {
		//System.out.println("sendAnswer: Full message: " + strMessage);
		JSONObject json = new JSONObject(strMessage);
		String strMyAnswer = json.getString("message");
		//System.out.println("sendAnswer: answer to send: " + strMessage);
		String strMsgToSend = "{'username': '" + json.getString("username") + "'"
						    	+ ",'message': '" + strMyAnswer + "'"
								+ ",'game_active': '" + json.getString("game_active") + "'"
								+ ",'message_type': '" + json.getString("message_type") + "'"
							+ "}";
		try {
			for (Socket s : listeningSockets) {
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				out.println(strMsgToSend);
			} //end for
		} catch (Exception e) {
			e.printStackTrace();
		}
	} //end sendAnswer	

	void sendResult (String strMessage) {
		JSONObject json = new JSONObject(strMessage);
		String strResult = json.getString("message");
		String strMsgToSend = "{'username': '" + json.getString("username") + "'"
						    	+ ",'message': '" + strResult + "'"
								+ ",'game_active': '" + json.getString("game_active") + "'"
								+ ",'message_type': '" + json.getString("message_type") + "'"
								+ ",'correct_guesser': '" + json.getString("correct_guesser") + "'"
							+ "}";
		try {
			for (Socket s : listeningSockets) {
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				out.println(strMsgToSend);
			} //end for
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

} //end of class