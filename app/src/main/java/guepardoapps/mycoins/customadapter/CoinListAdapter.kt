package guepardoapps.mycoins.customadapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.afollestad.materialdialogs.MaterialDialog
import guepardoapps.mycoins.R
import guepardoapps.mycoins.activities.ActivityEdit
import guepardoapps.mycoins.common.Constants
import guepardoapps.mycoins.controller.NavigationController
import guepardoapps.mycoins.controller.SharedPreferenceController
import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.extensions.getSortFieldMethod
import guepardoapps.mycoins.extensions.getTrend
import guepardoapps.mycoins.models.Coin
import guepardoapps.mycoins.models.CoinAdapterHolder
import guepardoapps.mycoins.services.coin.CoinService
import kotlin.reflect.KCallable

@ExperimentalUnsignedTypes
internal class CoinListAdapter(private val context: Context, coinList: MutableList<Coin>, sortField: String, sortDirectionIsAsc: Boolean) : BaseAdapter() {

    private var coinAdapterList: List<CoinAdapterHolder> = listOf()

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private val navigationController: NavigationController = NavigationController(context)

    init {
        updateDataSet(coinList, sortField, sortDirectionIsAsc)
    }

    fun updateDataSet(coinList: MutableList<Coin>, sortField: String, sortDirectionIsAsc: Boolean) {
        coinAdapterList = coinList.map { coin ->
            val currencyString = SharedPreferenceController(context).load(Constants.currency, Constants.currencyDefault)
            val currencyId = Currency.values().first { x -> x.text == currencyString }.id
            val currency = Currency.values()[currencyId]
            val coinConversion = CoinService.instance.getCoinConversion(coin.coinType)
            return@map CoinAdapterHolder(coin, currency, coinConversion)
        }

        coinAdapterList = if (sortDirectionIsAsc) {
            coinAdapterList.sortedWith(compareBy { coinAdapterHolder -> useCompareField(coinAdapterHolder, sortField) })
        } else {
            coinAdapterList.sortedWith(compareByDescending { coinAdapterHolder -> useCompareField(coinAdapterHolder, sortField) })
        }

        notifyDataSetChanged()
    }

    override fun getItem(position: Int): CoinAdapterHolder = coinAdapterList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = coinAdapterList.size

    @SuppressLint("SetTextI18n", "ViewHolder", "InflateParams")
    override fun getView(index: Int, convertView: View?, parentView: ViewGroup?): View {
        val rowView: View = inflater.inflate(R.layout.list_item, null)

        coinAdapterList[index].apply {
            coinImage = rowView.findViewById(R.id.coin_item_image)
            type = rowView.findViewById(R.id.coin_item_type)
            amount = rowView.findViewById(R.id.coin_item_amount)
            currencyImage = rowView.findViewById(R.id.coin_item_currency_image)
            currencyValue = rowView.findViewById(R.id.coin_item_currency_value)
            totalValue = rowView.findViewById(R.id.coin_item_total_value)
            trend = rowView.findViewById(R.id.coin_item_trend_icon)
            additionalInformation = rowView.findViewById(R.id.coin_item_additionalInformation)

            reload = rowView.findViewById(R.id.btnReload)
            edit = rowView.findViewById(R.id.btnEdit)
            delete = rowView.findViewById(R.id.btnDelete)

            coinImage.setImageResource(coin.coinType.iconId)

            type.text = type()
            amount.text = amountString()
            additionalInformation.text = additionalInformation()
            totalValue.text = totalValueString()
            currencyValue.text = currencyValueString()

            currencyImage.setImageResource(if (currency == Currency.EUR) R.mipmap.euro else if (currency == Currency.USD) R.mipmap.dollar else R.drawable.dummy)

            trend.setImageResource(CoinService.instance.getCoinTrend(coin.coinType).getTrend().id)

            reload.setOnClickListener {
                CoinService.instance.loadCoinConversion(coin.coinType)
                CoinService.instance.loadCoinTrend(coin.coinType)
            }

            edit.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(Constants.bundleDataId, coin.id)
                navigationController.navigateWithData(ActivityEdit::class.java, bundle, false)
            }

            delete.setOnClickListener {
                MaterialDialog(context).show {
                    title(text = context.getString(R.string.delete))
                    message(text = "${context.getString(R.string.delete)} ${coin.coinType.type}?")
                    positiveButton(text = context.getString(R.string.yes)) { CoinService.instance.deleteCoin(coin) }
                    negativeButton(text = context.getString(R.string.no))
                }
            }
        }

        return rowView
    }

    @Suppress("UNCHECKED_CAST")
    private fun useCompareField(coinAdapterHolder: CoinAdapterHolder, sortField: String): Comparable<*> {
        val kPair = coinAdapterHolder.getSortFieldMethod(sortField)
        return if (kPair.first != null) {
            when (kPair.second) {
                Double::class -> (kPair.first as KCallable<Double>).call(coinAdapterHolder)
                else -> (kPair.first as KCallable<String>).call(coinAdapterHolder)
            }
        } else {
            coinAdapterHolder.type()
        }
    }
}