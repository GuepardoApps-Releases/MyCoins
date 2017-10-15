package guepardoapps.mycoins;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;

import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import guepardoapps.mycoins.basic.classes.SerializableList;
import guepardoapps.mycoins.basic.classes.SerializableTriple;
import guepardoapps.mycoins.basic.dto.CoinDto;
import guepardoapps.mycoins.basic.utils.Logger;
import guepardoapps.mycoins.basic.utils.Tools;
import guepardoapps.mycoins.common.controller.BroadcastController;
import guepardoapps.mycoins.common.controller.DownloadController;
import guepardoapps.mycoins.common.controller.NetworkController;
import guepardoapps.mycoins.common.controller.ReceiverController;
import guepardoapps.mycoins.common.controller.SettingsController;
import guepardoapps.mycoins.common.converter.JsonDataToCoinAggregateConverter;
import guepardoapps.mycoins.common.converter.JsonDataToCoinConversionConverter;
import guepardoapps.mycoins.common.database.DatabaseCoinList;

public class CoinService {
    public static class CoinConversionDownloadFinishedContent implements Serializable {
        public SerializableList<SerializableTriple<String, Double, Double>> CoinConversionList;
        public boolean Success;
        public byte[] Response;

        public CoinConversionDownloadFinishedContent(SerializableList<SerializableTriple<String, Double, Double>> coinConversionList, boolean succcess, @NonNull byte[] response) {
            CoinConversionList = coinConversionList;
            Success = succcess;
            Response = response;
        }
    }

    public static final String CoinConversionDownloadFinishedBroadcast = "guepardoapps.lucahome.data.service.coinconversion.download.finished";
    public static final String CoinConversionDownloadFinishedBundle = "CoinConversionDownloadFinishedBundle";

    public static final String CoinDownloadFinishedBroadcast = "guepardoapps.lucahome.data.service.coin.download.finished";

    public static final String CoinIntent = "CoinIntent";

    private static final CoinService SINGLETON = new CoinService();
    private boolean _isInitialized;

    private static final String TAG = CoinService.class.getSimpleName();
    private Logger _logger;

    private static final int MIN_TIMEOUT_MIN = 15;
    private static final int MAX_TIMEOUT_MIN = 24 * 60;

    private boolean _reloadEnabled;
    private int _reloadTimeout;
    private Handler _reloadHandler = new Handler();
    private Runnable _reloadListRunnable = new Runnable() {
        @Override
        public void run() {
            _logger.Debug("_reloadListRunnable run");

            LoadCoinConversionList();
            LoadData();

            if (_reloadEnabled && NetworkController.IsNetworkAvailable(_context)) {
                _reloadHandler.postDelayed(_reloadListRunnable, _reloadTimeout);
            }
        }
    };

    private Context _context;
    private BroadcastController _broadcastController;
    private DownloadController _downloadController;
    private ReceiverController _receiverController;
    private SettingsController _settingsController;

    private DatabaseCoinList _databaseCoinList;

    private ArrayList<CoinDto> _coinList = new ArrayList<>();
    private ArrayList<CoinDto> _filteredCoinList = new ArrayList<>();
    private SerializableList<SerializableTriple<String, Double, Double>> _coinConversionList = new SerializableList<>();

    private BroadcastReceiver _coinAggregateEurDownloadFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            _logger.Debug("_coinAggregateEurDownloadFinishedReceiver");
            DownloadController.DownloadFinishedBroadcastContent content = (DownloadController.DownloadFinishedBroadcastContent) intent.getSerializableExtra(DownloadController.DownloadFinishedBundle);
            String contentResponse = Tools.DecompressByteArrayToString(content.Response);

            if (content.CurrentDownloadType != DownloadController.DownloadType.CoinAggregateEur) {
                _logger.Debug(String.format(Locale.getDefault(), "Received download finished with downloadType %s", content.CurrentDownloadType));
                return;
            }

