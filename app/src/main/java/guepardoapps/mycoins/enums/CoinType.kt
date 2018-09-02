package guepardoapps.mycoins.enums

import guepardoapps.mycoins.R

internal enum class CoinType(val id: Int, val iconId: Int, val type: String) {
    Null(0, -1, ""),
    Bch(1, R.drawable.bch, "BCH"),
    Btc(2, R.drawable.btc, "BTC"),
    Dash(3, R.drawable.dash, "DASH"),
    Etc(4, R.drawable.etc, "ETC"),
    Eth(5, R.drawable.eth, "ETH"),
    Ltc(6, R.drawable.ltc, "LTC"),
    Xmr(7, R.drawable.xmr, "XMR"),
    Zec(8, R.drawable.zec, "ZEC"),
}