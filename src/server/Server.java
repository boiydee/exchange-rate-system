package server;

import attributes.Account;
import attributes.exhangeRateService.ExchangeRateService;
import attributes.exhangeRateService.ExchangeRequest;

import java.io.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 12345; // Port for socket server
    private static final int THREAD_POOL_SIZE = 10; // Number of threads for handling clients
    private final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE); // Thread pool

    public static void main(String[] args) {
        // Create and initialize ServerLogic
        ServerLogic serverLogic = new ServerLogic();

        try {
            // Start RMI registry and bind the RMI server object
            RmiMethodsInterface rmiServer = new RmiServerMethods(serverLogic);
            LocateRegistry.createRegistry(1099); // Start RMI registry on port 1099
            Naming.rebind("rmi://127.0.0.1/RmiServer", rmiServer);
            System.out.println("RMI Server is running on port 1099...");

            // Start the socket server for GUI-based client connections
            Server server = new Server();
            server.startSocketServer(serverLogic, rmiServer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSocketServer(ServerLogic serverLogic, RmiMethodsInterface rmiServer) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Socket Server is running and listening on port " + PORT + "...");

            while (true) {
                // Accept a new client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

                // Handle the client in a separate thread
                threadPool.execute(new ClientHandler(clientSocket, serverLogic, rmiServer));
            }
        } catch (IOException e) {
            System.err.println("Error starting socket server: " + e.getMessage());
        } finally {
            threadPool.shutdown(); // Gracefully shut down the thread pool
        }
    }
}
