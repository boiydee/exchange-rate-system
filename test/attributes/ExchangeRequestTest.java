package attributes;

import attributes.exhangeRateService.ExchangeRequest;
import org.junit.jupiter.api.Test;

import java.util.Currency;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExchangeRequestTest {

    @Test
    public void instantiates(){
        HashMap<Currency, Double> currencyMap = new HashMap<>();
        currencyMap.put(Currency.getInstance("GBP"), 10.00);
        ExchangeRequest exchangeRequest = new ExchangeRequest("originAccount", "destinationAccount", TransactionState.PENDING, new HashMap<>());

        assertEquals(exchangeRequest, exchangeRequest);
        assertEquals(exchangeRequest.getOriginAccount(), "originAccount");
        assertEquals(exchangeRequest.getDestinationAccount(), "destinationAccount");
        assertEquals(exchangeRequest.getState(), TransactionState.PENDING);

        HashMap<Currency, Double> expectedMap = new HashMap<>();
        currencyMap.put(Currency.getInstance("GBP"), 10.00);

        assertEquals(exchangeRequest.getAmounts(), expectedMap);
    }

    @Test
    public void setState(){
        HashMap<Currency, Double> currencyMap = new HashMap<>();
        currencyMap.put(Currency.getInstance("GBP"), 10.00);
        ExchangeRequest exchangeRequest = new ExchangeRequest("originAccount", "destinationAccount", TransactionState.PENDING, new HashMap<Currency, Double>());

        assertEquals(exchangeRequest.getState(), TransactionState.PENDING);
        exchangeRequest.setState(TransactionState.ACCEPTED);
        assertEquals(exchangeRequest.getState(), TransactionState.ACCEPTED);
    }
}
