package guepardoapps.mycoins.extensions

import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.models.Coin
import guepardoapps.mycoins.models.CoinConversion
import guepardoapps.mycoins.utils.Logger

internal fun Coin.value(coinConversion: CoinConversion, currency: Currency): Double {
    if (this.coinType.id != coinConversion.coinType.id) {
        Logger.instance.error("CoinExtension", "Incompatible CoinTypes for coin (${this.coinType.id}) and conversion (${coinConversion.coinType.id})!")
        return -1.0
    }

    return when (currency) {
        Currency.EUR -> this.amount * coinConversion.eurValue
        Currency.USD -> this.amount * coinConversion.usDollarValue
        Currency.Null -> 0.0
    }
}

internal fun Coin.valueString(coinConversion: CoinConversion, currency: Currency): String {
    if (this.coinType.id != coinConversion.coinType.id) {
        Logger.instance.error("CoinExtension", "Incompatible CoinTypes for coin (${this.coinType.id}) and conversion (${coinConversion.coinType.id})!")
        return "-1.0"
    }

    return when (currency) {
        Currency.EUR -> "${(this.amount * coinConversion.eurValue).doubleFormat(2)} €"
        Currency.USD -> "${(this.amount * coinConversion.usDollarValue).doubleFormat(2)} $"
        Currency.Null -> "-"
    }
}