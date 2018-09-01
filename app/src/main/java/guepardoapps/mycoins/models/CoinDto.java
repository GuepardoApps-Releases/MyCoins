package guepardoapps.mycoins.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Locale;

import guepardoapps.mycoins.enums.Currency;

public class CoinDto implements Serializable {
    private static final long serialVersionUID = 8473643947363954860L;
    private static final String TAG = CoinDto.class.getSimpleName();

    public enum Action {NULL, Add, Update}

    public enum Aggregation {NULL, Fall, Rise}

    private int _id;

    private String _type;
    private double _amount;

    private double _euroConversion;
    private Aggregation _euroAggregation;
    private double _usDollarConversion;
    private Aggregation _usDollarAggregation;

    private int _icon;
    private Action _action;

    public CoinDto(
            int id,
            @NonNull String type,
            double amount,
            double euroConversion,
            @NonNull Aggregation euroAggregation,
            double usDollarConversion,
            @NonNull Aggregation usDollarAggregation,
            int icon,
            @NonNull Action action) {
        _id = id;

        _type = type;
        _amount = amount;

        _euroConversion = euroConversion;
        _euroAggregation = euroAggregation;
        _usDollarConversion = usDollarConversion;
        _usDollarAggregation = usDollarAggregation;

        _icon = icon;
        _action = action;
    }

    public CoinDto(
            int id,
            @NonNull String type,
            double amount,
            double euroConversion,
            @NonNull Aggregation euroAggregation,
            double usDollarConversion,
            @NonNull Aggregation usDollarAggregation,
            int icon) {
        this(id, type, amount, euroConversion, euroAggregation, usDollarConversion, usDollarAggregation, icon, Action.NULL);
    }

    public CoinDto(
            int id,
            @NonNull String type,
            double amount) {
        this(id, type, amount, -1, Aggregation.NULL, -1, Aggregation.NULL, -1, Action.NULL);
    }

    public int GetId() {
        return _id;
    }

    public String GetType() {
        return _type;
    }

    public void SetType(@NonNull String type) {
        _type = type;
    }

    public double GetAmount() {
        return _amount;
    }

    public void SetAmount(double amount) {
        _amount = amount;
    }

    public double GetEuroConversion() {
        return _euroConversion;
    }

    public String GetEuroConversionString() {
        return String.format(Locale.getDefault(), "%.2f €", _euroConversion);
    }

    public void SetEuroConversion(double euroConversion) {
        _euroConversion = euroConversion;
    }

    public Aggregation GetEuroAggregation() {
        return _euroAggregation;
    }

    public void SetEuroAggregation(@NonNull Aggregation euroAggregation) {
        _euroAggregation = euroAggregation;
    }

    public double GetUsDollarConversion() {
        return _usDollarConversion;
    }

    public String GetUsDollarConversionString() {
        return String.format(Locale.getDefault(), "%.2f $", _usDollarConversion);
    }

    public void SetUsDollarConversion(double usDollarConversion) {
        _usDollarConversion = usDollarConversion;
    }

    public Aggregation GetUsDollarAggregation() {
        return _usDollarAggregation;
    }

    public void SetUsDollarAggregation(@NonNull Aggregation usDollarConversion) {
        _usDollarAggregation = usDollarConversion;
    }

    public double GetValue(@NonNull Currency currency) {
        switch (currency) {
            case EUR:
                return _amount * _euroConversion;
            case USD:
                return _amount * _usDollarConversion;
            default:
                return -1;
        }
    }

    public String GetValueString(@NonNull Currency currency) {
        switch (currency) {
            case EUR:
                return String.format(Locale.getDefault(), "%.2f €", GetValue(currency));
            case USD:
                return String.format(Locale.getDefault(), "%.2f $", GetValue(currency));
            default:
                return String.format(Locale.getDefault(), "%.2f", GetValue(currency));
        }
    }

    public int GetIcon() {
        return _icon;
    }

    public void SetAction(Action action) {
        _action = action;
    }

    public Action GetAction() {
        return _action;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "{%s: {Id: %d};{Type: %s};{Amount: %.6f};{EuroConversion: %.6f};{EuroAggregation: %s};{UsDollarConversion: %.6f};{UsDollarAggregation: %s}}", TAG, _id, _type, _amount, _euroConversion, _euroAggregation, _usDollarConversion, _usDollarAggregation);
    }
}
