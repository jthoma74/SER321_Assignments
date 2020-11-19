package Assignment4P2P;
import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.json.*;

/**
 * This is the main class for the peer2peer program.
 * It starts a client with a username and port. Next the peer can decide who to listen to. 
 * So this peer2peer application is basically a subscriber model, we can "blurt" out to anyone who wants to listen and 
 * we can decide who to listen to. We cannot limit in here who can listen to us. So we talk publicly but listen to only the other peers
 * we are interested in. 
 * 
 */


public class Peer {
	public enum State { PEER, HOST, PAWN }
	private String username;
	private BufferedReader bufferedReader;
	private ServerThread serverThread;
	private JSONArray arrQuestionBank;
	private JSONObject objQuestionBank;
	private int intNextQuestionId;
	private State myState;
	private ArrayList<Integer> alsAlreadyAsked;	
	private int intNumQuestions;
	private String strCurrentQuestion;
	private String strCurrentAnswer;
	private int myPortNumber;
	private ArrayList<ClientThread> clientThreads = new ArrayList<ClientThread>();
    private int intThreadNum = 0;
	private int myScore = 0;
	private int numTimesHost = 0;
	//private ArrayList<String> alsWrongAnswerers;
	
	public Peer(BufferedReader bufReader, String username, ServerThread serverThread, String iPort){
		this.username = username;
		this.bufferedReader = bufReader;
		this.serverThread = serverThread;
		this.arrQuestionBank = null;
		this.intNextQuestionId =0;
		this.numTimesHost = 0;
		this.myState = State.PEER;
		this.alsAlreadyAsked = new ArrayList<>();
		this.intNumQuestions = 0;
		this.myPortNumber = Integer.parseInt(iPort);
		//this.alsWrongAnswerers = new ArrayList<>();
	}

	/**
	 * Main method saying hi and also starting the Server thread where other peers can subscribe to listen
	 *
	 * @param args[0] username
	 * @param args[1] port for server
	 */
	public static void main (String[] args) throws Exception {

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		String username = args[0];
		System.out.println("Hello " + username + " and welcome! Your port will be " + args[1]);

		// starting the Server Thread, which waits for other peers to want to connect
		ServerThread serverThread = new ServerThread(args[1]);
		serverThread.start();
		Peer peer = new Peer(bufferedReader, args[0], serverThread, args[1]);
		peer.updateListenToPeers();
	}
	
	/**
	 * User is asked to define who they want to subscribe/listen to
	 * Per default we listen to no one
	 *
	 */
	public void updateListenToPeers() throws Exception {
		System.out.println("> Who do you want to listen to? Enter host:port");
		String input = bufferedReader.readLine();
		String[] setupValue = input.split(" ");
		for (int i = 0; i < setupValue.length; i++) {
			String[] address = setupValue[i].split(":");
			Socket socket = null;
			try {
				socket = new Socket(address[0], Integer.valueOf(address[1]));
				ClientThread cThread = new ClientThread(socket);
				cThread.start();
				clientThreads.add(cThread);
				intThreadNum++;
			} catch (Exception c) {
				if (socket != null) {
					socket.close();
				} else {
					System.out.println("Cannot connect, wrong input");
					System.out.println("Exiting: I know really user friendly");
					System.exit(0);
				}
			}
		}

		askForInput();
	}
	
	/**
	 * Client waits for user to input their message or quit
	 *
	 * @param bufReader bufferedReader to listen for user entries
	 * @param username name of this peer
	 * @param serverThread server thread that is waiting for peers to sign up
	 */
	public void askForInput() throws Exception {
	
		myState = State.PEER;
		try {
			System.out.println("> Type start and hit enter to start the game (exit to exit)");
			while(true) {
				String message = bufferedReader.readLine();
				if (message.equals("exit")) {
					System.out.println("bye, see you next time");
					break;
				} else if(message.equalsIgnoreCase("start")) {
						// Start the game here. This will set the state of each peer and set question to send from host
						startNewGame(); 
					} else {
						// we are sending the message to our server thread. this one is then responsible for sending it to listening peers
						serverThread.sendMessage("{'username': '"+ username +"', 'message':'" + message + "', 'game_active':'NO'}");	
					} // end else
			} //end while
			System.exit(0);
		
		} catch (Exception e) {
			e.printStackTrace();
		} //end try-catch
	} //end of askForInput()

