package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {

    public static void startServer(int port, String nodeName) {
        try {
            // Force RMI to use 10.198.73.40 for local communication
            System.setProperty("java.rmi.server.hostname", "10.198.73.40");

            // Create or get the RMI registry
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(port);
                System.out.println("RMI Registry created on port " + port);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry(port);
                System.out.println("RMI Registry already exists on port " + port);
            }

            // Create the implementation object
            // Export on port+100 (e.g. 1199 for Node1, 1200 for Node2) to avoid
            // conflict with the registry port AND use a fixed, known port.
            int exportPort = port + 100;
            VideoImpl videoService = new VideoImpl(nodeName + "Storage", exportPort);

            // Bind the remote object (stub) in the registry
            // All nodes use the same binding name "VideoService" so the client can find it
            // easily
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

        // Take node name and port from command line arguments if provided
        // Example: java RMIServer Node2 1100
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
