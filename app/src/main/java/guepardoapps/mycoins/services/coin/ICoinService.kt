package guepardoapps.mycoins.services.coin

import android.content.Context
import guepardoapps.mycoins.models.CoinType
import guepardoapps.mycoins.models.Coin
import guepardoapps.mycoins.models.CoinConversion
import guepardoapps.mycoins.models.CoinTrend

internal interface ICoinService {
    fun initialize(context: Context)

    fun getCoinList(): MutableList<Coin>
    fun getCoinById(id: Int): Coin?
    fun addCoin(coin: Coin)
    fun updateCoin(coin: Coin)
    fun deleteCoin(coin: Coin)

    fun getCoinConversion(coinType: CoinType): CoinConversion?
    fun getCoinTrend(coinType: CoinType): MutableList<CoinTrend>

    fun loadCoinConversion(coinType: CoinType)
    fun loadCoinTrend(coinType: CoinType)
}