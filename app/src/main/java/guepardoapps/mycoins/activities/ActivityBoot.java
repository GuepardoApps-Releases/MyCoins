package guepardoapps.mycoins.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import guepardoapps.mycoins.R;
import guepardoapps.mycoins.controller.NavigationController;
import guepardoapps.mycoins.controller.SettingsController;

public class ActivityBoot extends Activity {
    private static final String TAG = ActivityBoot.class.getSimpleName();
    private Logger _logger;

    private NavigationController _navigationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_boot);

        _logger = new Logger(TAG, true);
        _logger.Debug("onCreate");

        _navigationController = new NavigationController(this);

        SettingsController.getInstance().Initialize(this);
        SettingsController.getInstance().Dispose();
    }

    @Override
    protected void onResume() {
        super.onResume();
        _logger.Debug("onResume");
        navigateToMain();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _logger.Debug("onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _logger.Debug("onDestroy");
    }

    private void navigateToMain() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                _navigationController.NavigateTo(MainActivity.class, true);
            }
        }, 1500);
    }
}