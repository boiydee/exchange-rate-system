import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiServerMethods extends UnicastRemoteObject implements RmiMethodsInterface {
    private final Server server;

    public RmiServerMethods(Server server) throws RemoteException {
        super();
        this.server = server;
    }

    @Override
    public void getOutgoingTransferRequests() throws RemoteException {
        // Example: Print outgoing transfer requests (logic needed in Server)
        System.out.println("Fetching outgoing transfer requests...");
    }

    @Override
    public void getIncomingTransferRequests() throws RemoteException {
        System.out.println("Fetching incoming transfer requests...");
    }

    @Override
    public void getCurrentUserInfo() throws RemoteException {
        System.out.println("Fetching current user info...");
    }

    @Override
    public void getCurrentExchangeRates() throws RemoteException {
        server.updateExchangeRates();
    }

    @Override
    public void getOnlineUsers() throws RemoteException {
        System.out.println("Online Users: " + server.getOnlineUsers());
    }

    @Override
    public void sendTransferRequest() throws RemoteException {
        System.out.println("Send Transfer Request...");
    }

    @Override
    public void sendTransferRequestResponse() throws RemoteException {
        System.out.println("Send Transfer Request Response...");
    }

    @Override
    public void sendNewAccountToServer() throws RemoteException {
        System.out.println("Sending new account to server...");
    }
}
