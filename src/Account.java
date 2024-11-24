import java.io.Serializable;

public class Account implements Serializable {
    private final String username;
    private final String password;

    private float gbp_balance;
    private float usd_balance;
    private float euro_balance;
    private float yen_balance;

    // Constructor
    public Account(String username, String password) {
        this.username = username;
        this.password = password;
        this.gbp_balance = 0;
        this.usd_balance = 0;
        this.euro_balance = 0;
        this.yen_balance = 0;
    }

    // Getters
    public synchronized String getUsername() {
        return username;
    }

    public synchronized String getPassword() {
        return password;
    }

    public synchronized float getGbp_balance() {
        return gbp_balance;
    }

    public synchronized float getUsd_balance() {
        return usd_balance;
    }

    public synchronized float getEuro_balance() {
        return euro_balance;
    }

    public synchronized float getYen_balance() {
        return yen_balance;
    }

    // Setters
    public synchronized void setGbp_balance(float gbp_balance) {
        this.gbp_balance = gbp_balance;
    }

    public synchronized void setUsd_balance(float usd_balance) {
        this.usd_balance = usd_balance;
    }

    public synchronized void setEuro_balance(float euro_balance) {
        this.euro_balance = euro_balance;
    }

    public synchronized void setYen_balance(float yen_balance) {
        this.yen_balance = yen_balance;
    }

    // Balance Modifiers
    public synchronized void addToGbpBalance(float amount) {
        this.gbp_balance += amount;
    }

    public synchronized void addToUsdBalance(float amount) {
        this.usd_balance += amount;
    }

    public synchronized void addToEuroBalance(float amount) {
        this.euro_balance += amount;
    }

    public synchronized void addToYenBalance(float amount) {
        this.yen_balance += amount;
    }

    public synchronized void decreaseGbpBalance(float amount) {
        this.gbp_balance -= amount;
    }

    public synchronized void decreaseUsdBalance(float amount) {
        this.usd_balance -= amount;
    }

    public synchronized void decreaseEuroBalance(float amount) {
        this.euro_balance -= amount;
    }

    public synchronized void decreaseYenBalance(float amount) {
        this.yen_balance -= amount;
    }

    @Override
    public synchronized String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", gbp_balance=" + gbp_balance +
                ", usd_balance=" + usd_balance +
                ", euro_balance=" + euro_balance +
                ", yen_balance=" + yen_balance +
                '}';
    }
}
