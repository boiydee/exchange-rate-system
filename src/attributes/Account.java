package attributes;

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

    // getters and setters for retrieving balance amounts and altering the number of funds
    // check is put in place for setters to ensure users can't take out more money than their current balance
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
        if (amount <= this.gbpBalance){
            this.gbpBalance -= amount;
        }
        else {
            System.err.println("Not enough funds in this account to complete this action.");
        }
    }

    public void decreaseEuroBalance(double amount){
        if (amount <= this.euroBalance){
            this.euroBalance -= amount;
        }
        else {
            System.err.println("Not enough funds in this account to complete this action.");
        }
    }

    public void decreaseUsdBalance(double amount){
        if (amount <= this.usdBalance){
            this.usdBalance -= amount;
        }
        else {
            System.err.println("Not enough funds in this account to complete this action.");
        }
    }

    public void decreaseYenBalance(double amount){
        if (amount <= this.yenBalance){
            this.yenBalance -= amount;
        }
        else {
            System.err.println("Not enough funds in this account to complete this action.");
        }
    }

}
