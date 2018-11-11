package guepardoapps.mycoins.extensions

import guepardoapps.mycoins.annotations.JsonKey
import guepardoapps.mycoins.models.CoinConversion
import kotlin.reflect.full.declaredMemberProperties

internal fun CoinConversion.getJsonKey(): JsonKey {
    return this::class.annotations.find { it is JsonKey } as JsonKey
}

internal fun CoinConversion.getPropertyJsonKey(propertyName: String): JsonKey {
    return this::class.declaredMemberProperties.find { it.name == propertyName }?.annotations?.find { it is JsonKey } as JsonKey
}