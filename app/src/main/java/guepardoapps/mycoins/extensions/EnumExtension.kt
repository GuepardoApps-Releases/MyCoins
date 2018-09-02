package guepardoapps.mycoins.extensions

import guepardoapps.mycoins.enums.CoinType
import guepardoapps.mycoins.enums.Currency

internal fun Array<CoinType>.byString(string: String): CoinType {
    for (value in this) {
        if (value.type == string) {
            return value
        }
    }
    return CoinType.Null
}

internal fun Array<CoinType>.combinedString(): String {
    var combinedString = ""
    this.forEach { coinType ->
        combinedString += coinType.type + ","
    }
    return combinedString.substring(1, combinedString.length - 1)
}

internal fun Array<Currency>.combinedString(): String {
    var combinedString = ""
    this.forEach { coinType ->
        combinedString += coinType.text + ","
    }
    return combinedString.substring(1, combinedString.length - 1)
}