	public void startNewGame() throws Exception {
		try {
			System.out.println("> Type 1 if you want to be the host and 0 otherwise (exit to exit)");
			String message = bufferedReader.readLine();
			if (message.equals("exit")) {
				System.out.println("You have left the game.");
			} else if(message.equals("1")) {
				System.out.println("----------------");
				System.out.println("     NEW GAME   ");
				System.out.println("----------------");
				System.out.println("        #      ");
				System.out.println("       ###     ");
				System.out.println("      #####    ");
				System.out.println("----------------");
				System.out.println("You are the host");
				System.out.println("----------------");
				//peer goes to host mode
				enterHostMode();
			} else {
				//Peer goes to pawn mode
				myState = State.PAWN;
				enterPawnMode();
			} //end if-else
		} catch (Exception e) {
			e.printStackTrace();
		} //end try-catch
	} //end of startNewGame()

	public void enterHostMode() throws Exception {
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (numTimesHost == 0) {
			//System.out.println("\nenterHostMode: Entering host mode for the first time\n");
			initQuestionBank();
			initAlreadyAsked();
		} 
		myState = State.HOST;
		numTimesHost++;
		int intQuestionId; 
		JSONObject objJsonItem;
		System.out.println ("\nEnter YES to send a question");
		String message = bufferedReader.readLine();
		boolean boolContinue = false;
		if (message.equalsIgnoreCase("YES")) {	
			boolContinue = true;
		}
		sendNextQuestion(boolContinue);
		playGameAsHost();
	} //end of enterHostMode() 

	public void sendNextQuestion(boolean bContinue) {
		int intQuestionId; 
		JSONObject objJsonItem;
		//alsWrongAnswerers.clear();
		while (myState == State.HOST && bContinue) {
			intQuestionId = getNextQuestionId();
			for (int i = 0; i < intNumQuestions; i++) {
				objJsonItem = arrQuestionBank.getJSONObject(i);
				// Get the question and answer from JSON using random questionID
				if (objJsonItem.getInt("id") == intQuestionId) {
					strCurrentQuestion = objJsonItem.getString("question");
					strCurrentAnswer = objJsonItem.getString("answer");
					serverThread.sendQuestion("{'username': '"+ username +"', 'message':'" + strCurrentQuestion + "', 'game_active':'YES', 'cur_answer':'" + strCurrentAnswer 
												+ "', 'message_type': 'QUESTION', 'correct_guesser': '...'}");	
					break; //only breaks from for loop
				} //end if
			} //end for
			bContinue = false;
		} //end while	
	} //end of sendNextQuestion()

