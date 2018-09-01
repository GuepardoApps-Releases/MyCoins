package guepardoapps.mycoins.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.util.Locale;

import de.mateware.snacky.Snacky;

import guepardoapps.mycoins.services.CoinService;
import guepardoapps.mycoins.R;
import guepardoapps.mycoins.models.CoinDto;
import guepardoapps.mycoins.controller.ReceiverController;

public class ActivityCoinEdit extends AppCompatActivity {
    private static final String TAG = ActivityCoinEdit.class.getSimpleName();
    private Logger _logger;

    private boolean _propertyChanged;
    private CoinDto _coinDto;

    private CoinService _coinService;
    private ReceiverController _receiverController;

    private com.rey.material.widget.Button _saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_edit);

        _logger = new Logger(TAG);
        _logger.Debug("onCreate");

        _coinDto = (CoinDto) getIntent().getSerializableExtra(CoinService.CoinIntent);

        _coinService = CoinService.getInstance();

        _receiverController = new ReceiverController(this);

        final AutoCompleteTextView coinEditTypeTextView = (AutoCompleteTextView) findViewById(R.id.coin_edit_type_textview);
        final EditText coinAmountEditText = (EditText) findViewById(R.id.coin_edit_amount_textview);

        _saveButton = (com.rey.material.widget.Button) findViewById(R.id.save_coin_edit_button);

        TextWatcher sharedTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                _propertyChanged = true;
                _saveButton.setEnabled(true);
            }
        };

        coinEditTypeTextView.addTextChangedListener(sharedTextWatcher);
        coinAmountEditText.addTextChangedListener(sharedTextWatcher);

        if (_coinDto != null) {
            coinEditTypeTextView.setText(_coinDto.GetType());
            coinAmountEditText.setText(String.valueOf(_coinDto.GetAmount()));
        } else {
            displayFailSnacky("Cannot work with data! Is corrupt! Please try again!");
        }

        _saveButton.setEnabled(false);
        _saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coinEditTypeTextView.setError(null);
                boolean cancel = false;
                View focusView = null;

                if (!_propertyChanged) {
                    coinEditTypeTextView.setError(createErrorText(getString(R.string.error_nothing_changed)));
                    focusView = coinEditTypeTextView;
                    cancel = true;
                }

                String coinType = coinEditTypeTextView.getText().toString();

                if (TextUtils.isEmpty(coinType)) {
                    coinEditTypeTextView.setError(createErrorText(getString(R.string.error_field_required)));
                    focusView = coinEditTypeTextView;
                    cancel = true;
                }

                String coinAmountString = coinAmountEditText.getText().toString();

                if (TextUtils.isEmpty(coinAmountString)) {
                    coinAmountEditText.setError(createErrorText(getString(R.string.error_field_required)));
                    focusView = coinAmountEditText;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    if (_coinDto.GetAction() == CoinDto.Action.Add) {
                        int id = CoinService.getInstance().GetDataList().size();
                        _saveButton.setEnabled(false);
                        _coinService.AddCoin(new CoinDto(id, coinType, Double.parseDouble(coinAmountString)));
                        finish();
                    } else if (_coinDto.GetAction() == CoinDto.Action.Update) {
                        _saveButton.setEnabled(false);
                        _coinService.UpdateCoin(new CoinDto(_coinDto.GetId(), coinType, Double.parseDouble(coinAmountString)));
                        finish();
                    } else {
                        coinEditTypeTextView.setError(createErrorText(String.format(Locale.getDefault(), "Invalid action %s", _coinDto.GetAction())));
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        _logger.Debug("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        _logger.Debug("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        _logger.Debug("onPause");
        _receiverController.Dispose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _logger.Debug("onDestroy");
        _receiverController.Dispose();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Build a custom error text
     */
    private SpannableStringBuilder createErrorText(@NonNull String errorString) {
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.RED);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
        spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
        return spannableStringBuilder;
    }

    private void displayFailSnacky(@NonNull String message) {
        Snacky.builder()
                .setActivty(ActivityCoinEdit.this)
                .setText(message)
                .setDuration(Snacky.LENGTH_INDEFINITE)
                .setActionText(android.R.string.ok)
                .error()
                .show();
    }
}
