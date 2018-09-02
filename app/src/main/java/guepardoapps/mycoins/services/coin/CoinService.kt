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
import guepardoapps.mycoins.extensions.missingInNew
import guepardoapps.mycoins.models.Coin
import guepardoapps.mycoins.models.CoinConversion
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

        this.context = context

        apiService.setOnApiServiceListener(object : OnApiServiceListener {
            override fun onFinished(downloadType: DownloadType, coinType: CoinType, jsonString: String, success: Boolean) {
                Logger.instance.verbose(tag, "Received onDownloadListener onFinished")

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
    }

    override fun getCoinList(): MutableList<Coin> {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return mutableListOf()
        }

        val dbCoin = DbCoin(context!!)
        val list = dbCoin.get()
        dbCoin.close()

        return list
    }

    override fun addCoin(coin: Coin) {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return
        }

        val dbCoin = DbCoin(context!!)
        dbCoin.add(coin)
        dbCoin.close()
    }

    override fun updateCoin(coin: Coin) {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return
        }

        val dbCoin = DbCoin(context!!)
        dbCoin.update(coin)
        dbCoin.close()
    }

    override fun deleteCoin(coin: Coin) {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return
        }

        val dbCoin = DbCoin(context!!)
        dbCoin.delete(coin)
        dbCoin.close()
    }

    override fun loadCoinConversion() {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return
        }

        apiService.load(DownloadType.Trend, CoinType.Null, Constants.apiCoinConversion())
    }

    override fun getCoinConversion(coinType: CoinType): CoinConversion? {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return null
        }

        val dbCoinConversion = DbCoinConversion(context!!)
        val list = dbCoinConversion.findByCoinType(coinType).firstOrNull()
        dbCoinConversion.close()

        return list
    }

    override fun loadCoinTrend() {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return
        }

        val currencyId = SharedPreferenceController(context!!).load(Constants.currency, Constants.currencyDefault) as Int
        val currency = Currency.values()[currencyId]

        val coinTypeList = CoinType.values()
        for (coinType in coinTypeList) {
            apiService.load(DownloadType.Trend, coinType, Constants.apiCoinTrend(coinType, currency, 100))
        }
    }

    private fun handleDownloadConversion(jsonString: String, success: Boolean) {
        if (!success || jsonString.isEmpty()) {
            Logger.instance.error(tag, "handleDownloadConversion failed")
            return
        }

        val dbCoinConversion = DbCoinConversion(context!!)
        val oldList = dbCoinConversion.get()
        val updatedList = jsonDataToCoinConversionConverter.convertResponseToList(jsonString, oldList)
        if (updatedList.isNotEmpty()) {
            val missingList = oldList.missingInNew(updatedList)
            for (missingValue in missingList) {
                dbCoinConversion.delete(missingValue)
            }

            for (updatedValue in updatedList) {
                dbCoinConversion.update(updatedValue)
            }
        }
        dbCoinConversion.close()
    }

    private fun handleDownloadTrend(jsonString: String, success: Boolean, coinType: CoinType) {
        if (!success || jsonString.isEmpty()) {
            Logger.instance.error(tag, "handleDownloadTrend failed")
            return
        }

        val currencyId = SharedPreferenceController(context!!).load(Constants.currency, Constants.currencyDefault) as Int
        val currency = Currency.values()[currencyId]

        val dbCoinTrend = DbCoinTrend(context!!)
        val oldList = dbCoinTrend.get()
        val updatedList = jsonDataToCoinTrendConverter.convertResponseToList(jsonString, coinType, currency)
        if (updatedList.isNotEmpty()) {
            val missingList = oldList.missingInNew(updatedList)
            for (missingValue in missingList) {
                dbCoinTrend.delete(missingValue)
            }

            for (updatedValue in updatedList) {
                dbCoinTrend.update(updatedValue)
            }
        }
        dbCoinTrend.close()
    }
}