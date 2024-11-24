package client;

import attributes.cmdLineUI.MenuOptions;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Client {
    private String name;
    private final RmiMethodsInterface rmiServerMethods;
    private static final Scanner input = new Scanner(System.in);

    public Client(RmiMethodsInterface rmiServerMethods) {
        this.rmiServerMethods = rmiServerMethods;
    }

    public void runClient() throws Exception {
        MenuOptions userInput = MenuOptions.GETONLINEUSERS;
        while (!userInput.equals(MenuOptions.EXIT)) {
            displayMenu();
            userInput = MenuOptions.values()[(input.nextInt()) - 1];
            input.nextLine(); // Consume newline
            switch (userInput) {
                case GETOUTGOINGTRANSFERREQUESTS -> rmiServerMethods.getOutgoingTransferRequests();
                case GETINCOMINGTRANSFERREQUESTS -> rmiServerMethods.getIncomingTransferRequests();
                case GETCURRENTUSERINFO -> System.out.println(rmiServerMethods.getCurrentUserInfo(name));
                case GETCURRENTEXCHANGERATES -> {
                    System.out.println("To transfer currencies within your account enter your own username");
                    System.out.print("What currency do you want to see the exchange rates for? (JPY, GBP, EUR, USD)");
                    ArrayList<String> options = new ArrayList<>();
                    options.add("JPY");
                    options.add("GBP");
                    options.add("USD");
                    options.add("EUR");
                    String chosenCurrency = input.nextLine();
                    while (!options.contains(chosenCurrency)){
                        System.out.print("What currency do you want to see the exchange rates for? (JPY, GBP, EUR, USD)");
                        chosenCurrency = input.nextLine();
                    }
                    Map<String, Double> rates = rmiServerMethods.getCurrentExchangeRates(chosenCurrency);
                    System.out.println(chosenCurrency + ":");
                    for (String currency : rates.keySet()){
                        if (rates.get(currency) != null){
                            System.out.println("\t" + currency + " 1 -> " + rates.get(currency));
                        }
                    }
                    System.out.println();
                }
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

    protected boolean login(String name, String password) throws RemoteException {
        this.name = name;
        return rmiServerMethods.login(name, password);
    }

    protected boolean setupNewAccount(String name, String password) throws RemoteException {
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
