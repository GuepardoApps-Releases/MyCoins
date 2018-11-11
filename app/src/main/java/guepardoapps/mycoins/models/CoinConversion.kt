package guepardoapps.mycoins.models

import guepardoapps.mycoins.annotations.JsonKey

@JsonKey("", "")
internal class CoinConversion {
    private val tag: String = CoinConversion::class.java.simpleName

    var id: Int = 0
    var coinType: CoinType = CoinTypes.Null

    @JsonKey("", "EUR")
    var eurValue: Double = 0.0

    @JsonKey("", "USD")
    var usDollarValue: Double = 0.0

    override fun toString(): String {
        return "{Class: $tag, Id: $id, CoinType: $coinType, EurValue: $eurValue, UsDollarValue: $usDollarValue}"
    }
}