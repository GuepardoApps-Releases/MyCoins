package guepardoapps.mycoins.converter

import guepardoapps.mycoins.extensions.getPropertyJsonKey
import guepardoapps.mycoins.models.Coin
import guepardoapps.mycoins.models.CoinConversion
import guepardoapps.mycoins.utils.Logger
import org.json.JSONObject

internal class JsonDataToCoinConversionConverter : IJsonDataToCoinConversionConverter {
    private val tag: String = JsonDataToCoinConversionConverter::class.java.simpleName

    override fun convertResponseToList(jsonString: String, coinList: MutableList<Coin>): MutableList<CoinConversion> {
        val list = mutableListOf<CoinConversion>()
        Logger.instance.verbose(tag, jsonString)

        try {
            val jsonObject = JSONObject(jsonString)

            for (coin in coinList) {
                val coinJsonObject = jsonObject.getJSONObject(coin.coinType.type)

                val coinConversion = CoinConversion()
                coinConversion.coinType = coin.coinType
                coinConversion.eurValue = coinJsonObject.getDouble(coinConversion.getPropertyJsonKey(coinConversion::eurValue.name).key)
                coinConversion.usDollarValue = coinJsonObject.getDouble(coinConversion.getPropertyJsonKey(coinConversion::usDollarValue.name).key)

                list.add(coinConversion)
            }
        } catch (exception: Exception) {
            Logger.instance.error(tag, exception)
        }

        return list
    }
}