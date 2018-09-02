package guepardoapps.mycoins.converter

import guepardoapps.mycoins.enums.CoinType
import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.extensions.getJsonKey
import guepardoapps.mycoins.extensions.getPropertyJsonKey
import guepardoapps.mycoins.models.CoinTrend
import guepardoapps.mycoins.utils.Logger
import org.json.JSONObject

internal class JsonDataToCoinTrendConverter : IJsonDataToCoinTrendConverter {
    private val tag: String = JsonDataToCoinTrendConverter::class.java.simpleName

    private val responseJsonKey = "Response"
    private val responseSuccessJsonKey = "Success"
    // private val typeJsonKey = "Type"
    // private val aggregatedJsonKey = "Aggregated"

    override fun convertResponseToList(jsonString: String, coinType: CoinType, currency: Currency): MutableList<CoinTrend> {
        val list = mutableListOf<CoinTrend>()
        Logger.instance.verbose(tag, jsonString)

        try {
            val jsonObject = JSONObject(jsonString)
            if (jsonObject.getString(responseJsonKey) != responseSuccessJsonKey) {
                Logger.instance.error(tag, "Error in parsing jsonObject in convertResponseToList: $jsonObject")
                return mutableListOf()
            }

            val coinCurrencyC = CoinTrend()
            val dataListJsonArray = jsonObject.getJSONArray(coinCurrencyC.getJsonKey().key)

            for (index in 0 until dataListJsonArray.length() step 1) {
                val dataJsonObject = dataListJsonArray.getJSONObject(index)

                val newCoinCurrency = CoinTrend()
                newCoinCurrency.id = index
                newCoinCurrency.time = dataJsonObject.getLong(newCoinCurrency.getPropertyJsonKey(newCoinCurrency::time.name).key)
                newCoinCurrency.openValue = dataJsonObject.getDouble(newCoinCurrency.getPropertyJsonKey(newCoinCurrency::openValue.name).key)
                newCoinCurrency.closeValue = dataJsonObject.getDouble(newCoinCurrency.getPropertyJsonKey(newCoinCurrency::closeValue.name).key)
                newCoinCurrency.lowValue = dataJsonObject.getDouble(newCoinCurrency.getPropertyJsonKey(newCoinCurrency::lowValue.name).key)
                newCoinCurrency.highValue = dataJsonObject.getDouble(newCoinCurrency.getPropertyJsonKey(newCoinCurrency::highValue.name).key)
                newCoinCurrency.coinType = coinType
                newCoinCurrency.currency = currency

                list.add(newCoinCurrency)
            }
        } catch (exception: Exception) {
            Logger.instance.error(tag, exception)
        }

        return list
    }
}