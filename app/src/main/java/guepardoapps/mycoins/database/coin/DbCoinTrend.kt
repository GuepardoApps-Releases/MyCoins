package guepardoapps.mycoins.database.coin

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import guepardoapps.mycoins.models.CoinType
import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.enums.DbAction
import guepardoapps.mycoins.extensions.byString
import guepardoapps.mycoins.models.CoinTrend
import guepardoapps.mycoins.models.CoinTypes
import guepardoapps.mycoins.models.DbPublishSubject
import guepardoapps.mycoins.publishsubject.DbCoinTrendActionPublishSubject
import guepardoapps.mycoins.utils.Logger

// Helpful
// https://developer.android.com/training/data-storage/sqlite
// https://www.techotopia.com/index.php/A_Kotlin_Android_SQLite_Database_Tutorial

internal class DbCoinTrend(context: Context) : SQLiteOpenHelper(context, DatabaseName, null, DatabaseVersion) {
    private val tag: String = DbCoinTrend::class.java.simpleName

    override fun onCreate(database: SQLiteDatabase) {
        val createTable = (
                "CREATE TABLE IF NOT EXISTS $DatabaseTable"
                        + "("
                        + "$ColumnId INTEGER PRIMARY KEY autoincrement,"
                        + "$ColumnCoinType TEXT,"
                        + "$ColumnTime BIGINT,"
                        + "$ColumnOpenValue DOUBLE,"
                        + "$ColumnCloseValue DOUBLE,"
                        + "$ColumnLowValue DOUBLE,"
                        + "$ColumnHighValue DOUBLE,"
                        + "$ColumnCurrency INT"
                        + ")")
        database.execSQL(createTable)
        DbCoinTrendActionPublishSubject.instance.publishSubject.onNext(DbPublishSubject(DbAction.Null, 1f))
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database.execSQL("DROP TABLE IF EXISTS $DatabaseTable")
        onCreate(database)
    }

