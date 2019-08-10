package guepardoapps.mycoins.activities

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import com.github.guepardoapps.timext.kotlin.extensions.seconds
import com.github.guepardoapps.timext.kotlin.postDelayed
import guepardoapps.mycoins.R
import guepardoapps.mycoins.common.Constants
import guepardoapps.mycoins.controller.NavigationController
import guepardoapps.mycoins.controller.SharedPreferenceController
import guepardoapps.mycoins.services.coin.CoinService
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ActivityBoot : Activity() {

    private var subscription: Disposable? = null

    @ExperimentalUnsignedTypes
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.side_boot)

        val sharedPreferenceController = SharedPreferenceController(this)

        if (!sharedPreferenceController.load(Constants.sharedPrefName, false)) {
            sharedPreferenceController.run {
                save(Constants.currency, Constants.currencyDefault)
                save(Constants.reloadEnabled, Constants.reloadEnabledDefault)
                save(Constants.reloadTimeoutMs, Constants.reloadTimeoutMsDefault)
                save(Constants.sharedPrefName, true)
            }
        }

        if (!CoinService.instance.isInitialized) {
            subscription = CoinService.instance.initialSetupPublishSubject
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            { done ->
                                if (done) {
                                    NavigationController(this).navigate(ActivityMain::class.java, true)
                                }
                            },
                            { }
                    )
        } else {
            Handler().postDelayed({ NavigationController(this).navigate(ActivityMain::class.java, true) }, 1.5.seconds)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.dispose()
    }
}