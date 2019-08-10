package guepardoapps.mycoins.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import guepardoapps.mycoins.R
import guepardoapps.mycoins.annotations.SortField
import guepardoapps.mycoins.controller.NavigationController
import guepardoapps.mycoins.customadapter.CoinListAdapter
import guepardoapps.mycoins.enums.DbAction
import guepardoapps.mycoins.models.CoinAdapterHolder
import guepardoapps.mycoins.models.DbPublishSubject
import guepardoapps.mycoins.publishsubject.DbCoinActionPublishSubject
import guepardoapps.mycoins.publishsubject.DbCoinConversionActionPublishSubject
import guepardoapps.mycoins.publishsubject.DbCoinTrendActionPublishSubject
import guepardoapps.mycoins.services.coin.CoinService
import guepardoapps.mycoins.utils.Logger
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.sephiroth.android.library.bottomnavigation.BottomNavigation
import kotlinx.android.synthetic.main.side_main.*
import kotlin.reflect.full.declaredMemberFunctions

@ExperimentalUnsignedTypes
class ActivityMain : Activity(), BottomNavigation.OnMenuItemSelectionListener {

    private val context = this

    private var sortDirectionIsAsc: Boolean = true

    private val sortFields: List<String> = CoinAdapterHolder::class.declaredMemberFunctions
            .mapNotNull { kFunction -> kFunction.annotations.firstOrNull { annotation -> annotation is SortField<*> } }
            .sortedBy { annotation -> (annotation as SortField<*>).position }
            .map { annotation -> (annotation as SortField<*>).field }

    private var sortField: String = sortFields.first()

    private val subscriptions: ArrayList<Disposable> = arrayListOf()

    private val subscriptionsSize: Int = 3

    private lateinit var coinListAdapter: CoinListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.side_main)

        coinListAdapter = CoinListAdapter(context, mutableListOf(), sortField, sortDirectionIsAsc)

        bottomNavigation.setOnMenuItemClickListener(context)

        sortFieldSpinner.apply {
            adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, sortFields)
                    .apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    sortField = sortFields[position]
                    coinListAdapter.updateDataSet(CoinService.instance.getCoinList(), sortField, sortDirectionIsAsc)
                }
            }
        }

        sortDirectionButton.apply {
            setOnClickListener {
                sortDirectionIsAsc = !sortDirectionIsAsc
                @Suppress("DEPRECATION")
                setIcon(if (sortDirectionIsAsc) resources.getDrawable(android.R.drawable.arrow_up_float) else resources.getDrawable(android.R.drawable.arrow_down_float), true)
                coinListAdapter.updateDataSet(CoinService.instance.getCoinList(), sortField, sortDirectionIsAsc)
            }
        }

        subscriptions.plusAssign(DbCoinActionPublishSubject.instance.publishSubject
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { dbAction -> handleDbPublishSubject(dbAction) },
                        { }
                ))
        subscriptions.plusAssign(DbCoinConversionActionPublishSubject.instance.publishSubject
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { dbAction -> handleDbPublishSubject(dbAction) },
                        { }
                ))
        subscriptions.plusAssign(DbCoinTrendActionPublishSubject.instance.publishSubject
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { dbAction -> handleDbPublishSubject(dbAction) },
                        { }
                ))
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.forEach { disposable -> disposable.dispose() }
        subscriptions.clear()
    }

    override fun onResume() {
        super.onResume()
        if (subscriptions.size == subscriptionsSize) {
            listView.adapter = coinListAdapter
            coinListAdapter.updateDataSet(CoinService.instance.getCoinList(), sortField, sortDirectionIsAsc)
        }
    }

    override fun onMenuItemReselect(itemId: Int, position: Int, fromUser: Boolean) = performMenuAction(itemId)

    override fun onMenuItemSelect(itemId: Int, position: Int, fromUser: Boolean) = performMenuAction(itemId)

    private fun handleDbPublishSubject(dbPublishSubject: DbPublishSubject) {
        when (dbPublishSubject.dbAction) {
            DbAction.Add,
            DbAction.Update,
            DbAction.Delete ->
                if (dbPublishSubject.percentage == 1f) {
                    coinListAdapter.updateDataSet(CoinService.instance.getCoinList(), sortField, sortDirectionIsAsc)
                } else {
                    Logger.instance.debug(ActivityMain::class.java.simpleName, "Received action ${dbPublishSubject.dbAction} with percentage ${dbPublishSubject.percentage}")
                }
            else -> Logger.instance.verbose(ActivityMain::class.java.simpleName, "No action needed for dbAction ${dbPublishSubject.dbAction}")
        }
    }

    private fun performMenuAction(itemId: Int) {
        when (itemId) {
            R.id.item_add -> NavigationController(context).navigate(ActivityEdit::class.java, false)
            R.id.item_about -> NavigationController(context).navigate(ActivityAbout::class.java, false)
            R.id.item_settings -> NavigationController(context).navigate(ActivitySettings::class.java, false)
            else -> Logger.instance.error(ActivityMain::class.java.simpleName, "Found no menu entry with id $itemId")
        }
    }
}