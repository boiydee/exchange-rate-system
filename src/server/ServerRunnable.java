package server;

import client.RmiServerMethods;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServerRunnable {
    public static void main(String[] args) {
        try {
            Server server = new Server();

            // Start RMI registry
            LocateRegistry.createRegistry(1099); // Default RMI port

            RmiServerMethods rmiServer = new RmiServerMethods(server);
            Naming.rebind("rmi://127.0.0.1/RmiServer", rmiServer);

            System.out.println("RMI server.Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
