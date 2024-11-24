import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;

public class LoginGUI extends JFrame {
    private boolean sessionActive = false;

    public LoginGUI(RmiMethodsInterface stub) {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                // Call the server to verify or create the account
                boolean verified = stub.verifyAccount(username, password);

                if (verified) {
                    // Notify the server that this user is now online
                    stub.addOnlineUser(username);

                    sessionActive = true;
                    JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    new MainGUI(username, stub).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Unable to verify account.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Error connecting to server: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
    }

    public boolean isSessionActive() {
        return sessionActive;
    }
}
