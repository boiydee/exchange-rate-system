import java.io.*;
import java.util.Scanner;

/** Stores account information such as user details and different currency balances **/

public class Account {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // different balances for each exchange currency
    private float gbp_balance;
    private float usd_balance;
    private float euro_balance;
    private float yen_balance;

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
        this.gbp_balance = 0;
        this.usd_balance = 0;
        this.euro_balance = 0;
        this.yen_balance = 0;
    }

    public float get_gbp_balance() {
        return this.gbp_balance;
    }

    public float get_usd_balance() {
        return this.usd_balance;
    }

    public float get_euro_balance() {
        return this.euro_balance;
    }

    public float get_yen_balance() {
        return this.yen_balance;
    }

    public void set_gbp_balance(float gbp_balance) {
        this.gbp_balance = gbp_balance;
    }

    public void set_usd_balance(float usd_balance) {
        this.usd_balance = usd_balance;
    }

    public void set_euro_balance(float euro_balance) {
        this.euro_balance = euro_balance;
    }

    public void set_yen_balance(float yen_balance) {
        this.yen_balance = yen_balance;
    }

    public void addToGbpBalance(float amount){
        this.gbp_balance += amount;
    }

    public void addToEuroBalance(float amount){
        this.euro_balance += amount;
    }

    public void addToUsdBalance(float amount){
        this.usd_balance += amount;
    }

    public void addToYenBalance(float amount){
        this.yen_balance += amount;
    }

    public void decreaseGbpBalance(float amount){
        this.gbp_balance -= amount;
    }

    public void decreaseEuroBalance(float amount){
        this.euro_balance -= amount;
    }

    public void decreaseUsdBalance(float amount){
        this.usd_balance -= amount;
    }

    public void decreaseYenBalance(float amount){
        this.yen_balance -= amount;
    }

    // This method needs to use the accounts stored on server - using a regular file right now for testing purposes
    public void verifyAccount(String username, String password) throws IOException {
        File accounts = new File("src/bankAccounts.txt");
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
                System.out.println("Account doesn't appear to exist - creating new one...");
                createAccount(username, password);
            }

        } catch (IOException e){
            System.err.println("Exception caught: " + e.getMessage());
        }
    }

    public void createAccount(String username, String password) throws IOException {
        File accounts = new File("src/bankAccounts.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(accounts, true))){
            writer.write(username + "," + password + "," + 0 + "," + 0 + "," + 0 + "," + 0);
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

        Account account = new Account(user, pass);

        account.verifyAccount(user, pass);

    }

    // TODO: Update balance methods (unless already handled in another class)
}
