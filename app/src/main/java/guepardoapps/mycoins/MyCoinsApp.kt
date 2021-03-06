package guepardoapps.mycoins

import android.app.Application
import guepardoapps.mycoins.services.coin.CoinService
import guepardoapps.mycoins.utils.Logger

class MyCoinsApp : Application() {
    private val tag: String = MyCoinsApp::class.java.simpleName

    override fun onCreate() {
        super.onCreate()

        Logger.instance.initialize(this)
        // Logger.instance.loggingEnabled = BuildConfig.DEBUG
        Logger.instance.loggingEnabled = false
        Logger.instance.writeToDatabaseEnabled = false
        Logger.instance.debug(tag, "onCreate")

        CoinService.instance.initialize(this)
    }
}