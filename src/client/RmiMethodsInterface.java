package client;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface RmiMethodsInterface extends Remote {
    List<String> getOutgoingTransferRequests(String username) throws RemoteException;

    List<String> getIncomingTransferRequests(String username) throws RemoteException;

    List<String> getCurrentUserInfo(String username) throws RemoteException;

    Map<String, Double> getCurrentExchangeRates() throws RemoteException;

    List<String> getOnlineUsers() throws RemoteException;

    void sendTransferRequest(String sender, String recipient, String currency, double amount) throws RemoteException;


    void updateAccountBalance(String username, String currency, double amount) throws IOException;

    boolean verifyAccount(String username, String password) throws RemoteException;

    void addOnlineUser(String username) throws RemoteException;

    void removeOnlineUser(String username) throws RemoteException;

    void sendTransferRequestResponse(String requestId, boolean accepted) throws RemoteException;

    boolean transferWithinAccount(String username, String fromCurrency, String toCurrency, float amount) throws RemoteException;



}
