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
    private final String accountsFilePath = "src/bankAccounts.txt";


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
                    account.setGbp_balance(gbpBalance);
                    account.setUsd_balance(usdBalance);
                    account.setEuro_balance(euroBalance);
                    account.setYen_balance(yenBalance);

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
//    public void saveAccounts() {
//        fileLock.writeLock().lock();
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/bankAccounts.txt"))) {
//            for (Account account : accounts.values()) {
//                writer.write(account.getUsername() + "," + account.getPassword() + ","
//                        + account.getGbp_balance() + "," + account.getUsd_balance() + ","
//                        + account.getEuro_balance() + "," + account.getYen_balance());
//                writer.newLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            fileLock.writeLock().unlock();
//        }
//    }

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


    public List<String> getAllUserInfo(String username) {
        Account account = accounts.get(username);
        List<String> userInfo = new ArrayList<>();
        if (account != null) {
            userInfo.add("User: " + username +
                    ", GBP: " + account.getGbp_balance() +
                    ", USD: " + account.getUsd_balance() +
                    ", EUR: " + account.getEuro_balance() +
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

    public void updateAccountBalance(String username, String currency, double amount) throws IOException {
        balanceLock.lock();
        try {
            Account account = accounts.get(username);
            if (account != null) {
                switch (currency.toUpperCase()) {
                    case "GBP" -> account.setGbp_balance((float) amount);
                    case "USD" -> account.setUsd_balance((float) amount);
                    case "EUR" -> account.setEuro_balance((float) amount);
                    case "YEN" -> account.setYen_balance((float) amount);
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
            case "GBP" -> account.setGbp_balance(amount);
            case "USD" -> account.setUsd_balance(amount);
            case "EUR" -> account.setEuro_balance(amount);
            case "YEN" -> account.setYen_balance(amount);
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



    // Verify or Create Account
    public boolean verifyAccount(String username, String password) throws IOException {
        fileLock.writeLock().lock(); // Write lock for creating accounts
        try {
            // Check if account exists in memory
            if (accounts.containsKey(username)) {
                Account account = accounts.get(username);
                return account.getPassword().equals(password);
            }

            // Load account from file if not in memory
            File accountsFile = new File(accountsFilePath);
            if (!accountsFile.exists()) {
                accountsFile.createNewFile();
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(accountsFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] details = line.split(",");
                    if (details.length >= 2 && details[0].equals(username)) {
                        if (details[1].equals(password)) {
                            // Load account into memory
                            Account account = new Account(username, password);
                            account.setGbp_balance(Float.parseFloat(details[2]));
                            account.setUsd_balance(Float.parseFloat(details[3]));
                            account.setEuro_balance(Float.parseFloat(details[4]));
                            account.setYen_balance(Float.parseFloat(details[5]));
                            accounts.put(username, account);
                            return true;
                        } else {
                            return false; // Invalid password
                        }
                    }
                }
            }

            // If account does not exist, create it
            Account newAccount = new Account(username, password);
            accounts.put(username, newAccount);
            saveAccounts(); // Save the new account to the file
            return true;
        } finally {
            fileLock.writeLock().unlock();
        }
    }

    // Save Accounts to File
    public void saveAccounts() {
        fileLock.writeLock().lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(accountsFilePath))) {
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

    // Add user to the online users list
    public synchronized void addOnlineUser(String username) {
        if (!onlineUsers.contains(username)) {
            onlineUsers.add(username);
        }
    }

    // Remove user from the online users list
    public synchronized void removeOnlineUser(String username) {
        onlineUsers.remove(username);
    }

    // Get the list of online users
    public List<String> getOnlineUsers() {
        synchronized (onlineUsers) {
            return List.copyOf(onlineUsers);
        }
    }



}
