package attributes;

import attributes.exhangeRateService.ExchangeRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExchangeRequestTest {

    @Test
    public void instantiates() {
        String originAccount = "originAccount";
        String destinationAccount = "destinationAccount";
        String currency = "GBP";
        double amount = 10.00;
        TransactionState initialState = TransactionState.PENDING;

        ExchangeRequest exchangeRequest = new ExchangeRequest(originAccount, destinationAccount, currency, amount, initialState);

        assertEquals(originAccount, exchangeRequest.getOriginAccount(), "Origin account should match");
        assertEquals(destinationAccount, exchangeRequest.getDestinationAccount(), "Destination account should match");
        assertEquals(currency, exchangeRequest.getCurrency(), "Currency should match");
        assertEquals(amount, exchangeRequest.getAmount(), "Amount should match");
        assertEquals(initialState, exchangeRequest.getState(), "Initial state should be PENDING");
        assertNotNull(exchangeRequest.getId(), "ExchangeRequest ID should not be null");
    }

    @Test
    public void setState() {
        String originAccount = "originAccount";
        String destinationAccount = "destinationAccount";
        String currency = "GBP";
        double amount = 10.00;
        TransactionState initialState = TransactionState.PENDING;

        ExchangeRequest exchangeRequest = new ExchangeRequest(originAccount, destinationAccount, currency, amount, initialState);

        assertEquals(TransactionState.PENDING, exchangeRequest.getState(), "Initial state should be PENDING");

        exchangeRequest.setState(TransactionState.ACCEPTED);
        assertEquals(TransactionState.ACCEPTED, exchangeRequest.getState(), "State should change to ACCEPTED");

        exchangeRequest.setState(TransactionState.CANCELLED);
        assertEquals(TransactionState.CANCELLED, exchangeRequest.getState(), "State should change to CANCELLED");
    }

    @Test
    public void toStringContainsAllDetails() {
        String originAccount = "originAccount";
        String destinationAccount = "destinationAccount";
        String currency = "GBP";
        double amount = 10.00;
        TransactionState initialState = TransactionState.PENDING;

        ExchangeRequest exchangeRequest = new ExchangeRequest(originAccount, destinationAccount, currency, amount, initialState);

        String result = exchangeRequest.toString();

        assertTrue(result.contains(originAccount), "String representation should contain the origin account");
        assertTrue(result.contains(destinationAccount), "String representation should contain the destination account");
        assertTrue(result.contains(currency), "String representation should contain the currency");
        assertTrue(result.contains(String.valueOf(amount)), "String representation should contain the amount");
        assertTrue(result.contains(initialState.toString()), "String representation should contain the state");
        assertTrue(result.contains(exchangeRequest.getId()), "String representation should contain the unique ID");
    }
}
