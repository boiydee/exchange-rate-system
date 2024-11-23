package server;

import attributes.Account;
import attributes.exhangeRateService.ExchangeRateService;
import attributes.exhangeRateService.ExchangeRequest;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Server {
    private static final int PORT = 12345;
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final List<String> onlineUsers = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, Double> exchangeRates = new ConcurrentHashMap<>();
    private final List<ExchangeRequest> exchangeRequests = Collections.synchronizedList(new ArrayList<>());
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private final Lock balanceLock = new ReentrantLock();
    private final ReadWriteLock exchangeRateLock = new ReentrantReadWriteLock();
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    public Server() {
        loadAccounts();
        loadExchangeRates();
    }

    private synchronized void loadAccounts() {
        System.out.println("Loading accounts from file...");
        File file = new File("src/bankAccounts.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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

                    Account account = new Account(username, password,0,0,0,0);
                    account.setGbpBalance(gbpBalance);
                    account.setUsdBalance(usdBalance);
                    account.setEuroBalance(euroBalance);
                    account.setYenBalance(yenBalance);

                    accounts.put(username, account);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void loadExchangeRates() {
        System.out.println("Fetching exchange rates from attirbutes.exhangeRateService.ExchangeRateService...");
        Map<String, Double> latestRates = exchangeRateService.fetchLatestRates();
        exchangeRates.clear(); // Clear existing rates
        for (Map.Entry<String, Double> entry : latestRates.entrySet()) {
            String currency = entry.getKey();
            Double rate = entry.getValue();

            // Add basic exchange rates for bidirectional conversions
            exchangeRates.put(currency + "-USD", rate); // Assuming all rates are against USD
            if (!currency.equals("USD")) { // Avoid self-conversion for USD
                exchangeRates.put("USD-" + currency, 1 / rate); // Reverse rate
            }
        }
        System.out.println("Exchange rates updated: " + exchangeRates);
    }

    private synchronized void saveAccounts() {
        System.out.println("Saving accounts to file...");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/bankAccounts.txt"))) {
            for (Account account : accounts.values()) {
                writer.write(account.getUsername() + "," + account.getPassword() + ","
                        + account.getGbpBalance() + "," + account.getUsdBalance() + ","
                        + account.getEuroBalance() + "," + account.getYenBalance());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean createAccount(String username, String password) {
        if (accounts.containsKey(username)) {
            return false;
        }
        Account newAccount = new Account(username, password, 0, 0, 0 , 0);
        accounts.put(username, newAccount);
        saveAccounts();
        return true;
    }

    public synchronized boolean loginUser(String username, String password) {
        Account account = accounts.get(username);
        if (account != null && account.getPassword().equals(password)) {
            onlineUsers.add(username);
            return true;
        }
        return false;
    }

    public synchronized void logoutUser(String username) {
        onlineUsers.remove(username);
    }

    public synchronized List<String> getOnlineUsers() {
        return new ArrayList<>(onlineUsers);
    }

    public synchronized Map<String, Double> getAccountBalances(String username) {
        Account account = accounts.get(username);
        if (account != null) {
            Map<String, Double> balances = new HashMap<>();
            balances.put("GBP", account.getGbpBalance());
            balances.put("USD", account.getUsdBalance());
            balances.put("EUR", account.getEuroBalance());
            balances.put("YEN", account.getYenBalance());
            return balances;
        }
        return Collections.emptyMap();
    }

    public boolean transferWithinAccount(String username, String fromCurrency, String toCurrency, float amount) {
        balanceLock.lock();
        try {
            Account account = accounts.get(username);
            if (account != null && hasSufficientFunds(account, fromCurrency, amount)) {
                float exchangeRate = (float) getExchangeRate(fromCurrency + "-" + toCurrency);
                adjustBalance(account, fromCurrency, -amount);
                adjustBalance(account, toCurrency, amount * exchangeRate);
                saveAccounts();
                return true;
            }
            return false;
        } finally {
            balanceLock.unlock();
        }
    }

    public boolean transferCurrency(String fromUser, String toUser, String currencyType, double amount) {
        balanceLock.lock();
        try {
            Account fromAccount = accounts.get(fromUser);
            Account toAccount = accounts.get(toUser);
            if (fromAccount != null && toAccount != null && hasSufficientFunds(fromAccount, currencyType, amount)) {
                adjustBalance(fromAccount, currencyType, -amount);
                adjustBalance(toAccount, currencyType, amount);
                saveAccounts();
                return true;
            }
            return false;
        } finally {
            balanceLock.unlock();
        }
    }

    private boolean hasSufficientFunds(Account account, String currency, double amount) {
        return switch (currency.toUpperCase()) {
            case "GBP" -> account.getGbpBalance() >= amount;
            case "USD" -> account.getUsdBalance() >= amount;
            case "EUR" -> account.getEuroBalance() >= amount;
            case "YEN" -> account.getYenBalance() >= amount;
            default -> false;
        };
    }

    private void adjustBalance(Account account, String currency, double amount) {
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

    public void updateExchangeRates() {
        exchangeRateLock.writeLock().lock();
        try {
            Map<String, Double> rates = exchangeRateService.fetchLatestRates();
            exchangeRates.putAll(rates);
        } finally {
            exchangeRateLock.writeLock().unlock();
        }
    }

    public double getExchangeRate(String currencyPair) {
        exchangeRateLock.readLock().lock();
        try {
            return exchangeRates.getOrDefault(currencyPair, 1.0);
        } finally {
            exchangeRateLock.readLock().unlock();
        }
    }
}
