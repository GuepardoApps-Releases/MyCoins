package guepardoapps.mycoins.common.controller;

import android.content.Context;
import android.support.annotation.NonNull;

import guepardoapps.mycoins.basic.enums.Currency;
import guepardoapps.mycoins.basic.utils.Logger;

public class SettingsController {
    private static final SettingsController SINGLETON = new SettingsController();

    private static final String TAG = SettingsController.class.getSimpleName();
    private Logger _logger;

    public static final String PREFS_INSTALLED = "prefs_installed";
    public static final String PREF_CURRENCY = "currency";
    public static final String PREF_HOURS_AGGREGATE = "hours_aggregate";
    public static final String PREF_RELOAD_ENABLED = "reload_enabled";
    public static final String PREF_RELOAD_TIMEOUT = "reload_timeout";

    private SharedPrefController _sharedPrefController;

    private boolean _isInitialized;

    private SettingsController() {
        _logger = new Logger(TAG);
    }

    public static SettingsController getInstance() {
        return SINGLETON;
    }

    public void Initialize(@NonNull Context context) {
        if (_isInitialized) {
            _logger.Error("Already initialized!");
            return;
        }

        _sharedPrefController = new SharedPrefController(context);
        if (!_sharedPrefController.LoadBooleanValueFromSharedPreferences(PREFS_INSTALLED)) {
            _sharedPrefController.SaveBooleanValue(PREFS_INSTALLED, true);
            SetCurrency(Currency.EUR);
            SetHoursAggregate(24);
            SetReloadEnabled(true);
            SetReloadTimeout(30);
        }

        _isInitialized = true;
    }

    public void SetCurrency(@NonNull Currency currency) {
        _sharedPrefController.SaveStringValue(PREF_CURRENCY, String.valueOf(currency));
    }

    public Currency GetCurrency() {
        String currency = _sharedPrefController.LoadStringValueFromSharedPreferences(PREF_CURRENCY);

        if (currency == null) {
            return Currency.EUR;
        } else {
            if (currency.contains("EUR")) {
                return Currency.EUR;
            } else {
                return Currency.USD;
            }
        }
    }

    public void SetHoursAggregate(int hours) {
        _sharedPrefController.SaveIntegerValue(PREF_HOURS_AGGREGATE, hours);
    }

    public int GetHoursAggregate() {
        return _sharedPrefController.LoadIntegerValueFromSharedPreferences(PREF_HOURS_AGGREGATE);
    }

    public void SetReloadEnabled(boolean enabled) {
        _sharedPrefController.SaveBooleanValue(PREF_RELOAD_ENABLED, enabled);
    }

    public boolean GetReloadEnabled() {
        return _sharedPrefController.LoadBooleanValueFromSharedPreferences(PREF_RELOAD_ENABLED);
    }

    public void SetReloadTimeout(int minutes) {
        _sharedPrefController.SaveIntegerValue(PREF_RELOAD_TIMEOUT, minutes);
    }

    public int GetReloadTimeout() {
        return _sharedPrefController.LoadIntegerValueFromSharedPreferences(PREF_RELOAD_TIMEOUT);
    }

    public void Dispose() {
        _logger.Debug("Dispose");
        _sharedPrefController = null;
        _isInitialized = false;
    }
}
