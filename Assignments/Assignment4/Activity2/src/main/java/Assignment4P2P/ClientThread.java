package Assignment4P2P;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.*;

/**
 * Client 
 * This is the Client thread class, there is a client thread for each peer we are listening to.
 * We are constantly listening and if we get a message we print it. 
 */

public class ClientThread extends Thread {
	private BufferedReader bufferedReader;
	private JSONObject json;
	
	public ClientThread(Socket socket) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	} //end constructor
	
	public void run() {
		while (true) {
			try {
				Thread.sleep(100);
			    json = new JSONObject(bufferedReader.readLine());
				if (json.getString("game_active").equals("NO")) {
					System.out.println("[" + json.getString("username")+"]: " + json.getString("message"));
				} else if (json.getString("message_type").equals("QUESTION")) {
						System.out.println("[" + json.getString("username")+"]: " + json.getString("message"));
				} else if (json.getString("message_type").equals("RESULT")) {
						System.out.println("[" + json.getString("username")+"]: " + json.getString("message"));
				}	//end else if			
			} catch (Exception e) {
				interrupt();
				break;
			} //end catch
		} //end while
	} //end run method


	public JSONObject receiveJsonMessage() {
		JSONObject json1 = json;
		json = null;
		return json1; 
	}


} //end class
