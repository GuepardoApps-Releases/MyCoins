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

                val coinJsonObject = JSONObject(jsonString).getJSONObject(coinType.type)
                val coinConversion = CoinConversion().apply {
                    this.coinType = coinType
                    this.eurValue = coinJsonObject.getDouble(getPropertyJsonKey(this::eurValue.name).key)
                    this.usDollarValue = coinJsonObject.getDouble(getPropertyJsonKey(this::usDollarValue.name).key)
                }

                coinConversion
            } catch (exception: Exception) {
                Logger.instance.error(tag, exception)
                null
            }
}