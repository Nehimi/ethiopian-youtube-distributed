package client;

import server.VideoInterface;
import java.rmi.registry.*;

public class QuickTest {
  public static void main(String[] args) throws Exception {
    System.setProperty("java.rmi.server.hostname", "10.198.73.40");
    System.out.println("=== Quick RMI Test ===");
    try {
      Registry r = LocateRegistry.getRegistry("10.198.73.40", 8081);
      VideoInterface s = (VideoInterface) r.lookup("VideoService");
      System.out.println("Connected to Node1: " + (s != null));
      if (s != null) {
        boolean ok = s.uploadChunk("rmi_test.mp4", new byte[] { 1, 2, 3, 4, 5 }, true);
        System.out.println("uploadChunk returned: " + ok);
        if (!ok) {
          System.out.println("FAIL: uploadChunk returned false - check server logs!");
        }
      }
    } catch (Exception e) {
      System.out.println("EXCEPTION: " + e.getClass().getName());
      System.out.println("MESSAGE: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
