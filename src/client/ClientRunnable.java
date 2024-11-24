package client;

import java.rmi.Naming;
import java.util.Scanner;

public class ClientRunnable {

    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        boolean loggedIn = false;
        try {
            String address = "127.0.0.1";
            String rmiAddress = "rmi://" + address + "/RmiServer";
            RmiMethodsInterface rmiServerMethods = (RmiMethodsInterface) Naming.lookup(rmiAddress);


            Client client = new Client(rmiServerMethods);
            while (!loggedIn) {
                System.out.println("Login (login) New Account (newAccount)");
                String loginMethod = input.nextLine();

                System.out.println("Please Enter Your Username");
                String username = input.nextLine();
                System.out.println(username);
                System.out.println("Please Enter Your Password");
                String password = input.nextLine();

                if (loginMethod.equals("login")) {
                    loggedIn = client.login(username, password);
                } else if (loginMethod.equals("newAccount")) {
                    loggedIn = client.setupNewAccount(username, password);
                    if (loggedIn) {
                        client.login(username, password);
                    }
                }
            }
            client.runClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
