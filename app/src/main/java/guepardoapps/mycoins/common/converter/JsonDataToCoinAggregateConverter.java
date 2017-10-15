package guepardoapps.mycoins.common.converter;

import android.support.annotation.NonNull;

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
        boolean aggregated = jsonObject.getBoolean("Aggregated");

        if (currency.contains("EUR")) {
            coinDto.SetEuroAggregation(aggregated ? CoinDto.Aggregation.Rise : CoinDto.Aggregation.Fall);
        } else if (currency.contains("USD")) {
            coinDto.SetUsDollarAggregation(aggregated ? CoinDto.Aggregation.Rise : CoinDto.Aggregation.Fall);
        }

        return coinDto;
    }
}
