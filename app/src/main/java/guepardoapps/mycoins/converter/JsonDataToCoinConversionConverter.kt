package guepardoapps.mycoins.converter

import guepardoapps.mycoins.extensions.getPropertyJsonKey
import guepardoapps.mycoins.models.CoinConversion
import guepardoapps.mycoins.models.CoinType
import guepardoapps.mycoins.utils.Logger
import org.json.JSONObject

internal class JsonDataToCoinConversionConverter : IJsonDataToCoinConversionConverter {
    private val tag: String = JsonDataToCoinConversionConverter::class.java.simpleName

    override fun convertResponse(jsonString: String, coinType: CoinType): CoinConversion? =
            try {
                Logger.instance.verbose(tag, jsonString)

                val jsonObject = JSONObject(jsonString)
                val coinJsonObject = jsonObject.getJSONObject(coinType.type)

                val coinConversion = CoinConversion()
                coinConversion.coinType = coinType
                coinConversion.eurValue = coinJsonObject.getDouble(coinConversion.getPropertyJsonKey(coinConversion::eurValue.name).key)
                coinConversion.usDollarValue = coinJsonObject.getDouble(coinConversion.getPropertyJsonKey(coinConversion::usDollarValue.name).key)

                coinConversion
            } catch (exception: Exception) {
                Logger.instance.error(tag, exception)
                null
            }
}