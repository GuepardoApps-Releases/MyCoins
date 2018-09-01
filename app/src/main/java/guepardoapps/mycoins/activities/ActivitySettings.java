package guepardoapps.mycoins.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import guepardoapps.mycoins.R;

public class ActivitySettings extends Activity {
    private static final String TAG = ActivitySettings.class.getSimpleName();
    private Logger _logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_settings);

        _logger = new Logger(TAG, true);
        _logger.Debug("onCreate");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        _logger.Debug("onKeyDown");

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}