import java.io.*;
import java.util.Scanner;

/** Stores account information such as user details and different currency balances **/

public class Account {

    private String username;
    private String password;

    private float gbp_balance;
    private float usd_balance;
    private float euro_balance;
    private float yen_balance;

    public float getGbp_balance() {
        return gbp_balance;
    }

    public float getUsd_balance() {
        return usd_balance;
    }

    public float getEuro_balance() {
        return euro_balance;
    }

    public float getYen_balance() {
        return yen_balance;
    }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
        this.gbp_balance = 0;
        this.usd_balance = 0;
        this.euro_balance = 0;
        this.yen_balance = 0;
    }

    public synchronized String getUsername() {
        return username;
    }

    public synchronized String getPassword() {
        return password;
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


        public synchronized void verifyAccount(String username, String password) throws IOException {
        File accounts = new File("src/bankAccounts.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(accounts))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(username) && details[1].equals(password)) {
                    System.out.println("Account verified for " + username);
                    return;
                }
            }
            System.out.println("Account not found. Creating new account...");
            createAccount(username, password);
        }
    }

    public synchronized void createAccount(String username, String password) throws IOException {
        File accounts = new File("src/bankAccounts.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(accounts, true))) {
            writer.write(username + "," + password + ",0,0,0,0");
            writer.newLine();
        }
    }

}
