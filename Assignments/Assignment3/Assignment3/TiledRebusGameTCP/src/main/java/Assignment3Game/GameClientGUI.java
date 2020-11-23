package Assignment3Game;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import java.net.*;
import java.io.*;
import java.util.Scanner;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.nio.ByteBuffer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Assignment3Game.GameProtocol;
import Assignment3Game.PicturePanel;

public class GameClientGUI implements Assignment3Game.OutputPanel.EventHandlers {
  JDialog frame;
  public static PicturePanel picturePanel;
  OutputPanel outputPanel;

  public static Socket socClient = null;
  public static String strHostName;
  public static int intPortNumber;
  public static GameProtocol gameProtocol;
  public static Message message = null;
  public static int intClickCounter = 0;
  public static int intNumTiles;

  public static ObjectOutputStream objOutStream;
  public static ObjectInputStream objInStream;

  public static GameClientGUI GUI;

  /**
     * Construct dialog
     */
    public GameClientGUI() {
      frame = new JDialog();
      frame.setLayout(new GridBagLayout());
      frame.setMinimumSize(new Dimension(500, 500));
      frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  
      // setup the top picture frame
      picturePanel = new PicturePanel();
      GridBagConstraints c = new GridBagConstraints();
      c.gridx = 0;
      c.gridy = 0;
      c.weighty = 0.25;
      frame.add(picturePanel, c);
  
      // setup the input, button, and output area
      c = new GridBagConstraints();
      c.gridx = 0;
      c.gridy = 1;
      c.weighty = 0.75;
      c.weightx = 1;
      c.fill = GridBagConstraints.BOTH;
      outputPanel = new OutputPanel();
      outputPanel.addEventHandlers(this);
      frame.add(outputPanel, c);
    }

  /**
   * Shows the current state in the GUI
   * 
   * @param makeModal - true to make a modal window, false disables modal behavior
   */
  public void show(boolean makeModal) {
    frame.pack();
    frame.setModal(makeModal);
    frame.setVisible(true);
  }

  /**
   * Creates a new game and set the size of the grid
   * 
   * @param dimension - the size of the grid will be dimension x dimension
   */
  public void newGame(int dimension) {
    picturePanel.newGame(dimension);
    //outputPanel.appendOutput("Started new game with a " + dimension + "x" + dimension + " board.");
  }

  /**
   * Insert an image into the grid at position (col, row)
   * 
   * @param filename - filename relative to the root directory
   * @param row      - the row to insert into
   * @param col      - the column to insert into
   * @return true if successful, false if an invalid coordinate was provided
   * @throws IOException An error occured with your image file
   */
  public boolean insertImage(String filename, int row, int col) throws IOException {
    String error = "";
    try {
      // insert the image
      if (picturePanel.insertImage(filename, row, col)) {
        // put status in output
        outputPanel.appendOutput("Inserting " + filename + " in position (" + row + ", " + col + ")");
        return true;
      }
      error = "File(\"" + filename + "\") not found.";
    } catch (PicturePanel.InvalidCoordinateException e) {
      // put error in output
      error = e.toString();
    }
    outputPanel.appendOutput(error);
    return false;
  }

  /**
   * Submit button handling
   * 
   * Change this to whatever you need
   */
  @Override
  public void submitClicked() {
    // Pulls the input box text
    String input = outputPanel.getInputText();
    // if has input
    if (input.length() > 0) {
      // append input to the output panel
      intClickCounter++;
      outputPanel.appendOutput(input);
      System.out.println("\nYour input: " + input + " has been stored");
      processInput(input);
      // clear input text box
      outputPanel.setInputText("");
    } // end if
  } // end click handler

  public static String strImageName;
  public static int intImageIndex = 0;
  public static int intNumTilesGUI = 0;
  public static int x = 0;
  public static int y = 0;

