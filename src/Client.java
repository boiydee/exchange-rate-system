import javax.swing.*;
import java.rmi.Naming;

public class Client {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                RmiMethodsInterface stub = (RmiMethodsInterface) Naming.lookup("rmi://127.0.0.1/RmiServer");
                new LoginGUI().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error connecting to server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
