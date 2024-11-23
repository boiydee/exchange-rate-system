import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class RmiServerMethods extends UnicastRemoteObject implements RmiMethodsInterface {
    private final Server server;

    public RmiServerMethods(Server server) throws RemoteException {
        super();
        this.server = server;
    }

    @Override
    public void getOutgoingTransferRequests() throws RemoteException {
        System.out.println("Fetching outgoing transfer requests...");
        // Add logic for outgoing requests
    }

    @Override
    public void getIncomingTransferRequests(String username) throws RemoteException {
        List<ExchangeRequest> incomingRequests = server.getExchangeRequests(username);
        for (ExchangeRequest request : incomingRequests) {
            System.out.println(request);
        }
    }


    @Override
    public void getCurrentUserInfo() throws RemoteException {
        System.out.println("Fetching current user info...");
    }

    @Override
    public void getCurrentExchangeRates() throws RemoteException {
        System.out.println("Fetching exchange rates...");
        server.updateExchangeRates();
    }

    @Override
    public void getOnlineUsers() throws RemoteException {
        System.out.println("Fetching online users: " + server.getOnlineUsers());
    }

    @Override
    public void sendTransferRequest() throws RemoteException {
        System.out.println("Sending transfer request...");
    }

    @Override
    public void sendTransferRequestResponse() throws RemoteException {
        System.out.println("Responding to transfer request...");
    }

    @Override
    public void sendNewAccountToServer() throws RemoteException {
        System.out.println("Creating new account on the server...");
    }
}
