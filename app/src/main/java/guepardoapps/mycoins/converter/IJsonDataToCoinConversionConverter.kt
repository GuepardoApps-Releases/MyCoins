package guepardoapps.mycoins.converter

import guepardoapps.mycoins.models.CoinConversion
import guepardoapps.mycoins.models.CoinType

internal interface IJsonDataToCoinConversionConverter {
    fun convertResponse(jsonString: String, coinType: CoinType): CoinConversion?
}