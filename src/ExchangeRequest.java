import java.util.Currency;
import java.util.Map;

public class ExchangeRequest {
    private final String originAccount;
    private final String destinationAccount;
    private TransactionState state;
    private final Map<Currency, Double> amounts;

    public ExchangeRequest(String originAccount, String destinationAccount, TransactionState state, Map<Currency, Double> amounts) {
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
        this.state = state;
        this.amounts = amounts;
    }

    public String getOriginAccount() {
        return originAccount;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public TransactionState getState() {
        return state;
    }

    public Map<Currency, Double> getAmounts() {
        return amounts;
    }

    public void setState(TransactionState state) {
        this.state = state;
    }



}
