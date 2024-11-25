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
                //case SENDNEWACCOUNTTOSERVER -> sendNewAccountToServer();
                case EXIT -> logout();
                default -> JOptionPane.showMessageDialog(MainGUI.this, "Unknown option selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void fetchOutgoingRequests() {
        try {
            // Call the server's method to get outgoing requests
            List<String> outgoingRequests = serverStub.getOutgoingTransferRequests(username);

            if (outgoingRequests.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No outgoing transfer requests found.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                StringBuilder requests = new StringBuilder("Outgoing Transfer Requests:\n");
                for (String request : outgoingRequests) {
                    requests.append(request).append("\n");
                }

                JOptionPane.showMessageDialog(
                        this,
                        requests.toString(),
                        "Outgoing Requests",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (RemoteException e) {
            showError(e);
        }
    }


    private void fetchIncomingRequests() {
        try {
            List<String> incomingRequests = serverStub.getIncomingTransferRequests(username);

            if (incomingRequests.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No incoming transfer requests found.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
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

                    // Extract the request ID from the selected request string
                    String[] requestDetails = selectedRequest.split(",");
                    if (requestDetails.length < 6) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Invalid request format.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    }

                    String requestId = requestDetails[0];
                    int startIndex = requestId.indexOf("'") + 1; // Find the first single quote and move to the next character
                    int endIndex = requestId.indexOf("'", startIndex); // Find the closing single quote
                    String formattedRequestId = requestId.substring(startIndex, endIndex);

                    System.out.println("Extracted request ID: " + requestId);

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

                    // Return to main menu after processing
                    JOptionPane.showMessageDialog(
                            this,
                            "Request processed. Returning to the main menu.",
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    returnToMainMenu();
                }
            }
        } catch (RemoteException e) {
            showError(e);
        }
    }

    private void processTransactionRequest(String requestId, boolean accepted) {
        try {
            System.out.println("Processing request ID: " + requestId + " with accepted: " + accepted);
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
            // Call the server's method to get account info for the current user
            List<String> accountInfo = serverStub.getCurrentUserInfo(username);

            // Check if the list is empty
            if (accountInfo == null || accountInfo.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No account information found.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                // Display the account info in a formatted string
                StringBuilder info = new StringBuilder("attributes.Account Info:\n");
                for (String detail : accountInfo) {
                    info.append(detail).append("\n");
                }
                JOptionPane.showMessageDialog(
                        this,
                        info.toString(),
                        "attributes.Account Information",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (RemoteException e) {
            // Show an error message if the server call fails
            showError(e);
        }
    }


    public void fetchExchangeRates() {
        try {
            // Call the server's method to get exchange rates
            Map<String, Double> exchangeRates = serverStub.getCurrentExchangeRates();

            // Check if the map is empty
            if (exchangeRates == null || exchangeRates.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No exchange rates found.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                // Display the exchange rates in a formatted string
                StringBuilder rates = new StringBuilder("Exchange Rates:\n");
                for (Map.Entry<String, Double> entry : exchangeRates.entrySet()) {
                    rates.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
                JOptionPane.showMessageDialog(
                        this,
                        rates.toString(),
                        "Exchange Rates",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (RemoteException e) {
            // Show an error message if the server call fails
            showError(e);
        }
    }


    public void fetchOnlineUsers() {
        try {
            // Call the server's method to get the list of online users
            List<String> onlineUsers = serverStub.getOnlineUsers();

            // Check if the list is empty
            if (onlineUsers.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No online users found.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                // Display the list of online users in a formatted string
                StringBuilder users = new StringBuilder("Online Users:\n");
                for (String user : onlineUsers) {
                    users.append(user).append("\n");
                }
                JOptionPane.showMessageDialog(
                        this,
                        users.toString(),
                        "Online Users",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (RemoteException e) {
            // Show an error message if the server call fails
            showError(e);
        }
    }


    public void sendTransferRequest() {
        try {
            // Prompt for recipient username
            String recipient = JOptionPane.showInputDialog(this, "Enter recipient username:");
            if (recipient == null || recipient.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Recipient username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Prompt for currency type
            String currency = JOptionPane.showInputDialog(this, "Enter currency (e.g., GBP, USD, EUR, YEN):");
            if (currency == null || currency.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Currency cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Prompt for transfer amount
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

            // Send transfer request via serverStub
            serverStub.sendTransferRequest(username, recipient, currency.toUpperCase(), amount);

            JOptionPane.showMessageDialog(this, "Transfer request sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (RemoteException e) {
            // Show an error message if the server call fails
            showError(e);
        }
    }


//    private void sendNewAccountToServer() {
//        try {
//            // Prompt for the new account username
//            String newUsername = JOptionPane.showInputDialog(this, "Enter new account username:");
//            if (newUsername == null || newUsername.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            // Prompt for the new account password
//            String newPassword = JOptionPane.showInputDialog(this, "Enter new account password:");
//            if (newPassword == null || newPassword.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            // Send the new account details to the server
//            serverStub.sendNewAccountToServer(newUsername, newPassword);
//
//            JOptionPane.showMessageDialog(this, "New account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
//        } catch (RemoteException e) {
//            // Show an error message if the server call fails
//            showError(e);
//        }
//    }


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
