import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.Scanner;

public class Client {
    private String name;
    private String password;
    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            String address = "127.0.0.1";

            System.out.println("Please Enter Your Username");
            String username = input.nextLine();
            System.out.println(username);
            System.out.println("Please Enter Your Password");
            String password = input.nextLine();
            System.out.println(password);

            Client client = new Client(username, password);
            client.runClient(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Client(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public void runClient(String address) throws Exception {
        RmiMethodsInterface stub = (RmiMethodsInterface) Naming.lookup("rmi://127.0.0.1/RmiServer");

        MenuOptions userInput = MenuOptions.GETONLINEUSERS;
        while (!userInput.equals(MenuOptions.EXIT)) {
            displayMenu();
            userInput = MenuOptions.values()[(input.nextInt()) - 1];
            input.nextLine(); // Consume newline
            switch (userInput) {
                case GETOUTGOINGTRANSFERREQUESTS -> stub.getOutgoingTransferRequests();
                case GETINCOMINGTRANSFERREQUESTS -> stub.getIncomingTransferRequests();
                case GETCURRENTUSERINFO -> stub.getCurrentUserInfo();
                case GETCURRENTEXCHANGERATES -> stub.getCurrentExchangeRates();
                case GETONLINEUSERS -> stub.getOnlineUsers();
                case SENDNEWACCOUNTTOSERVER -> {
                    System.out.print("Enter username: ");
                    String newUsername = input.nextLine();
                    System.out.print("Enter password: ");
                    String newPassword = input.nextLine();
                    stub.sendNewAccountToServer(); // Extend this for parameters
                }
                case SENDTRANSFERREQUESTS -> {
                    System.out.print("Enter recipient username: ");
                    String recipient = input.nextLine();
                    System.out.print("Enter currency to transfer: ");
                    String currency = input.nextLine();
                    System.out.print("Enter amount: ");
                    double amount = input.nextDouble();
                    input.nextLine(); // Consume newline
                    stub.sendTransferRequest(); // Extend this for parameters
                }
            }
        }
    }

    public void displayMenu() {
        int optNum = 1;

        System.out.println("Please enter an option:");
        for (MenuOptions options : MenuOptions.values()) {
            System.out.println(optNum + ". " + options.getDescription());
            optNum++;
        }
    }
}
