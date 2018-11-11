package guepardoapps.mycoins.extensions

import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.models.Coin
import guepardoapps.mycoins.models.CoinConversion
import guepardoapps.mycoins.utils.Logger

internal fun Coin.value(coinConversion: CoinConversion, currency: Currency): Double {
    if (this.coinType.type != coinConversion.coinType.type) {
        Logger.instance.error("CoinExtension", "Incompatible CoinTypes for coin (${this.coinType.type}) and conversion (${coinConversion.coinType.type})!")
        return 0.0
    }

    return when (currency) {
        Currency.EUR -> this.amount * coinConversion.eurValue
        Currency.USD -> this.amount * coinConversion.usDollarValue
    }
}