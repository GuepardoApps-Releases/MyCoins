package guepardoapps.mycoins.converter

import guepardoapps.mycoins.models.CoinType
import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.extensions.getJsonKey
import guepardoapps.mycoins.extensions.getPropertyJsonKey
import guepardoapps.mycoins.models.CoinTrend
import guepardoapps.mycoins.utils.Logger
import org.json.JSONObject

internal class JsonDataToCoinTrendConverter : IJsonDataToCoinTrendConverter {
    private val tag: String = JsonDataToCoinTrendConverter::class.java.simpleName

    override fun convertResponseToList(jsonString: String, coinType: CoinType, currency: Currency): MutableList<CoinTrend> =
            try {
                val list = mutableListOf<CoinTrend>()
                Logger.instance.verbose(tag, jsonString)

                val jsonObject = JSONObject(jsonString)
                if (jsonObject.getString("Response") == "Success") {
                    val coinCurrencyC = CoinTrend()
                    val dataListJsonArray = jsonObject.getJSONArray(coinCurrencyC.getJsonKey().key)

                    for (index in 0 until dataListJsonArray.length() step 1) {
                        val dataJsonObject = dataListJsonArray.getJSONObject(index)

                        val newCoinCurrency = CoinTrend().apply {
                            this.id = index
                            this.time = dataJsonObject.getLong(getPropertyJsonKey(this::time.name).key)
                            this.openValue = dataJsonObject.getDouble(getPropertyJsonKey(this::openValue.name).key)
                            this.closeValue = dataJsonObject.getDouble(getPropertyJsonKey(this::closeValue.name).key)
                            this.lowValue = dataJsonObject.getDouble(getPropertyJsonKey(this::lowValue.name).key)
                            this.highValue = dataJsonObject.getDouble(getPropertyJsonKey(this::highValue.name).key)
                            this.coinType = coinType
                            this.currency = currency
                        }
                        list.add(newCoinCurrency)
                    }
                } else {
                    Logger.instance.error(tag, "Error in parsing jsonObject in convertResponseToList: $jsonObject")
                }

                list
            } catch (exception: Exception) {
                Logger.instance.error(tag, exception)
                mutableListOf()
            }
}