package guepardoapps.mycoins.activities

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import guepardoapps.mycoins.R
import guepardoapps.mycoins.common.Constants
import guepardoapps.mycoins.controller.NavigationController
import guepardoapps.mycoins.controller.SharedPreferenceController
import guepardoapps.mycoins.database.coin.DbCoinConversion
import guepardoapps.mycoins.database.coin.DbCoinTrend
import guepardoapps.mycoins.enums.CoinType
import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.models.CoinConversion
import guepardoapps.mycoins.models.CoinTrend
import guepardoapps.mycoins.services.coin.CoinService

class ActivityBoot : Activity() {

    private val delayInMillis: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.side_boot)

        val sharedPreferenceController = SharedPreferenceController(this)

        if (!(sharedPreferenceController.load(Constants.sharedPrefName, false) as Boolean)) {
            sharedPreferenceController.save(Constants.currency, Constants.currencyDefault)
            sharedPreferenceController.save(Constants.reloadEnabled, Constants.reloadEnabledDefault)
            sharedPreferenceController.save(Constants.reloadTimeoutMs, Constants.reloadTimeoutMsDefault)
            sharedPreferenceController.save(Constants.sharedPrefName, true)

            val dbCoinTrend = DbCoinTrend(this)
            val dbCoinConversion = DbCoinConversion(this)
            CoinType.values().forEach { coinType ->
                val coinTrend = CoinTrend()
                coinTrend.id = coinType.id
                coinTrend.coinType = coinType
                coinTrend.time = System.currentTimeMillis()
                coinTrend.openValue = 0.0
                coinTrend.closeValue = 0.0
                coinTrend.lowValue = 0.0
                coinTrend.highValue = 0.0
                coinTrend.currency = Currency.values()[Constants.currencyDefault]
                dbCoinTrend.add(coinTrend)

                val coinConversion = CoinConversion()
                coinConversion.id = coinType.id
                coinConversion.coinType = coinType
                coinConversion.eurValue = 0.0
                coinConversion.usDollarValue = 0.0
                dbCoinConversion.add(coinConversion)
            }
        }

        CoinService.instance.loadCoinConversion()

        Handler().postDelayed({ NavigationController(this).navigate(ActivityMain::class.java, true) }, delayInMillis)
    }
}