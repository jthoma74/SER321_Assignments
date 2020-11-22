package mergeSort;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.text.ParseException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class MergeSort {
  /**
   * Thread that declares the lambda and then initiates the work
   */

  public static int message_id = 0;

  public static JSONObject init(int[] array) {
    JSONArray arr = new JSONArray();
    for (var i : array) {
      arr.put(i);
    }
    JSONObject req = new JSONObject();
    req.put("method", "init");
    req.put("data", arr);
    return req;
  }

  public static JSONObject peek() {
    JSONObject req = new JSONObject();
    req.put("method", "peek");
    return req;
  }

  public static JSONObject remove() {
    JSONObject req = new JSONObject();
    req.put("method", "remove");
    return req;
  }
  
  public static void Test(int port) {
    int[] a = { 
                5, 1, 6, 2, 3, 4, 10, 634, 34, 23, 653, 23, 2, 6, 
                9, 15, 25, 26, 7, 13, 25, 1000, 2472, 57, 21,
                54, 36, 363, 45, 67856, 2525, 3565, 45656, 543, 54,  
                15, 46, 745, 47, 7547, 245, 245, 636, 77, 75, 2, 23,
                342, 245, 345, 444, 44, 356, 57, 234, 465, 77,23434,
                233, 344, 35, 672, 2456, 5427, 447, 85, 469, 469, 6,
                23, 2398, 48, 3872, 287, 2487, 297, 2472, 29847, 4           
              };
    System.out.println("Array length is: " + a.length) ;
    long start = System.nanoTime();
    JSONObject response = NetworkUtils.send(port, init(a));
      System.out.println(response);
      response = NetworkUtils.send(port, peek());
      System.out.println(response);

      while (true) {
        response = NetworkUtils.send(port, remove());

        if (response.getBoolean("hasValue")) {
          System.out.println(response);;
  
        } else{
          break;
        } //end else
      } //end while 
      long finish = System.nanoTime();
      long time = TimeUnit.NANOSECONDS.toMillis(finish - start);
      System.out.println ("Time measured: " + time);
  } //end method


  public static void main(String[] args) {
    // all the listening ports in the setup
    ArrayList<Integer> ports = new ArrayList<>(Arrays.asList(8000, 8001, 8002, 8003, 8004, 8005, 8006));

    // setup each of the nodes
    //      0
    //   1     2
    // 3   4 5   6
    new Thread(new Branch(ports.get(0), ports.get(1), ports.get(2))).start();
    
    new Thread(new Branch(ports.get(1), ports.get(3), ports.get(4))).start();
    new Thread(new Sorter(ports.get(3))).start();
    new Thread(new Sorter(ports.get(4))).start();
    
    new Thread(new Branch(ports.get(2), ports.get(5), ports.get(6))).start();
    new Thread(new Sorter(ports.get(5))).start();
    new Thread(new Sorter(ports.get(6))).start();

    // make sure we didn't hang
    System.out.println("started");
    /*
    // One Sorter
    System.out.println("\nRunning with 1 sorter: ");
    Test(ports.get(3));
    

    
    // One branch / Two Sorters
    System.out.println("\nRunning with 1 branch and 2 starters: ");
    Test(ports.get(2));
    */
    
    // Three Branch / Four Sorters
    System.out.println("\nRunning with 3 branches and 4 sorters: ");
    Test(ports.get(0));
    
  }
  


}
