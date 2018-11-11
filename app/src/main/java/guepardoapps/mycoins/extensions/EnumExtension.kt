package guepardoapps.mycoins.extensions

import guepardoapps.mycoins.models.CoinType
import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.models.CoinTypes

internal fun Array<CoinType>.byString(string: String): CoinType {
    for (value in this) {
        if (value.type == string) {
            return value
        }
    }
    return CoinTypes.Null
}

internal fun Array<CoinType>.combinedString(): String {
    var combinedString = ""
    this.forEach { coinType ->
        combinedString += coinType.type + ","
    }
    return combinedString.substring(0, combinedString.length - 1)
}

internal fun Array<Currency>.combinedString(): String {
    var combinedString = ""
    this.forEach { coinType ->
        combinedString += coinType.text + ","
    }
    return combinedString.substring(0, combinedString.length - 1)
}