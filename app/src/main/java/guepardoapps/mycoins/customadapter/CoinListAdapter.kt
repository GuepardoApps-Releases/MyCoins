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

    private val navigationController: NavigationController = NavigationController(context)
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var coinAdapterList: List<CoinAdapterHolder> = listOf()

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

    override fun getItem(position: Int): Any {
        return coinAdapterList[position]
    }

    override fun getItemId(position: Int): Long {
        return coinAdapterList[position].coin.id.toLong()
    }

    override fun getCount(): Int {
        return coinAdapterList.size
    }

    @SuppressLint("SetTextI18n", "ViewHolder", "InflateParams")
    override fun getView(index: Int, convertView: View?, parentView: ViewGroup?): View {
        val rowView: View = inflater.inflate(R.layout.list_item, null)

        val holder = coinAdapterList[index]
        holder.coinImage = rowView.findViewById(R.id.coin_item_image)
        holder.type = rowView.findViewById(R.id.coin_item_type)
        holder.amount = rowView.findViewById(R.id.coin_item_amount)
        holder.currencyImage = rowView.findViewById(R.id.coin_item_currency_image)
        holder.currencyValue = rowView.findViewById(R.id.coin_item_currency_value)
        holder.totalValue = rowView.findViewById(R.id.coin_item_total_value)
        holder.trend = rowView.findViewById(R.id.coin_item_trend_icon)
        holder.additionalInformation = rowView.findViewById(R.id.coin_item_additionalInformation)

        holder.reload = rowView.findViewById(R.id.btnReload)
        holder.edit = rowView.findViewById(R.id.btnEdit)
        holder.delete = rowView.findViewById(R.id.btnDelete)

        holder.coinImage.setImageResource(holder.coin.coinType.iconId)

        holder.type.text = holder.type()
        holder.amount.text = holder.amountString()
        holder.additionalInformation.text = holder.additionalInformation()
        holder.totalValue.text = holder.totalValueString()
        holder.currencyValue.text = holder.currencyValueString()

        when (holder.currency) {
            Currency.EUR -> holder.currencyImage.setImageResource(R.mipmap.euro)
            Currency.USD -> holder.currencyImage.setImageResource(R.mipmap.dollar)
        }

        holder.trend.setImageResource(CoinService.instance.getCoinTrend(holder.coin.coinType).getTrend().id)

        holder.reload.setOnClickListener {
            CoinService.instance.loadCoinConversion(holder.coin.coinType)
            CoinService.instance.loadCoinTrend(holder.coin.coinType)
        }

        holder.edit.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.bundleDataId, holder.coin.id)
            navigationController.navigateWithData(ActivityEdit::class.java, bundle, false)
        }

        holder.delete.setOnClickListener {
            MaterialDialog(context).show {
                title(text = "Delete")
                message(text = "Delete ${holder.coin.coinType.type}?")
                positiveButton(text = "Yes") { CoinService.instance.deleteCoin(holder.coin) }
                negativeButton(text = "No")
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