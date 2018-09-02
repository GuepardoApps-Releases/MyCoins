package guepardoapps.mycoins.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import guepardoapps.mycoins.R
import guepardoapps.mycoins.controller.NavigationController
import guepardoapps.mycoins.customadapter.CoinListAdapter
import guepardoapps.mycoins.enums.DbAction
import guepardoapps.mycoins.publishsubject.DbCoinActionPublishSubject
import guepardoapps.mycoins.publishsubject.DbCoinConversionActionPublishSubject
import guepardoapps.mycoins.publishsubject.DbCoinTrendActionPublishSubject
import guepardoapps.mycoins.services.coin.CoinService
import guepardoapps.mycoins.utils.Logger
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.sephiroth.android.library.bottomnavigation.BottomNavigation
import kotlinx.android.synthetic.main.side_main.*

class ActivityMain : Activity(), BottomNavigation.OnMenuItemSelectionListener {
    private val tag: String = ActivityMain::class.java.simpleName

    private var subscriptionDbCoinAction: Disposable? = null
    private var subscriptionDbCoinConversionAction: Disposable? = null
    private var subscriptionDbCoinTrendAction: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.side_main)
        bottomNavigation.setOnMenuItemClickListener(this)

        subscriptionDbCoinAction = DbCoinActionPublishSubject.instance.publishSubject
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { dbAction -> handleDbAction(dbAction) },
                        { _ -> }
                )

        subscriptionDbCoinConversionAction = DbCoinConversionActionPublishSubject.instance.publishSubject
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { dbAction -> handleDbAction(dbAction) },
                        { _ -> }
                )

        subscriptionDbCoinTrendAction = DbCoinTrendActionPublishSubject.instance.publishSubject
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { dbAction -> handleDbAction(dbAction) },
                        { _ -> }
                )
    }

    override fun onResume() {
        super.onResume()
        if (subscriptionDbCoinAction != null && subscriptionDbCoinConversionAction != null && subscriptionDbCoinTrendAction != null) {
            listView.adapter = CoinListAdapter(this, CoinService.instance.getCoinList())
            listView.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        subscriptionDbCoinAction?.dispose()
        subscriptionDbCoinAction = null

        subscriptionDbCoinConversionAction?.dispose()
        subscriptionDbCoinConversionAction = null

        subscriptionDbCoinTrendAction?.dispose()
        subscriptionDbCoinTrendAction = null
    }

    override fun onMenuItemSelect(itemId: Int, position: Int, fromUser: Boolean) {
        performMenuAction(itemId)
    }

    override fun onMenuItemReselect(itemId: Int, position: Int, fromUser: Boolean) {
        performMenuAction(itemId)
    }

    private fun handleDbAction(dbAction: DbAction) {
        when (dbAction) {
            DbAction.Add -> listView.adapter = CoinListAdapter(this, CoinService.instance.getCoinList())
            DbAction.Update -> listView.adapter = CoinListAdapter(this, CoinService.instance.getCoinList())
            DbAction.Delete -> listView.adapter = CoinListAdapter(this, CoinService.instance.getCoinList())
            else -> Logger.instance.verbose(tag, "No action needed for dbAction $dbAction")
        }
    }

    private fun performMenuAction(itemId: Int) {
        when (itemId) {
            R.id.item_add -> NavigationController(this).navigate(ActivityEdit::class.java, false)
            R.id.item_about -> NavigationController(this).navigate(ActivityAbout::class.java, false)
            else -> Logger.instance.error(tag, "Found no menu entry with id $itemId")
        }
    }
}