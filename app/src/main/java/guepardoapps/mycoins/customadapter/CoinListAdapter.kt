package guepardoapps.mycoins.customadapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.rey.material.widget.FloatingActionButton
import guepardoapps.mycoins.R
import guepardoapps.mycoins.activities.ActivityEdit
import guepardoapps.mycoins.common.Constants
import guepardoapps.mycoins.controller.NavigationController
import guepardoapps.mycoins.controller.SharedPreferenceController
import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.extensions.doubleFormat
import guepardoapps.mycoins.models.Coin
import guepardoapps.mycoins.services.coin.CoinService

internal class CoinListAdapter(private val context: Context, private val list: MutableList<Coin>) : BaseAdapter() {

    private val navigationController: NavigationController = NavigationController(context)
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private class Holder {
        lateinit var coinImage: ImageView

        lateinit var type: TextView
        lateinit var amount: TextView

        lateinit var currencyImage: ImageView
        lateinit var currencyValue: TextView

        lateinit var edit: FloatingActionButton
        lateinit var delete: FloatingActionButton
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return list[position].id.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n", "ViewHolder", "InflateParams")
    override fun getView(index: Int, convertView: View?, parentView: ViewGroup?): View {
        val rowView: View = inflater.inflate(R.layout.list_item, null)

        val coin = list[index]

        val holder = Holder()
        holder.coinImage = rowView.findViewById(R.id.coin_item_image)
        holder.type = rowView.findViewById(R.id.coin_item_type)
        holder.amount = rowView.findViewById(R.id.coin_item_amount)
        holder.currencyImage = rowView.findViewById(R.id.coin_item_currency_image)
        holder.currencyValue = rowView.findViewById(R.id.coin_item_currency_value)

        holder.edit = rowView.findViewById(R.id.btnEdit)
        holder.delete = rowView.findViewById(R.id.btnDelete)

        holder.coinImage.setImageResource(coin.coinType.iconId)

        holder.type.text = coin.coinType.type
        holder.amount.text = coin.amount.toString()

        val currencyId = SharedPreferenceController(context).load(Constants.currency, Constants.currencyDefault) as Int
        val currency = Currency.values()[currencyId]
        if (currency == Currency.EUR) {
            holder.currencyImage.setImageResource(R.mipmap.euro)
        }

        val coinConversion = CoinService.instance.getCoinConversion(coin.coinType)
        if (coinConversion != null) {
            if (currency == Currency.EUR) {
                holder.currencyValue.text = coinConversion.eurValue.doubleFormat(2) + " €"
            } else {
                holder.currencyValue.text = coinConversion.usDollarValue.doubleFormat(2) + " $"
            }
        } else {
            holder.currencyValue.text = "-"
        }

        holder.edit.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.bundleDataId, coin.id)
            navigationController.navigateWithData(ActivityEdit::class.java, bundle, false)
        }

        holder.delete.setOnClickListener {
            MaterialDialog(context).show {
                title(text = "Delete")
                message(text = "Delete ${coin.coinType.type}?")
                positiveButton(text = "Yes") { _ -> CoinService.instance.deleteCoin(coin) }
                negativeButton(text = "No")
            }
        }

        return rowView
    }
}