package Assignment3Game;
import java.net.*;

import Assignment3Game.GameServer;

import java.io.*;
import java.lang.Math;
import java.util.Random;
public class GameProtocol {
    /** Program Message Exchanges
     * SETUP
     * --> Have a set of questions and answers (hard code)
     * --> Use image splitter method from starter code
     * 1) Send a quiz question to the client
     * 2) Receive answer from client
     * 3) Verify answer from client (the answer can be the quiz question, or the correct final answer to the puzzle)
     * 4) If answer to quiz is correct {Send next image piece}
     * 5) Else if the answer to the puzzle is correct {Send message: You won!}
    **/


    // Possible states of the game: 
    public static final int WAITING = 0;
    public static final int FIRST_QN_SENT = 1;
    public static final int QN_SENT = 2;
    public static final int ANS_SENT = 3;
    public static final int SENT_IMG = 4; //I dont think this is used
    public static final int WIN = 5;
    public static final int LOST = 6;
    public static final int SERVER_SENT = 7;

    private static final int CORRECT = 1;
    private static final int INCORRECT = 0;

    private int intCurrentState = WAITING;
    private int intQuestionFlag = 0;
    public int intNextQuestion; 
    public int intIncorrectGuesses = 0;


    public String[] arrQuestions = {
        "What is the capital of France?",
        "What is 6 squared?", 
        "What is the square root of 100?",
        "What is the cube root of 27?",
        "Who was the first president of the USA?",
        "What is mochi made of?",
        "What is the most expensive spice in the world?",
        "What is 50/2?",
        "What is value of pi to two decimal points?",
        "Who is the leader of the United Nations?",
        "What fruit did Eve bite?",
        "Who invented the light bulb? (last name only)",
        "What is the last name of the first man on the moon?",
        "Which is the longest river in the world?",
        "What is the name of the little mermaid?",
        "What element is Hg?",
        "What is the third planet from the sun?",
        "How many continents are there?",
        "What is the fastest land animal?",
        "What programming language is commonly taught to students?"
    };
    public String[] arrAnswers = {
        "Paris",
        "36",
        "10",
        "3",
        "George Washington",
        "Rice",
        "Saffron",
        "25",
        "3.14",
        "Antonio Guterres",
        "Apple",
        "Edison",
        "Armstrong",
        "Nile",
        "Ariel",
        "Mercury",
        "Earth",
        "7",
        "Cheetah",
        "Java"

    };
    
    public int[] alreadyAsked = {
        -1,-1,-1,-1,
        -1,-1,-1,-1,
        -1,-1,-1,-1,
        -1,-1,-1,-1,
        -1,-1,-1,-1
    }; 

    public int countAlreadyAsked() {
        int intCount = 0;
        for(int i = 0; i < alreadyAsked.length; i++) {
            if (alreadyAsked[i] > 0){
                intCount++;
            } //end if
        } //end for
        return intCount;
    }

    public int getNextQuestionNumber(){
        int range = 20; // Not ideal: Fix later -hardcoded number of questions and answers
        Random rand = new Random(); 
        int intRandom = rand.nextInt(range); //range is from 0 to 19
        System.out.println("Random Qn #" + intRandom + " has been selected");
        return intRandom;
    }

    public String getNextQuestion() {
    
        //1.generate a random number
        //2.check if random number has been used already
        //3.if not, return the string from that index
        //4.otherwise generate a different random number

        Boolean boolQnExhausted = false;
        while (!boolQnExhausted){
            intNextQuestion = getNextQuestionNumber();
            // only choose unasked questions:
            if (alreadyAsked[intNextQuestion] < 0 ) {
                alreadyAsked[intNextQuestion] = 1;
                if (countAlreadyAsked() == alreadyAsked.length) {
                    boolQnExhausted = true; // breaks out of while loop
                }    
                return arrQuestions[intNextQuestion]; //break out of the method
            } // end nested while loop    
        } //end outer while loop  
        return "All questions have been exhausted!"; 
    } // getNextQuestion()

    public Boolean isAnswerCorrect(String strClientResponse) {
        if (strClientResponse.equalsIgnoreCase(arrAnswers[intNextQuestion])) {
            return true;
        } else {
            intIncorrectGuesses++;
            return false;
        }
    } //end isAnswerCorrect()

    String[] puzzleAnswers = {
        "Pineapple upside down cake"
    };

    /** Send the next question OR a message */
    public Message protoProcess(Message msg) {

        String strMsg = null;
        // Parcel the string question and send to client
        if (intCurrentState == WAITING) { // Send first question to client     
            strMsg = getNextQuestion();
            intQuestionFlag = 0;
        }   intCurrentState = FIRST_QN_SENT;
        if (msg.getState() == ANS_SENT) { //If client has sent answer
            boolean boolAnswerCorrect = isAnswerCorrect(msg.getText());
        if(msg.getText().equals(puzzleAnswers[0])){ // Check if client has entered rebus puzzle answer           
            strMsg = "You Win!"; 
            intQuestionFlag = 0; 
            intCurrentState = WIN;
            return (prepareMsg(msg, strMsg, intQuestionFlag, intCurrentState)); //break out if puzzle answer is correct.
        } else if (boolAnswerCorrect) { // Check if answer to qn is correct
            strMsg = "Correct Answer!" + "\n Next Question: " + getNextQuestion(); //Sends: "Answer correct" message + the next question
            intQuestionFlag = 1;
            intCurrentState = QN_SENT;
        } else if (!boolAnswerCorrect && intIncorrectGuesses < 3) { // If answer is incorrect and client has more tries
            strMsg = "Incorrect Answer" + "\n Try a different Qn: " + getNextQuestion();
            intQuestionFlag = 0;
            intCurrentState = QN_SENT;
        } else if (!boolAnswerCorrect && intIncorrectGuesses >= 3) { // If answer is incorrect and user has no more tries
            strMsg = "You Lose...";
            intQuestionFlag = 0;
            intCurrentState = LOST;
            } // end else ifs                          
        } //end if-else for checking answers    
        
        // Parcel the message and return it:
        return (prepareMsg(msg, strMsg, intQuestionFlag, intCurrentState));
    } //end processInput()

    /**
     * Prepares message to send
     */
    public Message prepareMsg (Message msg, String strMsg, int num, int state) {
        msg.setText(strMsg);
        msg.setNumber(num);
        msg.setState(state);
        return msg;
    } //end prepare Msg

} //end GameProtocol class

