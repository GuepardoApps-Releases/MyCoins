package guepardoapps.mycoins.models

import com.github.guepardoapps.kulid.ULID
import guepardoapps.mycoins.annotations.JsonKey

@JsonKey("", "")
internal class CoinConversion {

    var id: String = ULID.random()

    var coinType: CoinType = CoinTypes.Null

    @JsonKey("", "EUR")
    var eurValue: Double = 0.0

    @JsonKey("", "USD")
    var usDollarValue: Double = 0.0

    override fun toString(): String = "{Class: ${CoinConversion::class.java.simpleName}, Id: $id, CoinType: $coinType, EurValue: $eurValue, UsDollarValue: $usDollarValue}"
}