            CoinDto coinDto = (CoinDto) content.Additional;
            try {
                coinDto = JsonDataToCoinAggregateConverter.UpdateAggregate(coinDto, contentResponse, "EUR");

                for (int index = 0; index < _coinList.size(); index++) {
                    if (_coinList.get(index).GetId() == coinDto.GetId()) {
                        _coinList.set(index, coinDto);
                        _logger.Debug(String.format(Locale.getDefault(), "Updated CoinDto is %s", coinDto));
                        _broadcastController.SendSimpleBroadcast(CoinDownloadFinishedBroadcast);
                        break;
                    }
                }
            } catch (JSONException jsonException) {
                _logger.Error(jsonException.getMessage());
            }

            String requestUrlUsd = String.format(Locale.getDefault(), "https://min-api.cryptocompare.com/data/histohour?fsym=%s&tsym=%s&limit=%d&aggregate=3&e=CCCAGG", coinDto.GetType(), "USD", SettingsController.getInstance().GetHoursAggregate());
            _logger.Debug(String.format(Locale.getDefault(), "RequestUrlUsd is: %s", requestUrlUsd));

            _downloadController.SendCommandToWebsiteAsync(requestUrlUsd, DownloadController.DownloadType.CoinAggregateUsd, coinDto);
        }
    };

    private BroadcastReceiver _coinAggregateUsdDownloadFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            _logger.Debug("_coinAggregateUsdDownloadFinishedReceiver");
            DownloadController.DownloadFinishedBroadcastContent content = (DownloadController.DownloadFinishedBroadcastContent) intent.getSerializableExtra(DownloadController.DownloadFinishedBundle);
            String contentResponse = Tools.DecompressByteArrayToString(content.Response);

            if (content.CurrentDownloadType != DownloadController.DownloadType.CoinAggregateUsd) {
                _logger.Debug(String.format(Locale.getDefault(), "Received download finished with downloadType %s", content.CurrentDownloadType));
                return;
            }

            CoinDto coinDto = (CoinDto) content.Additional;
            try {
                coinDto = JsonDataToCoinAggregateConverter.UpdateAggregate(coinDto, contentResponse, "USD");

                for (int index = 0; index < _coinList.size(); index++) {
                    if (_coinList.get(index).GetId() == coinDto.GetId()) {
                        _coinList.set(index, coinDto);
                        _logger.Debug(String.format(Locale.getDefault(), "Updated CoinDto is %s", coinDto));
                        _broadcastController.SendSimpleBroadcast(CoinDownloadFinishedBroadcast);
                        break;
                    }
                }
            } catch (JSONException jsonException) {
                _logger.Error(jsonException.getMessage());
            }
        }
    };

    private BroadcastReceiver _coinConversionDownloadFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            _logger.Debug("_coinConversionDownloadFinishedReceiver");
            DownloadController.DownloadFinishedBroadcastContent content = (DownloadController.DownloadFinishedBroadcastContent) intent.getSerializableExtra(DownloadController.DownloadFinishedBundle);
            String contentResponse = Tools.DecompressByteArrayToString(content.Response);

            if (content.CurrentDownloadType != DownloadController.DownloadType.CoinConversion) {
                _logger.Debug(String.format(Locale.getDefault(), "Received download finished with downloadType %s", content.CurrentDownloadType));
                return;
            }

            SerializableList<SerializableTriple<String, Double, Double>> coinConversionList = JsonDataToCoinConversionConverter.GetList(contentResponse);
            if (coinConversionList == null) {
                _logger.Error("Converted coinConversionList is null!");

                _broadcastController.SendSerializableBroadcast(
                        CoinConversionDownloadFinishedBroadcast,
                        CoinConversionDownloadFinishedBundle,
                        new CoinConversionDownloadFinishedContent(_coinConversionList, false, content.Response));

                return;
            }

            _coinConversionList = coinConversionList;

            mergeCoinConversionWithCoinList();

            _broadcastController.SendSerializableBroadcast(
                    CoinConversionDownloadFinishedBroadcast,
                    CoinConversionDownloadFinishedBundle,
                    new CoinConversionDownloadFinishedContent(_coinConversionList, true, content.Response));

            LoadData();
        }
    };

    private CoinService() {
        _logger = new Logger(TAG);
        _logger.Debug("Created...");
    }

    public static CoinService getInstance() {
        return SINGLETON;
    }

    public void Initialize(@NonNull Context context, boolean reloadEnabled, int reloadTimeout) {
        _logger.Debug("initialize");

        if (_isInitialized) {
            _logger.Warning("Already initialized!");
            return;
        }

        _reloadEnabled = reloadEnabled;

        _context = context;
        _broadcastController = new BroadcastController(_context);
        _downloadController = new DownloadController(_context);
        _receiverController = new ReceiverController(_context);
        _settingsController = SettingsController.getInstance();

        _databaseCoinList = new DatabaseCoinList(_context);
        _databaseCoinList.Open();

        _receiverController.RegisterReceiver(_coinAggregateEurDownloadFinishedReceiver, new String[]{DownloadController.DownloadFinishedBroadcast});
        _receiverController.RegisterReceiver(_coinAggregateUsdDownloadFinishedReceiver, new String[]{DownloadController.DownloadFinishedBroadcast});
        _receiverController.RegisterReceiver(_coinConversionDownloadFinishedReceiver, new String[]{DownloadController.DownloadFinishedBroadcast});

        SetReloadTimeout(reloadTimeout);

        _isInitialized = true;
    }

    public void Dispose() {
        _logger.Debug("Dispose");
        _reloadHandler.removeCallbacks(_reloadListRunnable);
        _receiverController.Dispose();
        _databaseCoinList.Close();
        _isInitialized = false;
    }

    public ArrayList<CoinDto> GetDataList() {
        return _coinList;
    }

    public ArrayList<String> GetTypeList() {
        ArrayList<String> typeList = new ArrayList<>();

        for (int index = 0; index < _coinConversionList.getSize(); index++) {
            typeList.add(_coinConversionList.getValue(index).GetKey());
        }

        return typeList;
    }

    public CoinDto GetById(int id) {
        for (int index = 0; index < _coinList.size(); index++) {
            CoinDto entry = _coinList.get(index);

            if (entry.GetId() == id) {
                return entry;
            }
        }

        return null;
    }

    public ArrayList<CoinDto> SearchDataList(@NonNull String searchKey) {
        _filteredCoinList = new ArrayList<>();

        for (int index = 0; index < _coinList.size(); index++) {
            CoinDto entry = _coinList.get(index);

            if (String.valueOf(entry.GetId()).contains(searchKey)
                    || entry.GetType().contains(searchKey)
                    || String.valueOf(entry.GetAmount()).contains(searchKey)
                    || String.valueOf(entry.GetEuroConversion()).contains(searchKey)
                    || String.valueOf(entry.GetUsDollarConversion()).contains(searchKey)) {
                _filteredCoinList.add(entry);
            }
        }

        return _filteredCoinList;
    }

    public String AllCoinsValue() {
        double value = 0;

        for (int index = 0; index < _coinList.size(); index++) {
            CoinDto entry = _coinList.get(index);
            value += entry.GetValue(_settingsController.GetCurrency());
        }

        return String.format(Locale.getDefault(), "Sum: %.2f €", value);
    }

    public String FilteredCoinsValue() {
        double value = 0;

        for (int index = 0; index < _filteredCoinList.size(); index++) {
            CoinDto entry = _filteredCoinList.get(index);
            value += entry.GetValue(_settingsController.GetCurrency());
        }

        return String.format(Locale.getDefault(), "Sum: %.2f €", value);
    }

    public void LoadCoinConversionList() {
        _logger.Debug("LoadCoinConversionList");

        String requestUrl = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BCH,BTC,DASH,ETC,ETH,LTC,XMR,ZEC&tsyms=EUR,USD";
        _logger.Debug(String.format(Locale.getDefault(), "RequestUrl is: %s", requestUrl));

        _downloadController.SendCommandToWebsiteAsync(requestUrl, DownloadController.DownloadType.CoinConversion, null);
    }

    public void LoadData() {
        _logger.Debug("LoadData");

        _coinList = _databaseCoinList.GetCoinList();
        mergeCoinConversionWithCoinList();

        _broadcastController.SendSimpleBroadcast(CoinDownloadFinishedBroadcast);
    }

    public void LoadAggregateTrend(@NonNull CoinDto coinDto) {
        _logger.Debug(String.format(Locale.getDefault(), "LoadAggregateTrend for %s of the last %d hours.", coinDto, SettingsController.getInstance().GetHoursAggregate()));

        String requestUrlEur = String.format(Locale.getDefault(), "https://min-api.cryptocompare.com/data/histohour?fsym=%s&tsym=%s&limit=%d&aggregate=3&e=CCCAGG", coinDto.GetType(), "EUR", SettingsController.getInstance().GetHoursAggregate());
        _logger.Debug(String.format(Locale.getDefault(), "RequestUrlEur is: %s", requestUrlEur));

        _downloadController.SendCommandToWebsiteAsync(requestUrlEur, DownloadController.DownloadType.CoinAggregateEur, coinDto);
    }

    public void AddCoin(@NonNull CoinDto entry) {
        _logger.Debug(String.format(Locale.getDefault(), "AddCoin: Updating entry %s", entry));
        _databaseCoinList.CreateEntry(entry);
        _coinList = _databaseCoinList.GetCoinList();
        LoadCoinConversionList();
    }

    public void UpdateCoin(@NonNull CoinDto entry) {
        _logger.Debug(String.format(Locale.getDefault(), "UpdateCoin: Updating entry %s", entry));
        _databaseCoinList.UpdateEntry(entry);
        _coinList = _databaseCoinList.GetCoinList();
        LoadCoinConversionList();
    }

    public void DeleteCoin(@NonNull CoinDto entry) {
        _logger.Debug(String.format(Locale.getDefault(), "DeleteCoin: Deleting entry %s", entry));
        _databaseCoinList.Delete(entry);
        _coinList = _databaseCoinList.GetCoinList();
        LoadCoinConversionList();
    }

    public boolean GetReloadEnabled() {
        return _reloadEnabled;
    }

    public void SetReloadEnabled(boolean reloadEnabled) {
        _reloadEnabled = reloadEnabled;
        if (_reloadEnabled) {
            _reloadHandler.removeCallbacks(_reloadListRunnable);
            _reloadHandler.postDelayed(_reloadListRunnable, _reloadTimeout);
        }
    }

    public int GetReloadTimeout() {
        return _reloadTimeout;
    }

    public void SetReloadTimeout(int reloadTimeout) {
        if (reloadTimeout < MIN_TIMEOUT_MIN) {
            _logger.Warning(String.format(Locale.getDefault(), "reloadTimeout %d is lower then MIN_TIMEOUT_MIN %d! Setting to MIN_TIMEOUT_MIN!", reloadTimeout, MIN_TIMEOUT_MIN));
            reloadTimeout = MIN_TIMEOUT_MIN;
        }
        if (reloadTimeout > MAX_TIMEOUT_MIN) {
            _logger.Warning(String.format(Locale.getDefault(), "reloadTimeout %d is higher then MAX_TIMEOUT_MS %d! Setting to MAX_TIMEOUT_MIN!", reloadTimeout, MAX_TIMEOUT_MIN));
            reloadTimeout = MAX_TIMEOUT_MIN;
        }

        _reloadTimeout = reloadTimeout * 60 * 1000;
        if (_reloadEnabled) {
            _reloadHandler.removeCallbacks(_reloadListRunnable);
            _reloadHandler.postDelayed(_reloadListRunnable, _reloadTimeout);
        }
    }

    private void mergeCoinConversionWithCoinList() {
        if (_coinList == null) {
            _logger.Error("_coinList is null!");
            return;
        }

        if (_coinConversionList == null) {
            _logger.Error("_coinConversionList is null!");
            return;
        }

        for (int index = 0; index < _coinList.size(); index++) {
            CoinDto entry = _coinList.get(index);

            for (int conversionIndex = 0; conversionIndex < _coinConversionList.getSize(); conversionIndex++) {
                SerializableTriple<String, Double, Double> conversionEntry = _coinConversionList.getValue(conversionIndex);

                if (entry.GetType().contains(conversionEntry.GetKey())) {
                    entry.SetEuroConversion(conversionEntry.GetValue());
                    entry.SetUsDollarConversion(conversionEntry.GetValue2());
                    _databaseCoinList.UpdateEntry(entry);
                    LoadAggregateTrend(entry);
                    break;
                }
            }
        }
    }
}
