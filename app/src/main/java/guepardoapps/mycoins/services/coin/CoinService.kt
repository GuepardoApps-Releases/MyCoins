package guepardoapps.mycoins.services.coin

import android.annotation.SuppressLint
import android.content.Context
import guepardoapps.mycoins.common.Constants
import guepardoapps.mycoins.controller.SharedPreferenceController
import guepardoapps.mycoins.converter.JsonDataToCoinConversionConverter
import guepardoapps.mycoins.converter.JsonDataToCoinTrendConverter
import guepardoapps.mycoins.database.coin.DbCoin
import guepardoapps.mycoins.database.coin.DbCoinConversion
import guepardoapps.mycoins.database.coin.DbCoinTrend
import guepardoapps.mycoins.enums.CoinType
import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.enums.DownloadType
import guepardoapps.mycoins.models.Coin
import guepardoapps.mycoins.models.CoinConversion
import guepardoapps.mycoins.models.CoinTrend
import guepardoapps.mycoins.services.api.ApiService
import guepardoapps.mycoins.services.api.OnApiServiceListener
import guepardoapps.mycoins.utils.Logger

@SuppressLint("StaticFieldLeak")
internal class CoinService private constructor() : ICoinService {
    private val tag: String = CoinService::class.java.simpleName

    private var context: Context? = null

    private var jsonDataToCoinConversionConverter = JsonDataToCoinConversionConverter()
    private var jsonDataToCoinTrendConverter = JsonDataToCoinTrendConverter()
    private var apiService = ApiService()

    private object Holder {
        val instance: CoinService = CoinService()
    }

    companion object {
        val instance: CoinService by lazy { Holder.instance }
    }

    override fun initialize(context: Context) {
        if (this.context != null) {
            return
        }

        Logger.instance.verbose(tag, "initialize")

        this.context = context

        apiService.setOnApiServiceListener(object : OnApiServiceListener {
            override fun onFinished(downloadType: DownloadType, coinType: CoinType, jsonString: String, success: Boolean) {
                Logger.instance.verbose(tag, "Received onDownloadListener onFinished: $downloadType, $coinType, $jsonString, $success")

                when (downloadType) {
                    DownloadType.Conversion -> {
                        handleDownloadConversion(jsonString, success)
                    }
                    DownloadType.Trend -> {
                        handleDownloadTrend(jsonString, success, coinType)
                    }
                    DownloadType.Null -> {
                        Logger.instance.error(tag, "Received download update with downloadType Null and jsonString: $jsonString")
                    }
                }
            }
        })

        loadCoinConversion()
        for (coin in getCoinList()) {
            loadCoinTrend(coin.coinType)
        }
    }

    override fun getCoinList(): MutableList<Coin> {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return mutableListOf()
        }

        Logger.instance.verbose(tag, "getCoinList")

        val dbCoin = DbCoin(context!!)
        val list = dbCoin.get()
        dbCoin.close()

        return list
    }

    override fun getCoinById(id: Int): Coin? {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return null
        }

        Logger.instance.verbose(tag, "getCoinById $id")

        val dbCoin = DbCoin(context!!)
        val coin = dbCoin.findById(id).firstOrNull()
        dbCoin.close()

        return coin
    }

    override fun addCoin(coin: Coin) {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return
        }

        Logger.instance.verbose(tag, "addCoin $coin")

        val dbCoin = DbCoin(context!!)
        dbCoin.add(coin)
        dbCoin.close()

        loadCoinTrend(coin.coinType)
    }

    override fun updateCoin(coin: Coin) {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return
        }

        Logger.instance.verbose(tag, "updateCoin $coin")

        val dbCoin = DbCoin(context!!)
        dbCoin.update(coin)
        dbCoin.close()

        loadCoinTrend(coin.coinType)
    }

    override fun deleteCoin(coin: Coin) {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return
        }

        Logger.instance.verbose(tag, "deleteCoin $coin")

        val dbCoin = DbCoin(context!!)
        dbCoin.delete(coin)
        dbCoin.close()

        val dbCoinTrend = DbCoinTrend(context!!)
        val trendList = dbCoinTrend.findByCoinType(coin.coinType)
        for (value in trendList) {
            dbCoinTrend.delete(value)
        }
        dbCoinTrend.close()
    }

    override fun loadCoinConversion() {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return
        }

        Logger.instance.verbose(tag, "loadCoinConversion")
        apiService.load(DownloadType.Conversion, CoinType.Null, Constants.apiCoinConversion())
    }

    override fun getCoinConversion(coinType: CoinType): CoinConversion? {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return null
        }

        Logger.instance.verbose(tag, "getCoinConversion for $coinType")

        val dbCoinConversion = DbCoinConversion(context!!)
        val coinConversion = dbCoinConversion.findByCoinType(coinType).firstOrNull()
        dbCoinConversion.close()

        Logger.instance.verbose(tag, "coinConversion $coinConversion")

        return coinConversion
    }

    override fun loadCoinTrend(coinType: CoinType) {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return
        }

        Logger.instance.verbose(tag, "loadCoinTrend for $coinType")

        val currencyId = SharedPreferenceController(context!!).load(Constants.currency, Constants.currencyDefault) as Int
        val currency = Currency.values()[currencyId]

        apiService.load(DownloadType.Trend, coinType, Constants.apiCoinTrend(coinType, currency, 100))
    }

    override fun getCoinTrend(coinType: CoinType): CoinTrend? {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return null
        }

        Logger.instance.verbose(tag, "getCoinTrend for $coinType")

        val dbCoinTrend = DbCoinTrend(context!!)
        val coinTrend = dbCoinTrend.findByCoinType(coinType).firstOrNull()
        dbCoinTrend.close()

        Logger.instance.verbose(tag, "coinTrend $coinTrend")

        return coinTrend
    }

    private fun handleDownloadConversion(jsonString: String, success: Boolean) {
        if (!success || jsonString.isEmpty()) {
            Logger.instance.error(tag, "handleDownloadConversion failed")
            return
        }

        Logger.instance.verbose(tag, "handleDownloadConversion with $jsonString has success $success")

        val dbCoin = DbCoin(context!!)
        val coinList = dbCoin.get()
        dbCoin.close()

        val dbCoinConversion = DbCoinConversion(context!!)
        val updatedList = jsonDataToCoinConversionConverter.convertResponseToList(jsonString, coinList)
        dbCoinConversion.clear()
        if (updatedList.isNotEmpty()) {
            for (updatedValue in updatedList) {
                dbCoinConversion.add(updatedValue)
            }
        }
        dbCoinConversion.close()
    }

    private fun handleDownloadTrend(jsonString: String, success: Boolean, coinType: CoinType) {
        if (!success || jsonString.isEmpty()) {
            Logger.instance.error(tag, "handleDownloadTrend failed")
            return
        }

        Logger.instance.verbose(tag, "handleDownloadTrend with $jsonString has success $success for coinType $coinType")

        val currencyId = SharedPreferenceController(context!!).load(Constants.currency, Constants.currencyDefault) as Int
        val currency = Currency.values()[currencyId]

        val dbCoinTrend = DbCoinTrend(context!!)
        val updatedList = jsonDataToCoinTrendConverter.convertResponseToList(jsonString, coinType, currency)
        dbCoinTrend.clear()
        if (updatedList.isNotEmpty()) {
            for (updatedValue in updatedList) {
                dbCoinTrend.add(updatedValue)
            }
        }
        dbCoinTrend.close()
    }
}