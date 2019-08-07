package guepardoapps.mycoins.models

internal data class Coin(val id: String, val coinType: CoinType, var amount: Double, var additionalInformation: String)