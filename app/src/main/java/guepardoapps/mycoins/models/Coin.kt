package guepardoapps.mycoins.models

import guepardoapps.mycoins.enums.CoinType

internal data class Coin(val id: Int, val coinType: CoinType, var amount: Double)