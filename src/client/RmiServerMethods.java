package client;

import server.ServerLogic;

import java.io.IOException;
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
    public List<String> getOutgoingTransferRequests(String username) throws RemoteException {
        return serverLogic.getOutgoingRequests(username);
    }

    @Override
    public List<String> getIncomingTransferRequests(String username) throws RemoteException {
        return serverLogic.getIncomingRequests(username);
    }

    @Override
    public List<String> getCurrentUserInfo(String username) throws RemoteException {
        return serverLogic.getAllUserInfo(username);
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
    public void updateAccountBalance(String username, String currency, double amount) throws IOException {
        serverLogic.updateAccountBalance(username, currency, amount);
    }

    @Override
    public boolean verifyAccount(String username, String password) throws RemoteException {
        try {
            return serverLogic.verifyAccount(username, password);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void addOnlineUser(String username) throws RemoteException {
        serverLogic.addOnlineUser(username);
    }

    @Override
    public void removeOnlineUser(String username) throws RemoteException {
        serverLogic.removeOnlineUser(username);
    }

    @Override
    public void sendTransferRequestResponse(String requestId, boolean accepted) throws RemoteException {
        serverLogic.processTransferRequest(requestId, accepted); // Ensure this calls ServerLogic's method
    }



}
