package Assignment4MTServer;
import java.util.List;
import java.util.ArrayList;

class StringList {

    List<String> strings = new ArrayList<String>();

    public void add(String str) {
        int pos = strings.indexOf(str);
        if (pos < 0) {
            strings.add(str);
        }
    } //end of add()

    public void remove(int pos) {
        if (pos >= 0) {
            strings.remove(pos);
        }
    } //end of remove()

    public boolean contains(String str) {
        return strings.indexOf(str) >= 0;
    } //end of contains()

    public String getState(int pos) {
        if (pos >= 0 && pos <= strings.size()-1) {
            return strings.get(pos);
        } else {
            return "Invalid state index";
        }
    } //end if getState()

    public int size() {
        return strings.size();
    } //end of size()

    public String toString() {
        return strings.toString();
    } //end if toString()

    public void replace(int pos, String str) {
        if (pos >= 0 && pos <= strings.size()-1) {
            strings.set(pos, str);
        }
    } //end of replace()
}
