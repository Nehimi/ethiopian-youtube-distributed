package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {

    public static void startServer(int port, String nodeName) {
        try {

            System.setProperty("java.rmi.server.hostname", "127.0.0.1");

            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(port);
                System.out.println("RMI Registry created on port " + port);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry(port);
                System.out.println("RMI Registry already exists on port " + port);
            }

            int exportPort = port + 100;
            VideoImpl videoService = new VideoImpl(nodeName + "Storage", exportPort);

            registry.rebind("VideoService", videoService);

            System.out.println("Server [" + nodeName + "] is ready and 'VideoService' is bound successfully.");
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("Server [" + nodeName + "] exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Default values
        String nodeName = "Node1";
        int port = 1099;

        if (args.length >= 1) {
            nodeName = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default 1099.");
            }
        }

        startServer(port, nodeName);
    }
}
