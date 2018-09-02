package guepardoapps.mycoins.extensions

import guepardoapps.mycoins.annotations.JsonKey
import guepardoapps.mycoins.enums.Trend
import guepardoapps.mycoins.models.CoinTrend
import kotlin.reflect.full.declaredMemberProperties

internal fun CoinTrend.getJsonKey(): JsonKey {
    return this::class.annotations.find { it is JsonKey } as JsonKey
}

internal fun CoinTrend.getPropertyJsonKey(propertyName: String): JsonKey {
    return this::class.declaredMemberProperties.find { it.name == propertyName }?.annotations?.find { it is JsonKey } as JsonKey
}

internal fun ArrayList<CoinTrend>.getTrend(): Trend {
    if (this.isEmpty() || this.size == 1) {
        return Trend.Null
    }

    val first = this[0]
    val last = this.last()

    val difference = last.closeValue - first.openValue
    return when {
        difference > 0 -> Trend.Rise
        difference < 0 -> Trend.Fall
        else -> Trend.Null
    }
}

internal fun MutableList<CoinTrend>.missingInNew(newList: MutableList<CoinTrend>): MutableList<CoinTrend> {
    val missingList = mutableListOf<CoinTrend>()

    for (value in this) {
        val found = newList.find { x -> x.coinType.id == value.coinType.id }
        if (found == null) {
            missingList.add(value)
        }
    }

    return missingList
}