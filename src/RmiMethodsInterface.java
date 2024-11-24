import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface RmiMethodsInterface extends Remote {
    List<String> getOutgoingTransferRequests() throws RemoteException;

    List<String> getIncomingTransferRequests(String username) throws RemoteException;

    List<String> getCurrentUserInfo() throws RemoteException;

    Map<String, Double> getCurrentExchangeRates() throws RemoteException;

    List<String> getOnlineUsers() throws RemoteException;

    void sendTransferRequest(String sender, String recipient, String currency, double amount) throws RemoteException;

    void sendTransferRequestResponse(String requestId, boolean accepted) throws RemoteException;

    void sendNewAccountToServer(String username, String password) throws RemoteException;

    void updateAccountBalance(String username, String currency, double amount) throws RemoteException;
}
