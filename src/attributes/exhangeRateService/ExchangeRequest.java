package attributes.exhangeRateService;

import attributes.TransactionState;

import java.util.Currency;
import java.util.Map;
import java.util.UUID;

public class ExchangeRequest {
    private final String id; // Unique identifier for the request
    private final String originAccount;
    private final String destinationAccount;
    private final String currency;
    private final double amount;
    private TransactionState state;

    public ExchangeRequest(String originAccount, String destinationAccount, String currency, double amount, TransactionState state) {
        this.id = UUID.randomUUID().toString(); // Generate a unique ID
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
        this.currency = currency;
        this.amount = amount;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public String getOriginAccount() {
        return originAccount;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public String getCurrency() {
        return currency;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionState getState() {
        return state;
    }

    public void setState(TransactionState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "ExchangeRequest{" +
                "id='" + id + '\'' +
                ", originAccount='" + originAccount + '\'' +
                ", destinationAccount='" + destinationAccount + '\'' +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                ", state=" + state +
                '}';
    }
}
