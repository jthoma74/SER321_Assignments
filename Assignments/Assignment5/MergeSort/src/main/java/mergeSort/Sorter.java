package mergeSort;

import java.util.PriorityQueue;

import org.json.JSONObject;

public class Sorter extends Node {
  // creates a priority queue with natural ordering. Least value at head
  private PriorityQueue<Integer> sorted = new PriorityQueue<Integer>();
  
  // Creates a node calling super method from node
  public Sorter(int port) {
    super (port);
  }

  /**
   * Initializes JSON obj to zero, then adds data from JSON Array into priority queue
   */
  public synchronized JSONObject init(JSONObject object) {
    sorted.clear();
    for (var val : object.getJSONArray("data")) {
      sorted.add((Integer) val);
    }
    return object;
  }

  public synchronized JSONObject peek(JSONObject object) {
    object.put("response", true);
    if (sorted.size() > 0) {
      object.put("hasValue", true);
      object.put("value", sorted.peek()); //recursive. 
    } else {
      object.put("hasValue", false);
    }
    return object;
  }

  public synchronized JSONObject remove(JSONObject object) {
    object.put("response", true);
    if (sorted.size() > 0) {
      object.put("hasValue", true);
      object.put("value", sorted.remove());
    } else {
      object.put("hasValue", false);
    }
    return object;
  }

  public JSONObject error(String error) {
    JSONObject ret = new JSONObject();
    ret.put("error", error);
    return ret;
  }
}