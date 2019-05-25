package guepardoapps.mycoins.extensions

import guepardoapps.mycoins.annotations.JsonKey
import guepardoapps.mycoins.models.CoinConversion
import kotlin.reflect.full.declaredMemberProperties

internal fun CoinConversion.getPropertyJsonKey(propertyName: String): JsonKey = this::class.declaredMemberProperties.find { it.name == propertyName }?.annotations?.find { it is JsonKey } as JsonKey