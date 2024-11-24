package client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

import server.Server;

public class RmiServerMethods extends UnicastRemoteObject implements RmiMethodsInterface {
    private final Server server;

    public RmiServerMethods(Server server) throws RemoteException {
        super();
        this.server = server;
    }

    @Override
    public void getOutgoingTransferRequests() {
        // Example: Print outgoing transfer requests (logic needed in server.Server)
        System.out.println("Fetching outgoing transfer requests...");
    }

    @Override
    public void getIncomingTransferRequests() {
        System.out.println("Fetching incoming transfer requests...");
    }

    @Override
    public String getCurrentUserInfo(String username) {
        System.out.println("Fetching current user info...");
        Map<String, Double> balances = server.getAccountBalances(username);
        double gbp = balances.get("GBP");
        double usd = balances.get("USD");
        double eur = balances.get("EUR");
        double yen = balances.get("JPY");
        return String.format("Username: %s\nGBP: £%.2f\nUSD: $%.2f\nEUR: €%.2f\nJPY: ¥%.2f\n", username, gbp, usd, eur, yen);
    }

    @Override
    public Map<String, Double> getCurrentExchangeRates(String currency) {
        return server.getCurrencyExchangeRates(currency);
    }

    @Override
    public String getOnlineUsers() {
        return "Online Users: " + server.getOnlineUsers();
    }

    @Override
    public void sendTransferRequest(String sender, String recipient, String currency, double amount) {
        System.out.println("Send Transfer Request...");
        server.transferCurrency(sender, recipient, currency, amount);
    }

    @Override
    public void sendTransferRequestResponse() {
        System.out.println("Send Transfer Request Response...");
    }

    @Override
    public boolean sendNewAccountToServer(String name, String password) {
        System.out.println("Sending new account to server...");
        return server.createAccount(name, password);
    }

    @Override
    public boolean login(String name, String password) {
        return server.loginUser(name, password);
    }

    @Override
    public void logout(String name){
        server.logoutUser(name);
    }

}
