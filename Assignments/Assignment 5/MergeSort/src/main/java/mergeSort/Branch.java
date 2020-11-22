package mergeSort;

import org.json.JSONArray;
import org.json.JSONObject;

public class Branch extends Node {
  private int _leftPort;
  private int _rightPort;

  public Branch(int port, int left, int right) {
    super(port); //calls method from node.java; creates a node
    _leftPort = left;
    _rightPort = right;
  }

  /**
   * Initialize a json object and returns it
   */
  public JSONObject init(JSONObject object) {
    JSONArray left = new JSONArray();
    JSONArray right = new JSONArray();
    JSONArray whole = object.getJSONArray("data");

    // split up the array into two. first half on the left, second half on the right
    int i = 0;
    for (; i < whole.length() / 2; ++i) {
      left.put(whole.getInt(i));
    }
    for (; i < whole.length(); ++i) {
      right.put(whole.getInt(i));
    }

    object.put("data", left);
    JSONObject response1 = NetworkUtils.send(_leftPort, object);
    if (response1.has("error")) {
      return response1;
    }

    object.put("data", right);
    JSONObject response2 = NetworkUtils.send(_rightPort, object);
    if (response2.has("error")) {
      return response2;
    }

    object.put("data", whole);
    return object;
  }

  /**
   * Check for messages from left and right nodes. Checks if there is an error message and returns error message if there is one
   * If left node or right node is empty, return that response. Otherwise return the node with the higher value
   */
  public JSONObject peek(JSONObject object) {
    JSONObject response1 = NetworkUtils.send(_leftPort, object);
    if (response1.has("error")) {
      return response1;
    }

    JSONObject response2 = NetworkUtils.send(_rightPort, object);
    if (response2.has("error")) {
      return response2;
    }

    //if responses dont have value, return that object. Else return the node with the higher value.
    if (!response1.getBoolean("hasValue")) {
      return response2;
    } else if (!response2.getBoolean("hasValue")) {
      return response1;
    } else if (response1.getInt("value") < response2.getInt("value")) {
      return response1;
    } else {
      return response2;
    }
  }

  /**
   * ??
   */
  public JSONObject remove(JSONObject object) {
    object.put("method", "peek");
    JSONObject response1 = NetworkUtils.send(_leftPort, object);
    if (response1.has("error")) {
      return response1;
    }

    JSONObject response2 = NetworkUtils.send(_rightPort, object);
    if (response2.has("error")) {
      return response2;
    }

    object.put("method", "remove");
    if (!response1.getBoolean("hasValue")) {
      return NetworkUtils.send(_rightPort, object);
    } else if (!response2.getBoolean("hasValue")) {
      return NetworkUtils.send(_leftPort, object);
    } else if (response1.getInt("value") < response2.getInt("value")) {
      return NetworkUtils.send(_leftPort, object);
    } else {
      return NetworkUtils.send(_rightPort, object);
    }
  }

  public JSONObject error(String error) {
    JSONObject ret = new JSONObject();
    ret.put("error", error);
    return ret;
  }
}
