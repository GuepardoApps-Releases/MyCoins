package guepardoapps.mycoins.common

import guepardoapps.mycoins.models.CoinType
import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.extensions.combinedString
import guepardoapps.mycoins.models.CoinTypes

internal class Constants {
    companion object {
        const val sharedPrefName = "GuepardoMyCoinsSharedPref"
        const val sharedPrefKey = "I39d-s2e"

        const val currency = "currency"
        val currencyDefault = Currency.EUR.text
        const val reloadEnabled = "reloadEnabled"
        const val reloadEnabledDefault = true
        const val reloadTimeoutMs = "reloadTimeoutMs"
        const val reloadTimeoutMsDefault = 30 * 60 * 1000

        const val bundleDataId = "BundleDataId"

        fun apiCoinConversion(coinTypeArray: Array<CoinType>): String {
            return "https://min-api.cryptocompare.com/data/pricemulti?fsyms=${(if (coinTypeArray.any()) coinTypeArray else CoinTypes.values).combinedString()}&tsyms=${Currency.values().combinedString()}"
        }

        fun apiCoinTrend(coinType: CoinType, currency: Currency, limit: Int): String {
            return "https://min-api.cryptocompare.com/data/histohour?fsym=${coinType.type}&tsym=${currency.text}&limit=$limit&aggregate=3&e=CCCAGG"
        }
    }
}