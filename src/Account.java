import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** Stores account information such as user details and different currency balances **/

public class Account {

    private String username;
    private String password;

    // different balances for each exchange currency
    private float gbp_balance;
    private float usd_balance;
    private float euro_balance;
    private float yen_balance;

    public Account() {
        this.username = "";
        this.password = "";
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

    // checks if account already exists and allow login else create a new one
    public void verifyAccount(String username, String password) throws IOException {
        // get file of accounts from server?? put into list??
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) { // replace with file
            if(reader.readLine().contains(username) && reader.readLine().contains(password)){
                System.out.println("Your account has been verified - Welcome " + username + "!");
            }
            else if ((!reader.readLine().contains(username) && reader.readLine().contains(password)) || (!reader.readLine().contains(password) && reader.readLine().contains(username))){
                System.out.println("One or more details are incorrect. Please try again.");
                verifyAccount(username, password);

            }
            else {
                createAccount(username, password);
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        createAccount(username, password);
    }

    public void createAccount(String username, String password) {
        this.username = username;
        this.password = password;
        // write new account to server??
    }










}
