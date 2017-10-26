package guepardoapps.mycoins.common.converter;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import guepardoapps.mycoins.basic.dto.CoinDto;

public class JsonDataToCoinAggregateConverter {
    private static final String TAG = JsonDataToCoinAggregateConverter.class.getSimpleName();

    public static CoinDto UpdateAggregate(@NonNull CoinDto coinDto, @NonNull String responseString, @NonNull String currency) throws JSONException {
        if (responseString.length() <= 2) {
            return coinDto;
        }

        JSONObject jsonObject = new JSONObject(responseString);

        JSONArray dataArray = jsonObject.getJSONArray("Data");
        int arraySize = dataArray.length();

        JSONObject firstEntry = dataArray.getJSONObject(0);
        double openValue = firstEntry.getDouble("open");

        JSONObject lastEntry = dataArray.getJSONObject(arraySize - 1);
        double closeValue = lastEntry.getDouble("close");

        double difference = closeValue - openValue;

        if (currency.contains("EUR")) {
            coinDto.SetEuroAggregation(difference > 0 ? CoinDto.Aggregation.Rise : CoinDto.Aggregation.Fall);
        } else if (currency.contains("USD")) {
            coinDto.SetUsDollarAggregation(difference > 0 ? CoinDto.Aggregation.Rise : CoinDto.Aggregation.Fall);
        }

        return coinDto;
    }
}