    public void playGameAsHost() {
		//System.out.println("\nplayGameAsHost: Start playing as host now");
		boolean boolContinue = true;
		JSONObject jobMessage;
		while(myState == State.HOST && boolContinue) {
			//System.out.println("playGameAsHost: About to check messages as host");	
			jobMessage = checkIncomingMessages();
			// Check if incoming message is of type ANSWER
			//System.out.println("playGameAsHost(): " + jobMessage.toString());
			if (jobMessage.getString("message_type").equalsIgnoreCase("ANSWER")) {
				// Display who sent the answer and the answer
				String strSender = jobMessage.getString("username"); 
				System.out.println(strSender + " has sent the answer: " + jobMessage.getString("message"));				
				String message1 = "";
				if (strCurrentAnswer.equalsIgnoreCase(jobMessage.getString("message"))) {
					message1 = strSender + " had the correct answer: " + jobMessage.getString("message");
					
					// Send message_type: CORRECT to Server Threads. username: correct guesser
					serverThread.sendResult("{'username': '"+ username +"', 'message':'" + message1 + "', 'game_active':'YES', 'host_port':" + myPortNumber 
												+ ", 'message_type': 'RESULT', 'correct_guesser': '" +  strSender + "'}");
					jobMessage = null;	
					boolContinue = false;
					myState = State.PAWN;
				} else {
						//System.out.println(jobMessage.getString("username") + " had the WRONG answer") ;
						serverThread.sendResult("{'username': '"+ username +"', 'message': 'Incorrect. Try again..', 'game_active':'YES', 'host_port':" + myPortNumber 
												+ ", 'message_type': 'RESULT', 'correct_guesser': '...'}");
						jobMessage = null;
				} //end else
			} else if (jobMessage.getString("game_active").equalsIgnoreCase("NO") && jobMessage.getString("message_type").equalsIgnoreCase("RESULT")) {
				//System.out.println("playGameAsHost(): Received Game over info. Game_active is " + jobMessage.getString("game_active") + " and messge_type is " + jobMessage.getString("message_type"));
				myState = State.PEER;
				boolContinue = false;	
			} //end nested if
		} //end while
		if (myState == State.PEER) {
			//Game over. Option to start new game
			try {
				askForInput();	
			} catch (Exception e) {
				e.printStackTrace();
			} //end try-catch
		} else if (myState == State.PAWN) {
			System.out.println("Correct answer received. You will become Pawn now");
			enterPawnMode();
		} //end if
	} //end of playGame() 

	public void initQuestionBank() {
		//Open json file and fill the array with its contents
		//System.out.println("Initializing question bank");
		int intNextQuestionId = 0;
		
		try {
			File file = new File(Peer.class.getResource("/qbank.json").getFile());
			Reader reader = new FileReader(file);
			JSONTokener jsonTokener = new JSONTokener(reader);
			objQuestionBank = new JSONObject(jsonTokener);
			arrQuestionBank = (JSONArray) objQuestionBank.get("question_bank"); 
			intNumQuestions = arrQuestionBank.length();
			//System.out.println("Opened the question bank with " + intNumQuestions + " questions");
		} catch (Exception e) {
			e.printStackTrace();
		} //end try-catch
	} //end of initQuestionBank()

	public void initAlreadyAsked() {
		//System.out.println("Initializing already asked questions array");
		for (int i = 0; i < intNumQuestions; i++) {
			alsAlreadyAsked.add(-1);
		}
	} //end of initAlreadyAsked()

	public int countAlreadyAsked() {
		//System.out.println("Getting count of already asked questions");
        int intCount = 0;
        for(int i = 0; i < intNumQuestions; i++) {
            if (alsAlreadyAsked.get(i) > 0) {
                intCount++;
            } //end if
        } //end for
        return intCount;
    } //end of countAlreadyAsked()

	public int getNextQuestionId() {
		//System.out.println("Getting the id for next question");
		Random random1 = new Random();
		boolean boolQnExhausted = false; 
		int intQId = -1;
		while (!boolQnExhausted) {
			intQId = random1.nextInt(intNumQuestions);
			if (alsAlreadyAsked.get(intQId) < 0) {
				alsAlreadyAsked.set(intQId,1);
				if (countAlreadyAsked() == intNumQuestions) {
					boolQnExhausted = true; //breaks out of while loop
				} //end if
				return intQId;
			}
		} //end while
		return -1;
	} //end of getNextQuestionId()  

