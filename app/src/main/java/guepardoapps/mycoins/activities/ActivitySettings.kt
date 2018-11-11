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
import guepardoapps.mycoins.R
import guepardoapps.mycoins.common.Constants
import guepardoapps.mycoins.controller.SharedPreferenceController
import guepardoapps.mycoins.enums.Currency
import kotlinx.android.synthetic.main.side_settings.*

@SuppressLint("SetTextI18n")
class ActivitySettings : Activity() {

    private lateinit var sharedPreferenceController: SharedPreferenceController

    private lateinit var saveButton: Button

    @ExperimentalUnsignedTypes
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.side_settings)

        sharedPreferenceController = SharedPreferenceController(this)

        saveButton = findViewById(R.id.save_settings_edit_button)
        saveButton.isEnabled = false

        val savedCurrency = sharedPreferenceController.load(Constants.currency, Constants.currencyDefault)

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                saveButton.isEnabled = savedCurrency != editable.toString()
            }

            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
            }
        }

        settings_currency_edit_textview.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, Currency.values().map { x -> x.text }))
        settings_currency_edit_textview.addTextChangedListener(textWatcher)
        settings_currency_edit_textview.setText(savedCurrency)

        saveButton.setOnClickListener {
            settings_currency_edit_textview.error = null
            var cancel = false
            var focusView: View? = null

            val currencyTypeString = settings_currency_edit_textview.text.toString()
            if (TextUtils.isEmpty(currencyTypeString) || !Currency.values().any { x -> x.text == currencyTypeString }) {
                settings_currency_edit_textview.error = createErrorText(getString(R.string.error_field_required))
                focusView = settings_currency_edit_textview
                cancel = true
            }

            if (cancel) {
                settings_currency_edit_textview.setText("")
                focusView?.requestFocus()
            } else {
                sharedPreferenceController.save(Constants.currency, currencyTypeString)
                finish()
            }
        }
    }

    private fun createErrorText(errorString: String): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder(errorString)
        spannableStringBuilder.setSpan(ForegroundColorSpan(Color.RED), 0, errorString.length, 0)
        return spannableStringBuilder
    }
}