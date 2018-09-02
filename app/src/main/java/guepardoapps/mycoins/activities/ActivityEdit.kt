package guepardoapps.mycoins.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import com.evernote.android.state.State
import com.evernote.android.state.StateSaver
import guepardoapps.mycoins.R
import guepardoapps.mycoins.common.Constants
import guepardoapps.mycoins.enums.CoinType
import guepardoapps.mycoins.extensions.byString
import guepardoapps.mycoins.models.Coin
import guepardoapps.mycoins.services.coin.CoinService
import kotlinx.android.synthetic.main.side_edit.*

@SuppressLint("SetTextI18n")
class ActivityEdit : Activity() {

    private lateinit var saveButton: Button

    @State
    private var coin: Coin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StateSaver.restoreInstanceState(this, savedInstanceState)

        setContentView(R.layout.side_edit)

        saveButton = findViewById(R.id.save_coin_edit_button)
        saveButton.isEnabled = false

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                saveButton.isEnabled = true
            }

            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
            }
        }

        coin_type_edit_textview.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, CoinType.values().map { x -> x.type }))
        coin_type_edit_textview.addTextChangedListener(textWatcher)
        coin_amount_edit_textview.addTextChangedListener(textWatcher)

        val data = intent.extras
        if (data != null) {
            val id = data.getInt(Constants.bundleDataId)
            coin = CoinService.instance.getCoinById(id)
            if (coin != null) {
                coin_type_edit_textview.setText(coin?.coinType?.type)
                coin_amount_edit_textview.setText(coin?.amount?.toString())
            }
        }

        saveButton.setOnClickListener {
            coin_type_edit_textview.error = null
            coin_amount_edit_textview.error = null
            var cancel = false
            var focusView: View? = null

            val coinTypeString = coin_type_edit_textview.text.toString()
            if (TextUtils.isEmpty(coinTypeString)) {
                coin_type_edit_textview.error = createErrorText(getString(R.string.error_field_required))
                focusView = coin_type_edit_textview
                cancel = true
            }

            val amountString = coin_amount_edit_textview.text.toString()
            if (TextUtils.isEmpty(amountString)) {
                coin_amount_edit_textview.error = createErrorText(getString(R.string.error_field_required))
                focusView = coin_amount_edit_textview
                cancel = true
            }

            if (cancel) {
                focusView?.requestFocus()
            } else {
                val coinType = CoinType.values().byString(coinTypeString)
                val amount = amountString.toDouble()

                if (coin != null) {
                    CoinService.instance.updateCoin(Coin(coin?.id!!, coinType, amount))
                } else {
                    CoinService.instance.addCoin(Coin(0, coinType, amount))
                }

                finish()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        StateSaver.saveInstanceState(this, outState)
    }

    private fun createErrorText(errorString: String): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder(errorString)
        spannableStringBuilder.setSpan(ForegroundColorSpan(Color.RED), 0, errorString.length, 0)
        return spannableStringBuilder
    }
}