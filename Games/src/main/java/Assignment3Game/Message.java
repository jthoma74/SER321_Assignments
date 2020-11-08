package Assignment3Game;
import java.io.Serializable;

public class Message implements Serializable {

    String strText;
    int intNumber; //Question Correct Flag
    int intState;
    byte[] bytImage;

    // implement image byte array
   // Byte[] imgArray;

    public Message () {
        strText = "";
        intNumber = 0;
        intState  = 0;
        bytImage = null;
    } //default constructor end.

    public Message(String strText, int intNumber, int intState) {
        this.strText = strText;
        this.intNumber = intNumber;
        this.intState = intState;
    }

    // Copy constructor for copying objects. Does not need to copy byte array.
    public Message (Message msg) {
        strText = msg.strText;
        intNumber = msg.intNumber;
        intState = msg.intState;
    } //end copy constructor

    public String getText() {
        return strText;
    }

    public void setText (String str) {
        this.strText = str;
    }
       
    public int getState() {
        return intState;
    }

    public void setState(int i){
        this.intState = i;
    }

    public int getNumber (){
        return intNumber;
    }

    public void setNumber (int n){
        this.intNumber = n;
    }

    public void setByteImage (byte[] arr) {
        this.bytImage = arr.clone(); //clones values. Not just a pointer.
    }

    public byte[] getByteImage () {
        return bytImage;
    }

    /**
     * Clears the image in the byte array
     */
    public void clearImage () {
        this.bytImage = null;
    }
 

} // end Message class
