package guepardoapps.mycoins.converter

import guepardoapps.mycoins.extensions.getPropertyJsonKey
import guepardoapps.mycoins.models.CoinConversion
import guepardoapps.mycoins.utils.Logger
import org.json.JSONObject

internal class JsonDataToCoinConversionConverter : IJsonDataToCoinConversionConverter {
    private val tag: String = JsonDataToCoinConversionConverter::class.java.simpleName

    override fun convertResponseToList(jsonString: String, list: MutableList<CoinConversion>): MutableList<CoinConversion> {
        val updatedList = mutableListOf<CoinConversion>()

        try {
            val jsonObject = JSONObject(jsonString)

            for (coinConversion in list){
                val coinJsonObject = jsonObject.getJSONObject(coinConversion.coinType.type)
                coinConversion.eurValue = coinJsonObject.getDouble(coinConversion.getPropertyJsonKey(coinConversion::eurValue.name).key)
                coinConversion.usDollarValue = coinJsonObject.getDouble(coinConversion.getPropertyJsonKey(coinConversion::usDollarValue.name).key)
                updatedList.add(coinConversion)
            }
        } catch (exception: Exception) {
            Logger.instance.error(tag, exception)
        }

        return updatedList
    }
}