package guepardoapps.mycoins.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import guepardoapps.mycoins.CoinService;
import guepardoapps.mycoins.R;
import guepardoapps.mycoins.basic.dto.CoinDto;
import guepardoapps.mycoins.basic.utils.Logger;
import guepardoapps.mycoins.common.controller.NavigationController;
import guepardoapps.mycoins.common.controller.ReceiverController;
import guepardoapps.mycoins.common.controller.SettingsController;
import guepardoapps.mycoins.customadapter.CoinListAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Logger _logger;

    private ListView _listView;

    private Context _context;

    private CoinService _coinService;

    private NavigationController _navigationController;
    private ReceiverController _receiverController;

    private BroadcastReceiver _coinLoadFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            _logger.Debug("_coinLoadFinishedReceiver onReceive");
            CoinListAdapter coinListAdapter = new CoinListAdapter(_context, _coinService.GetDataList());
            _listView.setAdapter(coinListAdapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _logger = new Logger(TAG);
        _logger.Debug("onCreate");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _listView = (ListView) findViewById(R.id.listView_coin);

        _context = this;

        _coinService = CoinService.getInstance();
        _coinService.Initialize(_context, true, 15); // TODO read settings for reload and timeout
        _coinService.LoadCoinConversionList();

        CoinListAdapter coinListAdapter = new CoinListAdapter(_context, _coinService.GetDataList());
        _listView.setAdapter(coinListAdapter);

        _navigationController = new NavigationController(_context);
        _receiverController = new ReceiverController(_context);
        SettingsController.getInstance().Initialize(_context);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                data.putSerializable(CoinService.CoinIntent, new CoinDto(-1, "BTC", 0, -1, CoinDto.Aggregation.NULL, -1, CoinDto.Aggregation.NULL, R.drawable.btc, CoinDto.Action.Add));
                _navigationController.NavigateWithData(ActivityCoinEdit.class, data, false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        _logger.Debug("onResume");
        _receiverController.RegisterReceiver(_coinLoadFinishedReceiver, new String[]{CoinService.CoinDownloadFinishedBroadcast});
        CoinListAdapter coinListAdapter = new CoinListAdapter(_context, _coinService.GetDataList());
        _listView.setAdapter(coinListAdapter);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reload) {
            _coinService.LoadCoinConversionList();
            return true;
        } else if (id == R.id.action_settings) {
            _navigationController.NavigateTo(ActivitySettings.class, false);
            return true;
        } else if (id == R.id.action_about) {
            _navigationController.NavigateTo(ActivityImpressum.class, false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