  public static void processInput(String strMsg) {

        String strServerMsg;
        int intServerFlag;
        int intServerState;
        String strClientMsg;
        int intClientFlag;
        int intClientState;


        try {
            if (intClickCounter == 1) {
              intNumTilesGUI = Integer.parseInt(strMsg);
              GUI.newGame(intNumTilesGUI);
              GUI.show(true);
              GUI.displayThis("Started new game with a " + intNumTilesGUI + "x" + intNumTilesGUI + " board.");

              message = new Message("", intNumTilesGUI, GameProtocol.WAITING); 
              System.out.println("First Message successfully parceled");
              // Send message to socket via output stream:
              objOutStream.writeObject(message); 
              System.out.println("You've sent: " + message.getNumber() + " to the server");
            } else {
              // create listener and sender for Client
              // Uodate contents of Message packet:
              intClientFlag = 0;
              intClientState = GameProtocol.ANS_SENT;

              // Parcel the message:
              message.setText(strMsg);
              message.setNumber(intClientFlag);
              message.setState(intClientState);

              // Push to socket:
              objOutStream.writeObject(message);
              System.out.println("Message sent from Client...");
            }

            //Read next message from server
            byte[] bytCroppedImage;
            message = (Message) objInStream.readObject(); 
            System.out.println("Receiving next message from server...");
            strServerMsg = message.getText();
            intServerFlag = message.getNumber();
            intServerState = message.getState();
            GUI.displayThis(strServerMsg);
            
            // parse byte array:
            bytCroppedImage = message.getByteImage();
            System.out.println("Received byte array");

            // Check if there is an image in the message packet:
            if(bytCroppedImage != null) {
              System.out.println("Entered byte array to image conversion");

              ByteArrayInputStream bis = new ByteArrayInputStream(bytCroppedImage);
              System.out.println("converted byte array to input stream.");
              
              BufferedImage img = ImageIO.read(bis);
              System.out.println("converted input stream to buffered Image");

              //insert into picture panel
              System.out.println("Coordinate ("+ x +", " + y +")");
              picturePanel.insertBufferedImage(img, x, y);
              
              if (x <= intNumTilesGUI-1) {
                if (y < intNumTilesGUI-1) {
                  y++;
                } else {
                  x++; //goes down a row
                  y = 0; //goes back to first column
                }
              } //end if

            }

            if (intServerState == GameProtocol.WIN || intServerState == GameProtocol.LOST) {
              // Close the socket
              socClient.close();
          } else if (intServerFlag == 1) { //If answer is correct                     
              // Todo: show image on picture panel above
              GUI.displayThis("___________________________________");
        } // end else if
      } catch (Exception e){
        System.out.println("Error");
      }
  } //end mthod
  /**
   * Append to output panel from this class, GameClientGUI
   *
   */
  public void displayThis(String str) {
    outputPanel.appendOutput(str);
  }

  /**
   * Key listener for the input text box. Evaluates messages in the input text box
   * 
   * Change the behavior to whatever you need
   */
  @Override
  public void inputUpdated(String input) {
    if (input.equals("surprise")) {
      // outputPanel.appendOutput("You found me!");
      displayThis("You found me! --testing display method");
    }
  }

  public static void main(String[] args) throws IOException {
    // Check for two args: Host and port number
    if (args.length != 2) {
      System.err.println("Usage: java GameClient <host> <port number>");
      System.exit(1);
    } // end if

    Socket socClient = null;
    String strHostName = args[0];
    intPortNumber = Integer.parseInt(args[1]);
    GameProtocol gameProtocol = new GameProtocol();
    message = null;

    GUI = new GameClientGUI();

    try {
      // Create client socket
      socClient = new Socket(strHostName, intPortNumber);

      // Create object output stream to send objects TO Server
      objOutStream = new ObjectOutputStream(socClient.getOutputStream());
      // Create Object Input Stream to receive objects FROM Server:
      objInStream = new ObjectInputStream(socClient.getInputStream());

      System.out.println("Connection established. Game Start.");

      // Start GUI
      picturePanel.newGame(1);
      GUI.displayThis("Enter Number of Tiles and hit Submit");
      GUI.show(true);
      //System.out.println("Type Start and press Submit to begin the game");
    } catch (Exception ex) {
      ex.printStackTrace();
    } // end catch

  } // end main
} // end class
