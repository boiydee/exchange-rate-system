package GUI;

import attributes.cmdLineUI.MenuOptions;
import client.RmiMethodsInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class MainGUI extends JFrame {
    private final RmiMethodsInterface serverStub;
    private final String username;

    public MainGUI(String username, RmiMethodsInterface stub) {
        this.username = username;
        this.serverStub = stub;

        setTitle("Currency Exchange System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(MenuOptions.values().length, 1)); // Adjust layout dynamically

        // Create buttons for each menu option
        for (MenuOptions option : MenuOptions.values()) {
            JButton button = new JButton(option.getDescription());
            button.addActionListener(new MenuActionListener(option));
            add(button);
        }
    }

    // ActionListener for handling button clicks based on MenuOptions
    private class MenuActionListener implements ActionListener {
        private final MenuOptions option;

        public MenuActionListener(MenuOptions option) {
            this.option = option;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (option) {
                case GETOUTGOINGTRANSFERREQUESTS -> fetchOutgoingRequests();
                case GETINCOMINGTRANSFERREQUESTS -> fetchIncomingRequests();
                case GETCURRENTUSERINFO -> fetchAccountInfo();
                case GETCURRENTEXCHANGERATES -> fetchExchangeRates();
                case GETONLINEUSERS -> fetchOnlineUsers();
                case SENDTRANSFERREQUESTS -> sendTransferRequest();
                case TRANSFERWITHINACCOUNT -> transferWithinAccount(username);
                case EXIT -> logout();
                default -> JOptionPane.showMessageDialog(MainGUI.this, "Unknown option selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void fetchOutgoingRequests() {
        try {
            List<String> outgoingRequests = serverStub.getOutgoingTransferRequests(username);

            if (outgoingRequests.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No outgoing transfer requests found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder requests = new StringBuilder("Outgoing Transfer Requests:\n");
                for (String request : outgoingRequests) {
                    requests.append(request).append("\n");
                }
                JOptionPane.showMessageDialog(this, requests.toString(), "Outgoing Requests", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RemoteException e) {
            showError(e);
        }
    }

    private void fetchIncomingRequests() {
        try {
            List<String> incomingRequests = serverStub.getIncomingTransferRequests(username);

            if (incomingRequests.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No incoming transfer requests found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String selectedRequest = (String) JOptionPane.showInputDialog(
                        this,
                        "Select a request to process:",
                        "Incoming Requests",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        incomingRequests.toArray(),
                        incomingRequests.get(0)
                );

                if (selectedRequest != null) {
                    System.out.println("Selected request: " + selectedRequest);

                    String[] requestDetails = selectedRequest.split(",");
                    if (requestDetails.length < 6) {
                        JOptionPane.showMessageDialog(this, "Invalid request format.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }


                    String requestId = requestDetails[0];
                    int startIndex = requestId.indexOf("id='") + 4;
                    int endIndex = requestId.indexOf("'", startIndex);

                    String formattedRequestId = requestId.substring(startIndex, endIndex);
                    int choice = JOptionPane.showOptionDialog(
                            this,
                            "Do you want to accept or reject this request?\n" + selectedRequest,
                            "Process Request",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new String[]{"Accept", "Reject"},
                            "Accept"
                    );

                    if (choice == JOptionPane.YES_OPTION) {
                        processTransactionRequest(formattedRequestId, true);
                    } else if (choice == JOptionPane.NO_OPTION) {
                        processTransactionRequest(formattedRequestId, false);
                    }

                    JOptionPane.showMessageDialog(this, "Request processed. Returning to the main menu.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    returnToMainMenu();
                }
            }
        } catch (RemoteException e) {
            showError(e);
        }
    }

    private void processTransactionRequest(String requestId, boolean accepted) {
        try {
            serverStub.sendTransferRequestResponse(requestId, accepted);

            String message = accepted
                    ? "The request has been accepted and processed."
                    : "The request has been rejected.";
            JOptionPane.showMessageDialog(this, message, "Request Processed", JOptionPane.INFORMATION_MESSAGE);

            returnToMainMenu();
        } catch (RemoteException e) {
            showError(e);
        }
    }

    private void returnToMainMenu() {
        dispose();
        new MainGUI(username, serverStub).setVisible(true);
    }

    public void fetchAccountInfo() {
        try {
            List<String> accountInfo = serverStub.getCurrentUserInfo(username);

            if (accountInfo == null || accountInfo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No account information found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder info = new StringBuilder("Account Info:\n");
                for (String detail : accountInfo) {
                    info.append(detail).append("\n");
                }
                JOptionPane.showMessageDialog(this, info.toString(), "Account Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RemoteException e) {
            showError(e);
        }
    }

    public void fetchExchangeRates() {
        try {
            Map<String, Double> exchangeRates = serverStub.getCurrentExchangeRates();

            if (exchangeRates == null || exchangeRates.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No exchange rates found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder rates = new StringBuilder("Exchange Rates:\n");
                for (Map.Entry<String, Double> entry : exchangeRates.entrySet()) {
                    rates.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
                JOptionPane.showMessageDialog(this, rates.toString(), "Exchange Rates", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RemoteException e) {
            showError(e);
        }
    }

    public void fetchOnlineUsers() {
        try {
            List<String> onlineUsers = serverStub.getOnlineUsers();

            if (onlineUsers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No online users found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder users = new StringBuilder("Online Users:\n");
                for (String user : onlineUsers) {
                    users.append(user).append("\n");
                }
                JOptionPane.showMessageDialog(this, users.toString(), "Online Users", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RemoteException e) {
            showError(e);
        }
    }

    public void sendTransferRequest() {
        try {
            String recipient = JOptionPane.showInputDialog(this, "Enter recipient username:");
            if (recipient == null || recipient.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Recipient username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String currency = JOptionPane.showInputDialog(this, "Enter currency (e.g., GBP, USD, EUR, JPY):");
            if (currency == null || currency.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Currency cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String amountStr = JOptionPane.showInputDialog(this, "Enter transfer amount:");
            if (amountStr == null || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Amount cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered. Please enter a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            serverStub.sendTransferRequest(username, recipient, currency.toUpperCase(), amount);
            JOptionPane.showMessageDialog(this, "Transfer request sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (RemoteException e) {
            showError(e);
        }
    }

    private void transferWithinAccount(String username) {
        try {
            String fromCurrency = JOptionPane.showInputDialog(this, "Enter the source currency (GBP, USD, EUR, JPY):");
            String toCurrency = JOptionPane.showInputDialog(this, "Enter the target currency (GBP, USD, EUR, JPY):");

            if (fromCurrency == null || toCurrency == null || fromCurrency.isEmpty() || toCurrency.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Both source and target currencies must be specified.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            float amount = Float.parseFloat(JOptionPane.showInputDialog(this, "Enter the amount to transfer:"));
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = serverStub.transferWithinAccount(username, fromCurrency.toUpperCase(), toCurrency.toUpperCase(), amount);

            if (success) {
                JOptionPane.showMessageDialog(this, "Transfer successful!", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Transfer failed. Please check your balance and try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void logout() {
        try {
            serverStub.removeOnlineUser(username);
            JOptionPane.showMessageDialog(this, "Logged out successfully!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error notifying server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.exit(0);
    }

    private void showError(Exception e) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}
