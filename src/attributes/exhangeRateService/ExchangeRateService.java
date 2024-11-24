package attributes.exhangeRateService;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ExchangeRateService {
    public Map<String, Double> getConvertionRates(String currencyCode) {
        Map<String, Double> rateMap = new HashMap<>();
        try {
            String url_str = "https://open.er-api.com/v6/latest/" + currencyCode;

            // Making Request
            URL url = new URL(url_str);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            // Convert to JSON
            JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
            JsonObject jsonobj = root.getAsJsonObject();

            // Accessing rates in object
            JsonObject ratesObj = jsonobj.getAsJsonObject("rates");

            // Getting exchange rates
            double usdRate = ratesObj.get("USD") != null ? ratesObj.get("USD").getAsDouble() : -1;
            double gbpRate = ratesObj.get("GBP") != null ? ratesObj.get("GBP").getAsDouble() : -1;
            double eurRate = ratesObj.get("EUR") != null ? ratesObj.get("EUR").getAsDouble() : -1;
            double yenRate = ratesObj.get("JPY") != null ? ratesObj.get("JPY").getAsDouble() : -1;


            if (usdRate != -1){
                rateMap.put("USD",usdRate);
            }
            if (gbpRate != -1){
                rateMap.put("GBP",gbpRate);
            }
            if (eurRate != -1){
                rateMap.put("EUR",eurRate);
            }
            if (yenRate != -1){
                rateMap.put("JPY",yenRate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rateMap;
    }
}