	/*
	* The peer can send one answer only.
	*
	*/
	public void enterPawnMode() {
		myState = State.PAWN;
		System.out.println ("\n...YOU ARE A MERE PAWN...\n");
		String message = "";
		JSONObject jobIncoming;
		//System.out.println ("enterPawnMode: about to enter Try-catch.");
		try {
			//System.out.println("enterPawnMode: Entered Try-Catch Block");
			Thread.sleep(500);
			while(myState == State.PAWN){
				jobIncoming = checkIncomingMessages();	
				//System.out.println("\nenterPawnMode: " + jobIncoming.toString());
				if (jobIncoming.getString("game_active").equalsIgnoreCase("YES") && jobIncoming.getString("message_type").equalsIgnoreCase("RESULT")) {
					//System.out.println("enterPawnMode: Message type is RESULT");
					//System.out.println(jobIncoming.toString());
					if (jobIncoming.getString("correct_guesser").equalsIgnoreCase(username)) {
					    //System.out.println("enterPawnMode: I am the correct guesser");
						myScore++;
						if (myScore == 2) {
							//Game over
							System.out.println("I got it! I WON, IWON, IWON!!!");	
							message = "{'username': '"+ username +"', 'message':'I WON, IWON, IWON!!!', 'game_active':'NO', 'host_port':" 
								+ myPortNumber + ", 'message_type': 'RESULT', 'correct_guesser': '...'}";
							serverThread.sendResult(message);
							myState = State.PEER;
						} else { 
							//Correct answer given. New host now.
							//System.out.println("enterPawnMode: Correctly answered. I am the new host");
							myState = State.HOST;
						} //end else-if3
					} //end if2
				} else if (jobIncoming.getString("message_type").equalsIgnoreCase("QUESTION")) {
					// Enter answer:
					//System.out.println("enterPawnMode: Waiting for input from user");
					String userAnswer = bufferedReader.readLine(); 
					message = "{'username': '"+ username +"', 'message':'" + userAnswer + "', 'game_active':'YES', 'host_port':" 
								+ myPortNumber + ", 'message_type': 'ANSWER', 'correct_guesser': '...'}";
					//System.out.println(message); 
					//Send answer
					serverThread.sendAnswer(message);
				} else if (jobIncoming.getString("game_active").equalsIgnoreCase("NO") && jobIncoming.getString("message_type").equalsIgnoreCase("RESULT")) {
					//System.out.println("enterPawnMode(): Received Game over info. Game_active is " + jobIncoming.getString("game_active") + " and messge_type is " + jobIncoming.getString("message_type"));
					//Game over. Option to start new game
					myState = State.PEER;	
				}//end else-if
			} //end while - Be in loop as long as state is PAWN

			if (myState == State.HOST) {
				System.out.println("\nYour current score is: " + myScore + "\nYou are becoming host now..");
				Thread.sleep(500);
				enterHostMode();	
			} else if (myState == State.PEER) {
				//Current game is over. Option to start a new game
				try {
					askForInput();	
				} catch (Exception e) {
					e.printStackTrace();
				}
			} //end if
		} catch (Exception e) {
			e.printStackTrace();
		} //end try-catch

	}


	public JSONObject checkIncomingMessages() {
		//Method to check for messages in client sockets and store in receivedMsgJSON
		JSONObject jobTemp = null;
		boolean keepChecking = true;
		//System.out.println("In method checkIncomingMessages");

		while (keepChecking) {
			try {
				for (int i = 0; i < intThreadNum; i++) {
					//System.out.println("CheckIncomingMessages(): Checking client thread : " + i);
					jobTemp = clientThreads.get(i).receiveJsonMessage();
					if (jobTemp != null) {
						//System.out.println("checkIncomingMessages(): received JSON from a client thread");
						keepChecking = false;
						break;
					} //end if
					Thread.sleep(500);
				} //end for
			} catch (Exception e) {
				e.printStackTrace();
			} //end try-catch
		} //end while
		//System.out.println("checkIncomingMessages(): Returning JSON message");
		return jobTemp;
	} //end of checkIncomingMessages 


} //end of class Peer