package attributes;

import java.io.*;
import java.util.Scanner;

/** Stores account information such as user details and different currency balances **/

public class Account {

    private final String username;
    private final String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // different balances for each exchange currency
    private double gbpBalance;
    private double usdBalance;
    private double euroBalance;
    private double yenBalance;

    public Account(String username, String password, double gbpBalance, double usdBalance, double euroBalance, double yenBalance) {
        this.username = username;
        this.password = password;
        this.gbpBalance = gbpBalance;
        this.usdBalance = usdBalance;
        this.euroBalance = euroBalance;
        this.yenBalance = yenBalance;
    }

    public double getGbpBalance() {
        return this.gbpBalance;
    }

    public double getUsdBalance() {
        return this.usdBalance;
    }

    public double getEuroBalance() {
        return this.euroBalance;
    }

    public double getYenBalance() {
        return this.yenBalance;
    }

    public void setGbpBalance(double gbp_balance) {
        this.gbpBalance = gbp_balance;
    }

    public void setUsdBalance(double usd_balance) {
        this.usdBalance = usd_balance;
    }

    public void setEuroBalance(double euro_balance) {
        this.euroBalance = euro_balance;
    }

    public void setYenBalance(double yen_balance) {
        this.yenBalance = yen_balance;
    }

    public void addToGbpBalance(double amount){
        this.gbpBalance += amount;
    }

    public void addToEuroBalance(double amount){
        this.euroBalance += amount;
    }

    public void addToUsdBalance(double amount){
        this.usdBalance += amount;
    }

    public void addToYenBalance(double amount){
        this.yenBalance += amount;
    }

    public void decreaseGbpBalance(double amount){
        this.gbpBalance -= amount;
    }

    public void decreaseEuroBalance(double amount){
        this.euroBalance -= amount;
    }

    public void decreaseUsdBalance(double amount){
        this.usdBalance -= amount;
    }

    public void decreaseYenBalance(double amount){
        this.yenBalance -= amount;
    }

    // This method needs to use the accounts stored on server - using a regular file right now for testing purposes
    public void verifyAccount(String username, String password) throws IOException {
        File accounts = new File("src/resources/bankAccounts.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(accounts))) {
            String[] details = reader.readLine().split(",");
            String user = details[0];
            String pass = details[1];

            if (user.equals(username) && pass.equals(password)) {
                System.out.println("Your account has been verified - Welcome " + username + "!");
            } else if ((user.equals(username) && !pass.equals(password)) || (!user.equals(username) && pass.equals(password))) {
                System.out.println("One or more details are incorrect. Please try again.");
                // replace with call to menu option
            } else {
                System.out.println("attirbutes.Account doesn't appear to exist - creating new one...");
                createAccount(username, password);
            }

        } catch (IOException e){
            System.err.println("Exception caught: " + e.getMessage());
        }
    }

    public void createAccount(String username, String password) throws IOException {
        File accounts = new File("src/resources/bankAccounts.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(accounts, true))){
            writer.write(username + "," + password + "," + getGbpBalance() + "," + getUsdBalance() + "," + getEuroBalance() + "," + getYenBalance());
            writer.newLine();
        }
        catch (IOException e){
            System.err.println("Exception caught: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);

        System.out.println("Please enter your account username: ");
        String user = input.nextLine();

        System.out.println("Please enter your account password: ");
        String pass = input.nextLine();

        Account account = new Account(user, pass, 5.0, 14.0, 0.0, 16);

        account.verifyAccount(user, pass);

    }

    // TODO: Update balance methods (unless already handled in another class)
}
