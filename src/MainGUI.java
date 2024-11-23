import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
                case SENDNEWACCOUNTTOSERVER -> sendNewAccountToServer();
                case EXIT -> logout();
                default -> JOptionPane.showMessageDialog(MainGUI.this, "Unknown option selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void fetchOutgoingRequests() {
        try {
            serverStub.getOutgoingTransferRequests();
            JOptionPane.showMessageDialog(this, "Outgoing Requests Fetched!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showError(e);
        }
    }

    private void fetchIncomingRequests() {
        try {
            serverStub.getIncomingTransferRequests(username);
            JOptionPane.showMessageDialog(this, "Incoming Requests Fetched!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showError(e);
        }
    }

    private void fetchAccountInfo() {
        SwingUtilities.invokeLater(() -> {
            try {
                serverStub.getCurrentUserInfo();
                JOptionPane.showMessageDialog(this, "Account Info Displayed!", "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                showError(e);
            }
        });
    }



    private void fetchExchangeRates() {
        try {
            serverStub.getCurrentExchangeRates();
            JOptionPane.showMessageDialog(this, "Exchange Rates Fetched!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showError(e);
        }
    }

    private void fetchOnlineUsers() {
        try {
            serverStub.getOnlineUsers();
            JOptionPane.showMessageDialog(this, "Online Users Fetched!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showError(e);
        }
    }

    private void sendTransferRequest() {
        try {
            String recipient = JOptionPane.showInputDialog(this, "Enter recipient username:");
            String currency = JOptionPane.showInputDialog(this, "Enter currency:");
            String amountStr = JOptionPane.showInputDialog(this, "Enter amount:");
            double amount = Double.parseDouble(amountStr);

            serverStub.sendTransferRequest(); // You may need to adapt parameters
            JOptionPane.showMessageDialog(this, "Transfer Request Sent!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showError(e);
        }
    }

    private void sendNewAccountToServer() {
        try {
            serverStub.sendNewAccountToServer(); // You may need to adapt parameters
            JOptionPane.showMessageDialog(this, "New Account Sent to Server!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showError(e);
        }
    }

    private void logout() {
        JOptionPane.showMessageDialog(this, "Logged out successfully!", "Info", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private void showError(Exception e) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}
