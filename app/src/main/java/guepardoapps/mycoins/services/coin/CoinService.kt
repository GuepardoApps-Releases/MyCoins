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
import guepardoapps.mycoins.models.CoinType
import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.enums.DownloadType
import guepardoapps.mycoins.models.Coin
import guepardoapps.mycoins.models.CoinConversion
import guepardoapps.mycoins.models.CoinTrend
import guepardoapps.mycoins.services.api.ApiService
import guepardoapps.mycoins.services.api.OnApiServiceListener
import guepardoapps.mycoins.tasks.DbSaveTask
import guepardoapps.mycoins.utils.Logger
import io.reactivex.subjects.PublishSubject

@SuppressLint("StaticFieldLeak")
internal class CoinService private constructor() : ICoinService {
    private val tag: String = CoinService::class.java.simpleName

    private var apiService = ApiService()

    private var context: Context? = null

    private var initialLoadCoinConversions: MutableMap<String, Boolean> = hashMapOf()

    private var initialLoadCoinTrends: MutableMap<String, Boolean> = hashMapOf()

    val initialSetupPublishSubject = PublishSubject.create<Boolean>()

    var isInitialized: Boolean = false

    private object Holder {
        val instance: CoinService = CoinService()
    }

    companion object {
        val instance: CoinService by lazy { Holder.instance }
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

        loadCoinConversion(coin.coinType)
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
        dbCoinTrend.clear(coin.coinType)
        dbCoinTrend.close()

        val dbCoinConversion = DbCoinConversion(context!!)
        dbCoinConversion.clear(coin.coinType)
        dbCoinConversion.close()
    }

    override fun getCoinById(id: String): Coin? {
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

    override fun getCoinTrend(coinType: CoinType): MutableList<CoinTrend> {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return mutableListOf()
        }

        Logger.instance.verbose(tag, "getCoinTrend for $coinType")

        val dbCoinTrend = DbCoinTrend(context!!)
        val coinTrend = dbCoinTrend.findByCoinType(coinType)
        dbCoinTrend.close()

        Logger.instance.verbose(tag, "coinTrend $coinTrend")

        return coinTrend
    }

    override fun initialize(context: Context) {
        Logger.instance.verbose(tag, "initialize")

        if (this.context != null) {
            return
        }
        this.context = context

        apiService.setOnApiServiceListener(object : OnApiServiceListener {
            override fun onFinished(downloadType: DownloadType, coinType: CoinType, jsonString: String, success: Boolean) {
                Logger.instance.verbose(tag, "Received onDownloadListener onFinished: $downloadType, $coinType, $jsonString, $success")

                when (downloadType) {
                    DownloadType.Conversion -> {
                        initialLoadCoinConversions[coinType.type] = true
                        isInitialized = initialLoadCoinConversions.all { pair -> pair.value } && initialLoadCoinTrends.all { pair -> pair.value }
                        initialSetupPublishSubject.onNext(isInitialized)
                        handleDownloadConversion(jsonString, success, coinType)
                    }
                    DownloadType.Trend -> {
                        initialLoadCoinTrends[coinType.type] = true
                        isInitialized = initialLoadCoinConversions.all { pair -> pair.value } && initialLoadCoinTrends.all { pair -> pair.value }
                        initialSetupPublishSubject.onNext(isInitialized)
                        handleDownloadTrend(jsonString, success, coinType)
                    }
                    DownloadType.Null -> {
                        Logger.instance.error(tag, "Received download update with downloadType Null and jsonString: $jsonString")
                    }
                }
            }
        })

        val coinList = getCoinList()
        if (coinList.isEmpty()) {
            isInitialized = true
            initialSetupPublishSubject.onNext(isInitialized)
        } else {
            coinList.forEach { coin ->
                initialLoadCoinConversions.plusAssign(coin.coinType.type to false)
                initialLoadCoinTrends.plusAssign(coin.coinType.type to false)
            }
            coinList.forEach { coin ->
                loadCoinConversion(coin.coinType)
                loadCoinTrend(coin.coinType)
            }
        }
    }

    override fun loadCoinConversion(coinType: CoinType) {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return
        }

        Logger.instance.verbose(tag, "loadCoinConversion")
        apiService.load(DownloadType.Conversion, coinType, Constants.apiCoinConversion(arrayOf(coinType)))
    }

    override fun loadCoinTrend(coinType: CoinType) {
        if (this.context == null) {
            Logger.instance.warning(tag, "not initialized!")
            return
        }

        Logger.instance.verbose(tag, "loadCoinTrend for $coinType")

        val currencyString = SharedPreferenceController(context!!).load(Constants.currency, Constants.currencyDefault)
        val currencyId = Currency.values().first { x -> x.text == currencyString }.id
        val currency = Currency.values()[currencyId]

        apiService.load(DownloadType.Trend, coinType, Constants.apiCoinTrend(coinType, currency, 100))
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

        loadCoinConversion(coin.coinType)
        loadCoinTrend(coin.coinType)
    }

    private fun handleDownloadConversion(jsonString: String, success: Boolean, coinType: CoinType) {
        if (!success || jsonString.isEmpty()) {
            Logger.instance.error(tag, "handleDownloadConversion failed")
            return
        }

        Logger.instance.verbose(tag, "handleDownloadConversion with $jsonString has success $success")

        val coinConversion = JsonDataToCoinConversionConverter().convertResponse(jsonString, coinType)
        if (coinConversion != null) {
            val task = DbSaveTask()
            task.method = {
                val dbCoinConversion = DbCoinConversion(context!!)
                dbCoinConversion.clear(coinType)
                dbCoinConversion.add(coinConversion)
                dbCoinConversion.close()
            }
            task.execute()
            return
        }
    }

    private fun handleDownloadTrend(jsonString: String, success: Boolean, coinType: CoinType) {
        if (!success || jsonString.isEmpty()) {
            Logger.instance.error(tag, "handleDownloadTrend failed")
            return
        }

        Logger.instance.verbose(tag, "handleDownloadTrend with $jsonString has success $success for coinType $coinType")

        val currencyString = SharedPreferenceController(context!!).load(Constants.currency, Constants.currencyDefault)
        val currencyId = Currency.values().first { x -> x.text == currencyString }.id
        val currency = Currency.values()[currencyId]

        val updatedList = JsonDataToCoinTrendConverter().convertResponseToList(jsonString, coinType, currency)
        if (updatedList.isNotEmpty()) {
            val task = DbSaveTask()
            task.method = {
                val dbCoinTrend = DbCoinTrend(context!!)
                dbCoinTrend.clear(coinType)
                for ((index, updatedValue) in updatedList.withIndex()) {
                    dbCoinTrend.add(updatedValue, index.toFloat(), updatedList.size.toFloat())
                }
                dbCoinTrend.close()
            }
            task.execute()
            return
        }
    }
}