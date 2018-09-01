package guepardoapps.mycoins

import android.app.Application
import guepardoapps.mycoins.utils.Logger

class MyCoinsApp : Application() {
    private val tag: String = MyCoinsApp::class.java.simpleName

    override fun onCreate() {
        super.onCreate()
        Logger.instance.initialize(this)
        Logger.instance.debug(tag, "onCreate")
    }
}