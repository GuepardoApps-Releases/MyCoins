package guepardoapps.mycoins.converter

import guepardoapps.mycoins.models.CoinType
import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.models.CoinTrend

internal interface IJsonDataToCoinTrendConverter {
    fun convertResponseToList(jsonString: String, coinType: CoinType, currency: Currency): MutableList<CoinTrend>
}