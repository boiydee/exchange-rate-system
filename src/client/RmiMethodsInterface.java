package client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface RmiMethodsInterface extends Remote {
    void getOutgoingTransferRequests() throws RemoteException;
    void getIncomingTransferRequests() throws RemoteException;
    String getCurrentUserInfo(String username) throws RemoteException;
    Map<String, Double> getCurrentExchangeRates(String currency) throws RemoteException;
    String getOnlineUsers() throws RemoteException;

    void sendTransferRequest(String sender, String recipient, String currency, double amount) throws RemoteException;
    void sendTransferRequestResponse() throws RemoteException;
    boolean login(String name, String password) throws RemoteException;
    void logout(String name) throws RemoteException;
    boolean sendNewAccountToServer(String name, String password) throws RemoteException;
}