    override fun onDowngrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(database, oldVersion, newVersion)
    }

    fun add(coinCurrency: CoinTrend, currentActionNo: Float = 1f, totalActions: Float = 1f): Long {
        Logger.instance.debug(tag, "add: $coinCurrency")

        val values = ContentValues().apply {
            put(ColumnCoinType, coinCurrency.coinType.type)
            put(ColumnTime, coinCurrency.time)
            put(ColumnOpenValue, coinCurrency.openValue)
            put(ColumnCloseValue, coinCurrency.closeValue)
            put(ColumnLowValue, coinCurrency.lowValue)
            put(ColumnHighValue, coinCurrency.highValue)
            put(ColumnCurrency, coinCurrency.currency.id)
        }

        val database = this.writableDatabase
        val returnValue = database.insert(DatabaseTable, null, values)

        DbCoinTrendActionPublishSubject.instance.publishSubject.onNext(DbPublishSubject(DbAction.Add, currentActionNo / totalActions))
        return returnValue
    }

    fun delete(coinCurrency: CoinTrend, currentActionNo: Float = 1f, totalActions: Float = 1f): Int {
        Logger.instance.debug(tag, "delete: $coinCurrency")

        val database = this.writableDatabase

        val selection = "$ColumnId LIKE ?"
        val selectionArgs = arrayOf(coinCurrency.id.toString())
        val returnValue = database.delete(DatabaseTable, selection, selectionArgs)

        DbCoinTrendActionPublishSubject.instance.publishSubject.onNext(DbPublishSubject(DbAction.Delete, currentActionNo / totalActions))
        return returnValue
    }

    fun get(): MutableList<CoinTrend> {
        Logger.instance.debug(tag, "get")

        val database = this.readableDatabase

        val projection = arrayOf(
                ColumnId,
                ColumnCoinType,
                ColumnTime,
                ColumnOpenValue,
                ColumnCloseValue,
                ColumnLowValue,
                ColumnHighValue,
                ColumnCurrency)

        val sortOrder = "$ColumnId ASC"

        val cursor = database.query(
                DatabaseTable, projection, null, null,
                null, null, sortOrder)

        val list = mutableListOf<CoinTrend>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(ColumnId))
                val time = getLong(getColumnIndexOrThrow(ColumnTime))
                val openValue = getDouble(getColumnIndexOrThrow(ColumnOpenValue))
                val closeValue = getDouble(getColumnIndexOrThrow(ColumnCloseValue))
                val lowValue = getDouble(getColumnIndexOrThrow(ColumnLowValue))
                val highValue = getDouble(getColumnIndexOrThrow(ColumnHighValue))
                val currencyId = getInt(getColumnIndexOrThrow(ColumnCurrency))

                val coinTypeString = getString(getColumnIndexOrThrow(ColumnCoinType))
                val coinType = CoinTypes.values.byString(coinTypeString)
                if (coinType == CoinTypes.Null) coinType.type = coinTypeString

                val currency = Currency.values()[currencyId]

                val coinCurrency = CoinTrend()
                coinCurrency.id = id
                coinCurrency.coinType = coinType
                coinCurrency.time = time
                coinCurrency.openValue = openValue
                coinCurrency.closeValue = closeValue
                coinCurrency.lowValue = lowValue
                coinCurrency.highValue = highValue
                coinCurrency.currency = currency

                list.add(coinCurrency)
            }
        }

        DbCoinTrendActionPublishSubject.instance.publishSubject.onNext(DbPublishSubject(DbAction.Get, 1f))
        return list
    }

    fun findByCoinType(coinType: CoinType): MutableList<CoinTrend> {
        Logger.instance.debug(tag, "findByCoinType: $coinType")

        val database = this.readableDatabase

        val projection = arrayOf(
                ColumnId,
                ColumnCoinType,
                ColumnTime,
                ColumnOpenValue,
                ColumnCloseValue,
                ColumnLowValue,
                ColumnHighValue,
                ColumnCurrency)

        val cursor = database.query(
                DatabaseTable, projection,
                "$ColumnCoinType = '${coinType.type}'",
                null, null, null,
                "$ColumnId ASC")

        val list = mutableListOf<CoinTrend>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(ColumnId))
                val time = getLong(getColumnIndexOrThrow(ColumnTime))
                val openValue = getDouble(getColumnIndexOrThrow(ColumnOpenValue))
                val closeValue = getDouble(getColumnIndexOrThrow(ColumnCloseValue))
                val lowValue = getDouble(getColumnIndexOrThrow(ColumnLowValue))
                val highValue = getDouble(getColumnIndexOrThrow(ColumnHighValue))
                val currencyId = getInt(getColumnIndexOrThrow(ColumnCurrency))

                val currency = Currency.values()[currencyId]

                val coinCurrency = CoinTrend()
                coinCurrency.id = id
                coinCurrency.coinType = coinType
                coinCurrency.time = time
                coinCurrency.openValue = openValue
                coinCurrency.closeValue = closeValue
                coinCurrency.lowValue = lowValue
                coinCurrency.highValue = highValue
                coinCurrency.currency = currency

                list.add(coinCurrency)
            }
        }

        DbCoinTrendActionPublishSubject.instance.publishSubject.onNext(DbPublishSubject(DbAction.Get, 1f))
        return list
    }

    fun clear(coinType: CoinType) {
        Logger.instance.debug(tag, "clear")

        val coinTrendList = findByCoinType(coinType)
        for ((index, value) in coinTrendList.withIndex()) {
            delete(value, index.toFloat(), coinTrendList.size.toFloat())
        }
    }

    companion object {
        private const val DatabaseVersion = 1
        private const val DatabaseName = "guepardoapps-mycoins-coin-trend.db"
        private const val DatabaseTable = "coinCurrencyTable"

        private const val ColumnId = "_id"
        private const val ColumnCoinType = "coinType"
        private const val ColumnTime = "time"
        private const val ColumnOpenValue = "openValue"
        private const val ColumnCloseValue = "closeValue"
        private const val ColumnLowValue = "lowValue"
        private const val ColumnHighValue = "highValue"
        private const val ColumnCurrency = "currency"
    }
}