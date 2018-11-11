package guepardoapps.mycoins.models

internal data class Coin(val id: Int, val coinType: CoinType, var amount: Double, var additionalInformation: String)