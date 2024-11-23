package server;

import attributes.Account;
import attributes.exhangeRateService.ExchangeRateService;
import attributes.exhangeRateService.ExchangeRequest;

import java.io.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Server {
    private static final int PORT = 12345;
    private final ReadWriteLock fileLock = new ReentrantReadWriteLock();
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final List<String> onlineUsers = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, Map<String,Double>> exchangeRates = new ConcurrentHashMap<>();
    private final List<ExchangeRequest> exchangeRequests = Collections.synchronizedList(new ArrayList<>());
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final Lock balanceLock = new ReentrantLock();
    private final ReadWriteLock exchangeRateLock = new ReentrantReadWriteLock();

    public Server() {
        loadAccounts();
        loadExchangeRates();
    }

    private void loadAccounts() {
        fileLock.readLock().lock();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/bankAccounts.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length >= 6) {
                    String username = details[0];
                    String password = details[1];
                    float gbpBalance = Float.parseFloat(details[2]);
                    float usdBalance = Float.parseFloat(details[3]);
                    float euroBalance = Float.parseFloat(details[4]);
                    float yenBalance = Float.parseFloat(details[5]);

                    Account account = new Account(username, password);
                    account.set_gbp_balance(gbpBalance);
                    account.set_usd_balance(usdBalance);
                    account.set_euro_balance(euroBalance);
                    account.set_yen_balance(yenBalance);

                    accounts.put(username, account);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.readLock().unlock();
        }
    }

    private void saveAccounts() {
        fileLock.writeLock().lock(); // Acquire the write lock
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/bankAccounts.txt"))) {
            for (Account account : accounts.values()) {
                writer.write(account.getUsername() + "," + account.getPassword() + ","
                        + account.get_gbp_balance() + "," + account.get_usd_balance() + ","
                        + account.get_euro_balance() + "," + account.get_yen_balance());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.writeLock().unlock(); // Always release the write lock
        }
    }


    private synchronized void loadExchangeRates() {
        System.out.println("Fetching exchange rates from ExchangeRateService...");
        Map<String, Double> latestRates = exchangeRateService.fetchLatestRates();
        exchangeRates.clear();
        for (Map.Entry<String, Double> entry : latestRates.entrySet()) {
            String currency = entry.getKey();
            Double rate = entry.getValue();
            exchangeRates.put(currency + "-USD", rate);
            if (!currency.equals("USD")) {
                exchangeRates.put("USD-" + currency, 1 / rate);
            }
        }
        System.out.println("Exchange rates updated: " + exchangeRates);
    }

    public boolean createAccount(String username, String password) {
        return accounts.putIfAbsent(username, new Account(username, password)) == null;
    }


    public List<String> getOnlineUsers() {
        synchronized (onlineUsers) {
            return List.copyOf(onlineUsers);
        }
    }

    public List<ExchangeRequest> getExchangeRequests(String username) {
        List<ExchangeRequest> userRequests = new ArrayList<>();
        synchronized (exchangeRequests) {
            for (ExchangeRequest request : exchangeRequests) {
                if (request.getDestinationAccount().equals(username)) {
                    userRequests.add(request);
                }
            }
        }
        return userRequests;
    }



    public boolean loginUser(String username, String password) {
        Account account = accounts.get(username);
        if (account != null && account.getPassword().equals(password)) {
            synchronized (onlineUsers) {
                onlineUsers.add(username);
            }
            return true;
        }
        return false;
    }


    public synchronized void logoutUser(String username) {
        onlineUsers.remove(username);
    }

    public void updateExchangeRates() {
        exchangeRateLock.writeLock().lock();
        try {
            exchangeRates.putAll(exchangeRateService.fetchLatestRates());
        } finally {
            exchangeRateLock.writeLock().unlock();
        }
    }

    public boolean transferWithinAccount(String username, String fromCurrency, String toCurrency, float amount) {
        balanceLock.lock(); // Ensure thread safety for balance updates
        try {
            Account account = accounts.get(username);
            if (account != null && hasSufficientFunds(account, fromCurrency, amount)) {
                float exchangeRate = (float) getExchangeRate(fromCurrency + "-" + toCurrency);
                adjustBalance(account, fromCurrency, -amount);
                adjustBalance(account, toCurrency, amount * exchangeRate);
                saveAccounts(); // Persist updated balances
                return true;
            }
            return false;
        } finally {
            balanceLock.unlock(); // Always release the lock
        }
    }

    private void adjustBalance(Account account, String currency, float amount) {
        switch (currency.toUpperCase()) {
            case "GBP":
                account.addToGbpBalance(amount);
                break;
            case "USD":
                account.addToUsdBalance(amount);
                break;
            case "EUR":
                account.addToEuroBalance(amount);
                break;
            case "YEN":
                account.addToYenBalance(amount);
                break;
        }
    }


    public double getExchangeRate(String currencyPair) {
        exchangeRateLock.readLock().lock(); // Ensure thread-safe read operations
        try {
            return exchangeRates.getOrDefault(currencyPair, 1.0);
        } finally {
            exchangeRateLock.readLock().unlock(); // Always release the lock
        }
    }


    private boolean hasSufficientFunds(Account account, String currency, float amount) {
        switch (currency.toUpperCase()) {
            case "GBP":
                return account.get_gbp_balance() >= amount;
            case "USD":
                return account.get_usd_balance() >= amount;
            case "EUR":
                return account.get_euro_balance() >= amount;
            case "YEN":
                return account.get_yen_balance() >= amount;
            default:
                return false;
        }
    }



    public boolean transferCurrency(String fromUser, String toUser, String currencyType, float amount) {
        balanceLock.lock(); // Ensure thread safety for balance updates
        try {
            Account fromAccount = accounts.get(fromUser);
            Account toAccount = accounts.get(toUser);
            if (fromAccount != null && toAccount != null && hasSufficientFunds(fromAccount, currencyType, amount)) {
                adjustBalance(fromAccount, currencyType, -amount);
                adjustBalance(toAccount, currencyType, amount);
                saveAccounts(); // Persist updated balances
                return true;
            }
            return false;
        } finally {
            balanceLock.unlock(); // Always release the lock
        }
    }


    public static void main(String[] args) {
        try {
            Server server = new Server();
            RmiServerMethods rmiServer = new RmiServerMethods(server);

            // Start RMI registry
            LocateRegistry.createRegistry(1099);
            Naming.rebind("rmi://127.0.0.1/RmiServer", rmiServer);

            System.out.println("RMI Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
