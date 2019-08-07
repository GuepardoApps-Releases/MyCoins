package guepardoapps.mycoins.services.coin

import android.content.Context
import guepardoapps.mycoins.models.CoinType
import guepardoapps.mycoins.models.Coin
import guepardoapps.mycoins.models.CoinConversion
import guepardoapps.mycoins.models.CoinTrend

internal interface ICoinService {

    fun addCoin(coin: Coin)

    fun deleteCoin(coin: Coin)

    fun getCoinById(id: String): Coin?

    fun getCoinConversion(coinType: CoinType): CoinConversion?

    fun getCoinList(): MutableList<Coin>

    fun getCoinTrend(coinType: CoinType): MutableList<CoinTrend>

    fun initialize(context: Context)

    fun loadCoinConversion(coinType: CoinType)

    fun loadCoinTrend(coinType: CoinType)

    fun updateCoin(coin: Coin)
}