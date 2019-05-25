package guepardoapps.mycoins.models

import guepardoapps.mycoins.annotations.JsonKey
import guepardoapps.mycoins.enums.Currency

@JsonKey("", "Data")
internal class CoinTrend {
    private val tag: String = CoinTrend::class.java.simpleName

    @JsonKey("Data", "")
    var id: Int = 0

    @JsonKey("", "time")
    var time: Long = 0L

    @JsonKey("", "open")
    var openValue: Double = 0.0

    @JsonKey("", "close")
    var closeValue: Double = 0.0

    @JsonKey("", "low")
    var lowValue: Double = 0.0

    @JsonKey("", "high")
    var highValue: Double = 0.0

    var coinType: CoinType = CoinTypes.Null
    var currency: Currency = Currency.EUR

    override fun toString(): String = "{Class: $tag, Id: $id, Time: $time, OpenValue: $openValue, CloseValue: $closeValue, LowValue: $lowValue, HighValue: $highValue, CoinType: $coinType, Currency: $currency}"
}