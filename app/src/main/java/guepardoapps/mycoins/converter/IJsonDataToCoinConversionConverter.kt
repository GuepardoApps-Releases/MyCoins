package guepardoapps.mycoins.converter

import guepardoapps.mycoins.models.CoinConversion

internal interface IJsonDataToCoinConversionConverter {
    fun convertResponseToList(jsonString: String, list: MutableList<CoinConversion>): MutableList<CoinConversion>
}