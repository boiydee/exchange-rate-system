package attributes.exhangeRateService;

import java.util.Map;
import java.util.HashMap;

public class ExchangeRateService {
    public Map<String, Double> fetchLatestRates() {
        Map<String, Double> rates = new HashMap<>();
        // Placeholder: Simulate API call to fetch latest rates
        rates.put("USD", 1.0);
        rates.put("GBP", 0.75);
        rates.put("EUR", 0.85);
        rates.put("JPY", 110.0);
        return rates;
    }
}
