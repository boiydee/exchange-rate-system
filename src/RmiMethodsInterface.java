import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiMethodsInterface extends Remote {
    void getOutgoingTransferRequests() throws RemoteException;
    void getIncomingTransferRequests() throws RemoteException;
    void getCurrentUserInfo() throws RemoteException;
    void getCurrentExchangeRates() throws RemoteException;
    void getOnlineUsers() throws RemoteException;

    void sendTransferRequest() throws RemoteException;
    void sendTransferRequestResponse() throws RemoteException;
    void sendNewAccountToServer() throws RemoteException;
}
