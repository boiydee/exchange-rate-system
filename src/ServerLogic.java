import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class ServerLogic {
    private final ReadWriteLock fileLock = new ReentrantReadWriteLock();
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final List<String> onlineUsers = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, Double> exchangeRates = new ConcurrentHashMap<>();
    private final List<ExchangeRequest> exchangeRequests = Collections.synchronizedList(new ArrayList<>());
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final Lock balanceLock = new ReentrantLock();
    private final ReadWriteLock exchangeRateLock = new ReentrantReadWriteLock();

    public ServerLogic() {
        loadAccounts();
        loadExchangeRates();
    }

    // Load accounts from file
    public void loadAccounts() {
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

    // Save accounts to file
    public void saveAccounts() {
        fileLock.writeLock().lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/bankAccounts.txt"))) {
            for (Account account : accounts.values()) {
                writer.write(account.getUsername() + "," + account.getPassword() + ","
                        + account.getGbp_balance() + "," + account.getUsd_balance() + ","
                        + account.getEuro_balance() + "," + account.getYen_balance());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.writeLock().unlock();
        }
    }

    // Load exchange rates
    public synchronized void loadExchangeRates() {
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

    public List<String> getOutgoingRequests() {
        synchronized (exchangeRequests) {
            List<String> outgoing = new ArrayList<>();
            for (ExchangeRequest request : exchangeRequests) {
                if (request.getState() == TransactionState.PENDING) {
                    outgoing.add(request.toString());
                }
            }
            return outgoing;
        }
    }

    public List<String> getIncomingRequests(String username) {
        synchronized (exchangeRequests) {
            List<String> incoming = new ArrayList<>();
            for (ExchangeRequest request : exchangeRequests) {
                if (request.getDestinationAccount().equals(username) && request.getState() == TransactionState.PENDING) {
                    incoming.add(request.toString());
                }
            }
            return incoming;
        }
    }

    public List<String> getAllUserInfo() {
        List<String> userInfo = new ArrayList<>();
        for (Map.Entry<String, Account> entry : accounts.entrySet()) {
            Account account = entry.getValue();
            userInfo.add("User: " + entry.getKey() + ", GBP: " + account.getGbp_balance() +
                    ", USD: " + account.getUsd_balance() + ", EUR: " + account.getEuro_balance() +
                    ", YEN: " + account.getYen_balance());
        }
        return userInfo;
    }

    public Map<String, Double> getExchangeRates() {
        exchangeRateLock.readLock().lock();
        try {
            return new HashMap<>(exchangeRates);
        } finally {
            exchangeRateLock.readLock().unlock();
        }
    }

    public void updateExchangeRates() {
        exchangeRateLock.writeLock().lock();
        try {
            Map<String, Double> latestRates = exchangeRateService.fetchLatestRates();
            exchangeRates.putAll(latestRates);
            System.out.println("Exchange rates updated: " + latestRates);
        } finally {
            exchangeRateLock.writeLock().unlock();
        }
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

    public void addTransferRequest(String sender, String recipient, String currency, double amount) {
        synchronized (exchangeRequests) {
            exchangeRequests.add(new ExchangeRequest(sender, recipient, currency, amount, TransactionState.PENDING));
        }
    }

    public void processTransferRequest(String requestId, boolean accepted) {
        synchronized (exchangeRequests) {
            ExchangeRequest request = exchangeRequests.stream()
                    .filter(r -> r.getId().equals(requestId))
                    .findFirst()
                    .orElse(null);
            if (request != null) {
                if (accepted) {
                    transferCurrency(request.getOriginAccount(), request.getDestinationAccount(), request.getCurrency(), (float) request.getAmount());
                    request.setState(TransactionState.ACCEPTED);
                } else {
                    request.setState(TransactionState.CANCELLED);
                }
            }
        }
    }

    public void updateAccountBalance(String username, String currency, double amount) {
        balanceLock.lock();
        try {
            Account account = accounts.get(username);
            if (account != null) {
                switch (currency.toUpperCase()) {
                    case "GBP" -> account.addToGbpBalance((float) amount);
                    case "USD" -> account.addToUsdBalance((float) amount);
                    case "EUR" -> account.addToEuroBalance((float) amount);
                    case "YEN" -> account.addToYenBalance((float) amount);
                    default -> throw new IllegalArgumentException("Unsupported currency: " + currency);
                }
                saveAccounts();
            }
        } finally {
            balanceLock.unlock();
        }
    }

    private void adjustBalance(Account account, String currency, float amount) {
        switch (currency.toUpperCase()) {
            case "GBP" -> account.addToGbpBalance(amount);
            case "USD" -> account.addToUsdBalance(amount);
            case "EUR" -> account.addToEuroBalance(amount);
            case "YEN" -> account.addToYenBalance(amount);
        }
    }

    private boolean hasSufficientFunds(Account account, String currency, float amount) {
        return switch (currency.toUpperCase()) {
            case "GBP" -> account.getGbp_balance() >= amount;
            case "USD" -> account.getUsd_balance() >= amount;
            case "EUR" -> account.getEuro_balance() >= amount;
            case "YEN" -> account.getYen_balance() >= amount;
            default -> false;
        };
    }

    public double getExchangeRate(String currencyPair) {
        exchangeRateLock.readLock().lock();
        try {
            return exchangeRates.getOrDefault(currencyPair, 1.0);
        } finally {
            exchangeRateLock.readLock().unlock();
        }
    }

    public boolean transferCurrency(String fromUser, String toUser, String currencyType, float amount) {
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
}
