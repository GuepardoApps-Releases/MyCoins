package guepardoapps.mycoins.extensions

import guepardoapps.mycoins.annotations.JsonKey
import guepardoapps.mycoins.enums.Trend
import guepardoapps.mycoins.models.CoinTrend
import guepardoapps.mycoins.utils.Logger
import kotlin.reflect.full.declaredMemberProperties

internal fun CoinTrend.getJsonKey(): JsonKey = this::class.annotations.find { it is JsonKey } as JsonKey

internal fun CoinTrend.getPropertyJsonKey(propertyName: String): JsonKey = this::class.declaredMemberProperties.find { it.name == propertyName }?.annotations?.find { it is JsonKey } as JsonKey

internal fun MutableList<CoinTrend>.getTrend(): Trend {
    if (this.isEmpty()) {
        Logger.instance.debug("getTrend", "difference isEmpty")
        return Trend.Null
    }

    val difference = this.last().closeValue - this.first().openValue
    val icon = when {
        difference > 0 -> Trend.Rise
        difference < 0 -> Trend.Fall
        else -> Trend.Null
    }

    Logger.instance.debug("getTrend", "difference is $difference and icon should be $icon")
    return icon
}