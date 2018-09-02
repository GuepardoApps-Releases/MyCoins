package guepardoapps.mycoins.converter

import guepardoapps.mycoins.models.Coin
import guepardoapps.mycoins.models.CoinConversion

internal interface IJsonDataToCoinConversionConverter {
    fun convertResponseToList(jsonString: String, coinList: MutableList<Coin>): MutableList<CoinConversion>
}