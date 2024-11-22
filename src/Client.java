import java.io.IOException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.Scanner;

public class Client {
    private String name;
    private String password;
    private Socket socket;
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
            Client client = new Client(username, password,"127.0.0.1",9000);
            client.runClient(address);
        } catch (IOException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    public Client(String name, String password, String address, int port) throws IOException {
        this.name = name;
        this.password = password;
        socket = new Socket(address, port);
    }

    public void runClient(String address) throws IOException, NotBoundException {
        RmiServerMethods stub = (RmiServerMethods) Naming.lookup(address);

        MenuOptions userInput = MenuOptions.GETONLINEUSERS;
        while(!userInput.equals(MenuOptions.EXIT))
        {
            displayMenu();
            userInput = MenuOptions.values()[(input.nextInt()) - 1];
            input.nextLine();
            switch (userInput){
                case GETOUTGOINGTRANSFERREQUESTS ->{stub.getOutgoingTransferRequests();}
                case GETINCOMINGTRANSFERREQUESTS ->{stub.getIncomingTransferRequests();}
                case GETCURRENTUSERINFO ->{stub.getCurrentUserInfo();}
                case GETCURRENTEXCHANGERATES ->{stub.getCurrentExchangeRates();}
                case GETONLINEUSERS ->{stub.getOnlineUsers();}
                case SENDNEWACCOUNTTOSERVER ->{
                    if (true) { // check if account exists
                        stub.sendNewAccountToServer();
                    } else {
                        System.out.println("Account Already exists");
                    }
                }
                case SENDTRANSFERREQUESTS ->{stub.sendTransferRequest();}
            }

        }
    }

    /**
     * Displays menu options
     */
    public void displayMenu() {
        int optNum = 1;

        System.out.println("Please enter an option:");
        for (MenuOptions options : MenuOptions.values()) {
            System.out.println(optNum + ". " + options.getDescription());
            optNum++;
        }
    }



}
