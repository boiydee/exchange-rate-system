import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

public class RmiServerMethods extends UnicastRemoteObject implements RmiMethodsInterface {
    private final ServerLogic serverLogic;

    public RmiServerMethods(ServerLogic serverLogic) throws RemoteException {
        super();
        this.serverLogic = serverLogic;
    }

    @Override
    public List<String> getOutgoingTransferRequests() throws RemoteException {
        return serverLogic.getOutgoingRequests();
    }

    @Override
    public List<String> getIncomingTransferRequests(String username) throws RemoteException {
        return serverLogic.getIncomingRequests(username);
    }

    @Override
    public List<String> getCurrentUserInfo() throws RemoteException {
        return serverLogic.getAllUserInfo();
    }

    @Override
    public Map<String, Double> getCurrentExchangeRates() throws RemoteException {
        return serverLogic.getExchangeRates();
    }

    @Override
    public List<String> getOnlineUsers() throws RemoteException {
        return serverLogic.getOnlineUsers();
    }

    @Override
    public void sendTransferRequest(String sender, String recipient, String currency, double amount) throws RemoteException {
        serverLogic.addTransferRequest(sender, recipient, currency, amount);
    }

    @Override
    public void sendTransferRequestResponse(String requestId, boolean accepted) throws RemoteException {
        serverLogic.processTransferRequest(requestId, accepted);
    }

    @Override
    public void sendNewAccountToServer(String username, String password) throws RemoteException {
        serverLogic.createAccount(username, password);
    }

    @Override
    public void updateAccountBalance(String username, String currency, double amount) throws RemoteException {
        serverLogic.updateAccountBalance(username, currency, amount);
    }
}
