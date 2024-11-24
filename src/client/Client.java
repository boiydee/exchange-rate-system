package client;

import attributes.cmdLineUI.MenuOptions;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client {
    private String name;
    private final RmiMethodsInterface rmiServerMethods;
    private static final Scanner input = new Scanner(System.in);

    public Client(RmiMethodsInterface rmiServerMethods) {
        this.rmiServerMethods = rmiServerMethods;
    }

    public void runClient() throws Exception {
        rmiServerMethods.getCurrentExchangeRates();

        MenuOptions userInput = MenuOptions.GETONLINEUSERS;
        while (!userInput.equals(MenuOptions.EXIT)) {
            displayMenu();
            userInput = MenuOptions.values()[(input.nextInt()) - 1];
            input.nextLine(); // Consume newline
            switch (userInput) {
                case GETOUTGOINGTRANSFERREQUESTS -> rmiServerMethods.getOutgoingTransferRequests();
                case GETINCOMINGTRANSFERREQUESTS -> rmiServerMethods.getIncomingTransferRequests();
                case GETCURRENTUSERINFO -> System.out.println(rmiServerMethods.getCurrentUserInfo(name));
                case GETCURRENTEXCHANGERATES -> rmiServerMethods.getCurrentExchangeRates();
                case GETONLINEUSERS -> System.out.println(rmiServerMethods.getOnlineUsers());
                case SENDTRANSFERREQUESTS -> {
                    System.out.println("To transfer currencies within your account enter your own username");
                    System.out.print("Enter recipient username: ");
                    String recipient = input.nextLine();
                    System.out.print("Enter currency to transfer: ");
                    String currency = input.nextLine();
                    System.out.print("Enter amount: ");
                    double amount = input.nextDouble();
                    input.nextLine(); // Consume newline
                    rmiServerMethods.sendTransferRequest(name,recipient, currency, amount); // Extend this for parameters
                }
            }
        }
        rmiServerMethods.logout(name);
    }

    public boolean login(String name, String password) throws RemoteException {
        this.name = name;
        return rmiServerMethods.login(name, password);
    }

    public boolean setupNewAccount(String name, String password) throws RemoteException {
        this.name = name;
        return rmiServerMethods.sendNewAccountToServer(name, password);
    }

    private void displayMenu() {
        int optNum = 1;

        System.out.println("Please enter an option:");
        for (MenuOptions options : MenuOptions.values()) {
            System.out.println(optNum + ". " + options.getDescription());
            optNum++;
        }
    }
}
