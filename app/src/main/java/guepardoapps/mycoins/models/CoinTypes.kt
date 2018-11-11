package guepardoapps.mycoins.models

/**
 * Icons found at https://www.iconfinder.com/
 */

import guepardoapps.mycoins.R

internal class CoinTypes {
    companion object {
        val Null = CoinType(R.drawable.dummy, "")

        val Bch = CoinType(R.drawable.bch, "BCH")
        val Btc = CoinType(R.drawable.btc, "BTC")
        val Dash = CoinType(R.drawable.dash, "DASH")
        val Etc = CoinType(R.drawable.etc, "ETC")
        val Eth = CoinType(R.drawable.eth, "ETH")
        val Ltc = CoinType(R.drawable.ltc, "LTC")
        val Xmr = CoinType(R.drawable.xmr, "XMR")
        val Zec = CoinType(R.drawable.zec, "ZEC")

        val Ada = CoinType(R.drawable.ada, "ADA")
        val Bcpt = CoinType(R.drawable.bcpt, "BCPT")
        val Bnb = CoinType(R.drawable.bnb, "BNB")
        val Btcz = CoinType(R.drawable.btcz, "BTCZ")
        val Doge = CoinType(R.drawable.doge, "DOGE")
        val Eos = CoinType(R.drawable.eos, "EOS")
        val Grlc = CoinType(R.drawable.grlc, "GRLC")
        val Iota = CoinType(R.drawable.iota, "IOTA")
        val Nem = CoinType(R.drawable.nem, "XEM")
        val Neo = CoinType(R.drawable.neo, "NEO")
        val Safex = CoinType(R.drawable.safex, "SAFEX")
        val Trx = CoinType(R.drawable.trx, "TRX")
        val Vet = CoinType(R.drawable.vet, "VET")
        val Xlm = CoinType(R.drawable.xlm, "XLM")
        val Usdt = CoinType(R.drawable.usdt, "USDT")
        val Xrp = CoinType(R.drawable.xrp, "XRP")
        val Xtz = CoinType(R.drawable.xtz, "XTZ")
        val Xvg = CoinType(R.drawable.xvg, "XVG")

        val values: Array<CoinType> = arrayOf(
                Null,
                Bch, Btc, Dash, Etc, Eth, Ltc, Xmr, Zec,
                Ada, Bcpt, Bnb, Btcz, Doge, Eos, Grlc, Iota, Nem, Neo, Safex, Trx, Vet, Xlm, Usdt, Xrp, Xtz, Xvg
        )
    }
}