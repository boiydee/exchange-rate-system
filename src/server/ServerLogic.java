package server;

import attributes.Account;
import attributes.TransactionState;
import attributes.exhangeRateService.ExchangeRateService;
import attributes.exhangeRateService.ExchangeRequest;

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
    private final String accountsFilePath = "src/resources/bankAccounts.txt";
    private final String exchangeRequetsFilePath = "src/resources/exchangeRequests.txt";


    public ServerLogic() {
        loadAccounts();
        loadExchangeRates();
        loadExchangeRequests();
    }

    // Load accounts from file
    public void loadAccounts() {
        fileLock.readLock().lock();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/bankAccounts.txt"))) {
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

    public void loadTransfers() {
        fileLock.readLock().lock();
        exchangeRequests.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(exchangeRequetsFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length >= 6) {
                    String originAccount = details[0];
                    String destinationAccount = details[1];
                    String currency = details[2];
                    float amount = Float.parseFloat(details[3]);
                    TransactionState state =
                            switch (details[4]) {
                                case "CANCELLED" -> TransactionState.CANCELLED;
                                case "PENDING" -> TransactionState.PENDING;
                                case "ACCEPTED" -> TransactionState.ACCEPTED;
                                default -> throw new IllegalStateException("Unexpected value: " + details[4]);
                            };
                    String id = details[5];

                    ExchangeRequest eRequest = new ExchangeRequest(originAccount, destinationAccount, currency, amount, state, id);
                    exchangeRequests.add(eRequest);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.readLock().unlock();
        }
    }

    public boolean createAccount(String username, String password) {
        return accounts.putIfAbsent(username, new Account(username, password)) == null;
    }

    // Mainly for Testing
    public Map<String, Account> getAccounts() {
        return accounts;
    }


    public List<String> getOutgoingRequests(String username) {
        loadTransfers();
        synchronized (exchangeRequests) {
            List<String> outgoing = new ArrayList<>();
            for (ExchangeRequest request : exchangeRequests) {
                if (request.getOriginAccount().equals(username) && request.getState() == TransactionState.PENDING) {
                    outgoing.add(request.toString());
                }
            }
            return outgoing;
        }
    }

    public List<String> getIncomingRequests(String username) {
        loadTransfers();
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
            String formattedInfo = String.format("Username: %s\nGBP: £%.2f\nUSD: $%.2f\nEUR: €%.2f\nJPY: ¥%.2f\n", username, account.getGbp_balance(), account.getUsd_balance(), account.getEuro_balance(), account.getYen_balance());
            userInfo.add(formattedInfo);
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


    // Load exchange rates from the live service
    public synchronized void loadExchangeRates() {
        System.out.println("Fetching exchange rates from ExchangeRateService...");
        exchangeRates.clear();
        Map<String, Double> usdlatestRates = exchangeRateService.getConvertionRates("USD");
        Map<String, Double> eurlatestRates = exchangeRateService.getConvertionRates("EUR");
        Map<String, Double> yenlatestRates = exchangeRateService.getConvertionRates("JPY");
        Map<String, Double> gbplatestRates = exchangeRateService.getConvertionRates("GBP");

        for (Map.Entry<String, Double> entry : eurlatestRates.entrySet()) {
            exchangeRates.put("EUR -> " + entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Double> entry : usdlatestRates.entrySet()) {
            exchangeRates.put("USD -> " + entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Double> entry : yenlatestRates.entrySet()) {
            exchangeRates.put("JPY -> " + entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Double> entry : gbplatestRates.entrySet()) {
            exchangeRates.put("GBP -> " + entry.getKey(), entry.getValue());
        }
    }

    // Update exchange rates dynamically
    public void updateExchangeRates() {
        exchangeRateLock.writeLock().lock();
        try {
            System.out.println("Updating exchange rates...");
            Map<String, Double> latestRates = exchangeRateService.getConvertionRates("USD");
            for (Map.Entry<String, Double> entry : latestRates.entrySet()) {
                String currency = entry.getKey();
                Double rate = entry.getValue();
                exchangeRates.put(currency + "-USD", rate);
                if (!currency.equals("USD")) {
                    exchangeRates.put("USD-" + currency, 1 / rate);
                }
            }
            System.out.println("Exchange rates updated: " + exchangeRates);
        } finally {
            exchangeRateLock.writeLock().unlock();
        }
    }


    public boolean transferWithinAccount(String username, String fromCurrency, String toCurrency, float amount) {
        balanceLock.lock();
        try {
            Account account = accounts.get(username);

            if (account == null) {
                System.out.println("Account not found for user: " + username);
                return false;
            }

            // Check sufficient funds
            if (!hasSufficientFunds(account, fromCurrency, amount)) {
                System.out.printf("Insufficient funds: %s has %.2f in %s, attempted to transfer %.2f%n",
                        username, getBalance(account, fromCurrency), fromCurrency, amount);
                return false;
            }

            // Get exchange rate
            double exchangeRate = getExchangeRate(fromCurrency + "-" + toCurrency);
            if (exchangeRate <= 0) {
                System.out.printf("Invalid exchange rate: %s-%s not found or zero.%n", fromCurrency, toCurrency);
                return false;
            }

            // Perform the transfer
            float convertedAmount = (float) (amount * exchangeRate);
            adjustBalance(account, fromCurrency, -amount);
            adjustBalance(account, toCurrency, convertedAmount);

            saveAccounts(); // Persist changes
            System.out.printf("Transfer successful: %.2f %s to %.2f %s for user %s.%n",
                    amount, fromCurrency, convertedAmount, toCurrency, username);

            return true;
        } finally {
            balanceLock.unlock();
        }
    }



    public void addTransferRequest(String sender, String recipient, String currency, double amount) {
        synchronized (exchangeRequests) {
            // Generate a unique ID for the request
            String requestId = UUID.randomUUID().toString();

            // Create a new exchange request with the ID
            ExchangeRequest newRequest = new ExchangeRequest(sender, recipient, currency, amount, TransactionState.PENDING, requestId);

            // Add the request to the list
            exchangeRequests.add(newRequest);

            // Save the updated list of requests to the file
            saveExchangeRequests();
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
            case "GBP" -> account.setGbp_balance(account.getGbp_balance() + amount);
            case "USD" -> account.setUsd_balance(account.getUsd_balance() + amount);
            case "EUR" -> account.setEuro_balance(account.getEuro_balance() + amount);
            case "YEN" -> account.setYen_balance(account.getYen_balance() + amount);
            default -> throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
    }





    private boolean hasSufficientFunds(Account account, String currency, float amount) {
        float balance = getBalance(account, currency);
        System.out.printf("Checking funds: %s has %.2f in %s, needs %.2f.%n", account.getUsername(), balance, currency, amount);
        return balance >= amount;
    }

    private float getBalance(Account account, String currency) {
        return switch (currency.toUpperCase()) {
            case "GBP" -> account.getGbp_balance();
            case "USD" -> account.getUsd_balance();
            case "EUR" -> account.getEuro_balance();
            case "YEN" -> account.getYen_balance();
            default -> -1.0f; // Invalid currency
        };
    }


    public double getExchangeRate(String currencyPair) {
        exchangeRateLock.readLock().lock();
        try {
            double rate = exchangeRates.getOrDefault(currencyPair, -1.0); // -1.0 indicates missing rate
            System.out.printf("Exchange rate for %s: %.2f%n", currencyPair, rate);
            return rate;
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
                // Deduct from the sender
                adjustBalance(fromAccount, currencyType, -amount);

                // Add to the recipient
                adjustBalance(toAccount, currencyType, amount);

                System.out.println("Transfer completed: " + amount + " " + currencyType + " from " + fromUser + " to " + toUser);
                return true;
            }

            System.out.println("Transfer failed due to insufficient funds or invalid accounts.");
            return false;
        } finally {
            balanceLock.unlock();
        }
    }






    // Verify or Create attributes.Account
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/resources/bankAccounts.txt"))) {
            for (Account account : accounts.values()) {
                writer.write(account.getUsername() + "," + account.getPassword() + ","
                        + account.getGbp_balance() + "," + account.getUsd_balance() + ","
                        + account.getEuro_balance() + "," + account.getYen_balance());
                writer.newLine();
                System.out.println("Saved account: " + account);
            }
            System.out.println("All accounts saved to bankAccounts.txt.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.writeLock().unlock();
        }
    }




    public void saveTransferRequests() {
        fileLock.writeLock().lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(exchangeRequetsFilePath))) {
            for (ExchangeRequest request : exchangeRequests) {
                writer.write(request.getOriginAccount() + "," + request.getDestinationAccount() + ","
                        + request.getCurrency() + "," + request.getAmount() + ","
                        + request.getState().getDescription() + "," + request.getId());
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

    public void processTransferRequest(String requestId, boolean accepted) {
        synchronized (exchangeRequests) {
            ExchangeRequest request = exchangeRequests.stream()
                    .filter(r -> r.getId().equals(requestId))
                    .findFirst()
                    .orElse(null);

            if (request != null) {
                TransactionState newState;

                if (accepted) {
                    boolean success = transferCurrency(
                            request.getOriginAccount(),
                            request.getDestinationAccount(),
                            request.getCurrency(),
                            (float) request.getAmount()
                    );

                    if (success) {
                        newState = TransactionState.ACCEPTED;
                        saveAccounts(); // Persist account changes
                    } else {
                        newState = TransactionState.CANCELLED;
                    }
                } else {
                    newState = TransactionState.CANCELLED;
                }

                // Update the request state
                request.setState(newState);
                saveExchangeRequests(); // Persist request state changes
            } else {
                System.out.println("Request not found for ID: " + requestId);
            }
        }
    }



    public void saveExchangeRequests() {
        synchronized (exchangeRequests) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/resources/exchangeRequests.txt"))) {
                for (ExchangeRequest request : exchangeRequests) {
                    writer.write(
                            request.getOriginAccount() + "," +
                                    request.getDestinationAccount() + "," +
                                    request.getCurrency() + "," +
                                    request.getAmount() + "," +
                                    request.getState() + "," +
                                    request.getId()
                    );
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    public void saveExchangeRequests(String acceptedRequestId, TransactionState newState) {
        synchronized (exchangeRequests) {
            System.out.println("Saving exchange requests...");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/resources/exchangeRequests.txt"))) {
                for (ExchangeRequest request : exchangeRequests) {
                    // Check if this is the request being updated
                    if (request.getId().equals(acceptedRequestId)) {
                        request.setState(newState); // Update the state
                        System.out.println("Updated request ID: " + acceptedRequestId + " to state: " + newState);
                    }

                    // Write the request to the file
                    writer.write(
                            request.getOriginAccount() + "," +
                                    request.getDestinationAccount() + "," +
                                    request.getCurrency() + "," +
                                    request.getAmount() + "," +
                                    request.getState() + "," +
                                    request.getId()
                    );
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void loadExchangeRequests() {
        synchronized (exchangeRequests) {
            try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/exchangeRequests.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] details = line.split(",");
                    if (details.length == 6) {
                        String origin = details[0];
                        String destination = details[1];
                        String currency = details[2];
                        double amount = Double.parseDouble(details[3]);
                        TransactionState state = TransactionState.valueOf(details[4]);
                        String id = details[5];

                        ExchangeRequest request = new ExchangeRequest(origin, destination, currency, amount, state, id);
                        exchangeRequests.add(request);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





}
