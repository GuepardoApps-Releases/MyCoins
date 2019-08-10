package guepardoapps.mycoins.models

import android.widget.ImageView
import android.widget.TextView
import com.rey.material.widget.FloatingActionButton
import guepardoapps.mycoins.annotations.SortField
import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.extensions.doubleFormat
import guepardoapps.mycoins.extensions.value

@ExperimentalUnsignedTypes
internal class CoinAdapterHolder(var coin: Coin, var currency: Currency, private var coinConversion: CoinConversion?) {

    lateinit var coinImage: ImageView

    lateinit var type: TextView

    lateinit var amount: TextView

    lateinit var additionalInformation: TextView

    lateinit var currencyImage: ImageView

    lateinit var currencyValue: TextView

    lateinit var totalValue: TextView

    lateinit var trend: ImageView

    lateinit var reload: FloatingActionButton

    lateinit var edit: FloatingActionButton

    lateinit var delete: FloatingActionButton

    @SortField<String>("Name", 1u, String::class)
    fun type(): String = coin.coinType.type

    @SortField<Double>("Amount", 2u, Double::class)
    fun amount(): Double = coin.amount

    fun amountString(): String = coin.amount.toString()

    @SortField<Double>("Single Value", 3u, Double::class)
    fun currencyValue(): Double = if (coinConversion == null) 0.0 else when (currency) {
        Currency.EUR -> coinConversion!!.eurValue
        Currency.USD -> coinConversion!!.usDollarValue
    }

    fun currencyValueString(): String = if (coinConversion == null) "-" else when (currency) {
        Currency.EUR -> "${coinConversion!!.eurValue.doubleFormat(2)} €"
        Currency.USD -> "${coinConversion!!.usDollarValue.doubleFormat(2)} $"
    }

    @SortField<Double>("Total Value", 4u, Double::class)
    fun totalValue(): Double = coin.value(coinConversion!!, currency)

    fun totalValueString(): String = if (coinConversion == null) "-" else when (currency) {
        Currency.EUR -> "${(coinConversion!!.eurValue * coin.amount).doubleFormat(2)} €"
        Currency.USD -> "${(coinConversion!!.usDollarValue * coin.amount).doubleFormat(2)} $"
    }

    @SortField<String>("Text", 5u, String::class)
    fun additionalInformation(): String = coin.additionalInformation
}