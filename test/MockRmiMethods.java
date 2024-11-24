import client.RmiMethodsInterface;

import java.rmi.RemoteException;
import java.util.*;

public class MockRmiMethods implements RmiMethodsInterface {
    private final List<String> onlineUsers = new ArrayList<>();
    private final Map<String, List<String>> userRequests = new HashMap<>();
    private final Map<String, Double> exchangeRates = new HashMap<>();
    private final Map<String, Map<String, Double>> userBalances = new HashMap<>();

    public MockRmiMethods() {
        // Initialize mock data
        exchangeRates.put("GBP-USD", 1.2);
        exchangeRates.put("USD-GBP", 0.83);

        userBalances.put("testUser", Map.of(
                "GBP", 100.0,
                "USD", 200.0,
                "EUR", 300.0,
                "JPY", 400.0
        ));
    }

    @Override
    public List<String> getOutgoingTransferRequests(String username) throws RemoteException {
        return userRequests.getOrDefault(username, Collections.emptyList());
    }

    @Override
    public List<String> getIncomingTransferRequests(String username) throws RemoteException {
        return userRequests.getOrDefault(username, Collections.emptyList());
    }

    @Override
    public List<String> getCurrentUserInfo(String username) throws RemoteException {
        Map<String, Double> balances = userBalances.getOrDefault(username, Collections.emptyMap());
        List<String> info = new ArrayList<>();
        balances.forEach((currency, amount) -> info.add(currency + ": " + amount));
        return info;
    }

    @Override
    public Map<String, Double> getCurrentExchangeRates() throws RemoteException {
        return exchangeRates;
    }

    @Override
    public List<String> getOnlineUsers() throws RemoteException {
        return onlineUsers;
    }

    @Override
    public void sendTransferRequest(String sender, String recipient, String currency, double amount) throws RemoteException {
        userRequests.computeIfAbsent(recipient, k -> new ArrayList<>()).add(sender + " requests " + amount + " " + currency);
    }

    @Override
    public void sendTransferRequestResponse(String requestId, boolean accepted) throws RemoteException {
        // No-op for mock
    }

    @Override
    public void updateAccountBalance(String username, String currency, double amount) throws RemoteException {
        userBalances.computeIfAbsent(username, k -> new HashMap<>()).put(currency, amount);
    }

    @Override
    public boolean verifyAccount(String username, String password) throws RemoteException {
        return true;
    }

    @Override
    public void addOnlineUser(String username) throws RemoteException {
        onlineUsers.add(username);
    }

    @Override
    public void removeOnlineUser(String username) throws RemoteException {
        onlineUsers.remove(username);
    }
}
