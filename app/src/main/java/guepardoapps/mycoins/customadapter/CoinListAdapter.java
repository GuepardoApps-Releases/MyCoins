package guepardoapps.mycoins.customadapter;

import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rey.material.app.Dialog;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.FloatingActionButton;

import guepardoapps.mycoins.CoinService;
import guepardoapps.mycoins.R;
import guepardoapps.mycoins.activities.ActivityCoinEdit;
import guepardoapps.mycoins.basic.dto.CoinDto;
import guepardoapps.mycoins.basic.enums.Currency;
import guepardoapps.mycoins.basic.utils.Logger;
import guepardoapps.mycoins.common.controller.NavigationController;

public class CoinListAdapter extends BaseAdapter {
    private static final String TAG = CoinListAdapter.class.getSimpleName();
    private Logger _logger;

    private Context _context;
    private NavigationController _navigationController;

    private ArrayList<CoinDto> _coinList;

    private static LayoutInflater _inflater = null;
    private CoinListAdapter _coinListAdapter;

    public CoinListAdapter(@NonNull Context context, @NonNull ArrayList<CoinDto> coinList) {
        _logger = new Logger(TAG, true);
        _logger.Debug("created...");

        _context = context;
        _navigationController = new NavigationController(_context);

        _coinList = coinList;

        _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _coinListAdapter = this;
    }

    @Override
    public int getCount() {
        return _coinList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class Holder {
        ImageView _typeImage;
        TextView _type;
        TextView _amount;
        TextView _euroConversion;
        TextView _euroAmount;
        ImageView _euroAggregation;
        TextView _usDollarConversion;
        TextView _usDollarAmount;
        ImageView _usDollarAggregation;
        FloatingActionButton _btnDelete;
        FloatingActionButton _btnEdit;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @Override
    public View getView(final int index, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView = _inflater.inflate(R.layout.list_item, null);

        final CoinDto coin = _coinList.get(index);

        holder._typeImage = rowView.findViewById(R.id.itemTypeImage);
        holder._typeImage.setImageResource(coin.GetIcon());

        holder._type = rowView.findViewById(R.id.itemTypeText);
        holder._type.setText(coin.GetType());
        holder._amount = rowView.findViewById(R.id.itemAmount);
        holder._amount.setText(String.format(Locale.getDefault(), "%.6f", coin.GetAmount()));

        holder._euroConversion = rowView.findViewById(R.id.itemEuroConversion);
        holder._euroConversion.setText(coin.GetEuroConversionString());
        holder._euroAmount = rowView.findViewById(R.id.itemEuroAmount);
        holder._euroAmount.setText(coin.GetValueString(Currency.EUR));
        holder._euroAggregation = rowView.findViewById(R.id.itemEuroAggregation);
        if (coin.GetEuroAggregation() == CoinDto.Aggregation.Rise) {
            holder._euroAggregation.setImageResource(android.R.drawable.arrow_up_float);
        } else if (coin.GetEuroAggregation() == CoinDto.Aggregation.Fall) {
            holder._euroAggregation.setImageResource(android.R.drawable.arrow_down_float);
        } else {
            holder._euroAggregation.setImageResource(android.R.drawable.radiobutton_off_background);
        }

        holder._usDollarConversion = rowView.findViewById(R.id.itemUsDollarConversion);
        holder._usDollarConversion.setText(coin.GetUsDollarConversionString());
        holder._usDollarAmount = rowView.findViewById(R.id.itemUsDollarAmount);
        holder._usDollarAmount.setText(coin.GetValueString(Currency.USD));
        holder._usDollarAggregation = rowView.findViewById(R.id.itemUsDollarAggregation);
        if (coin.GetUsDollarAggregation() == CoinDto.Aggregation.Rise) {
            holder._usDollarAggregation.setImageResource(android.R.drawable.arrow_up_float);
        } else if (coin.GetUsDollarAggregation() == CoinDto.Aggregation.Fall) {
            holder._usDollarAggregation.setImageResource(android.R.drawable.arrow_down_float);
        } else {
            holder._usDollarAggregation.setImageResource(android.R.drawable.radiobutton_off_background);
        }

        holder._btnEdit = rowView.findViewById(R.id.btnEdit);
        holder._btnEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _logger.Debug("_btnEdit onClick");

                coin.SetAction(CoinDto.Action.Update);

                Bundle details = new Bundle();
                details.putSerializable(CoinService.CoinIntent, coin);

                _navigationController.NavigateWithData(ActivityCoinEdit.class, details, false);
            }
        });

        holder._btnDelete = rowView.findViewById(R.id.btnDelete);
        holder._btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _logger.Debug("_btnDelete onClick");

                boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;

                final Dialog dialog = new Dialog(_context);
                dialog
                        .title("Do you want to delete this note?")
                        .positiveAction("Yes")
                        .negativeAction("No")
                        .applyStyle(isLightTheme ? R.style.SimpleDialogLight : R.style.SimpleDialog)
                        .setCancelable(true);

                dialog.positiveActionClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CoinService.getInstance().DeleteCoin(coin);
                        _coinList.remove(coin);
                        _coinListAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                dialog.negativeActionClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        return rowView;
    }
}