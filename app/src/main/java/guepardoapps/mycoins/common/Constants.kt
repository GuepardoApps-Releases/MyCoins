package guepardoapps.mycoins.common

import guepardoapps.mycoins.enums.CoinType
import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.extensions.combinedString

internal class Constants {
    companion object {
        const val sharedPrefName = "GuepardoMyCoinsSharedPref"
        const val sharedPrefKey = "I39d-s2e"

        const val currency = "currency"
        val currencyDefault = Currency.EUR.id
        const val reloadEnabled = "reloadEnabled"
        const val reloadEnabledDefault = true
        const val reloadTimeoutMs = "reloadTimeoutMs"
        const val reloadTimeoutMsDefault = 30 * 60 * 1000

        const val bundleDataId = "BundleDataId"

        fun apiCoinConversion(): String {
            return "https://min-api.cryptocompare.com/data/pricemulti?fsyms=${CoinType.values().combinedString()}&tsyms=${Currency.values().combinedString()}"
        }

        fun apiCoinTrend(coinType: CoinType, currency: Currency, limit: Int): String {
            return "https://min-api.cryptocompare.com/data/histohour?fsym=${coinType.type}&tsym=${currency.text}&limit=$limit&aggregate=3&e=CCCAGG"
        }
    }
}