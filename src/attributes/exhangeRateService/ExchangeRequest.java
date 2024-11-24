package attributes.exhangeRateService;

import attributes.TransactionState;

import java.util.UUID;

public class ExchangeRequest {
    private final String id; // Unique identifier for the request
    private final String originAccount;
    private final String destinationAccount;
    private final String currency;
    private final double amount;
    private TransactionState state;

    // Constructor
//    public ExchangeRequest(String originAccount, String destinationAccount, String currency, double amount, TransactionState state) {
//        this.id = UUID.randomUUID().toString();
//        this.originAccount = originAccount;
//        this.destinationAccount = destinationAccount;
//        this.currency = currency;
//        this.amount = amount;
//        this.state = state;
//    }

    public ExchangeRequest(String originAccount, String destinationAccount, String currency, double amount, TransactionState state, String id) {
        this.id = id;
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
        this.currency = currency;
        this.amount = amount;
        this.state = state;
    }


    // Getters and Setters
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
