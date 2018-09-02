package guepardoapps.mycoins.database.coin

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import guepardoapps.mycoins.enums.CoinType
import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.enums.DbAction
import guepardoapps.mycoins.models.CoinTrend
import guepardoapps.mycoins.publishsubject.DbCoinTrendActionPublishSubject

// Helpful
// https://developer.android.com/training/data-storage/sqlite
// https://www.techotopia.com/index.php/A_Kotlin_Android_SQLite_Database_Tutorial

internal class DbCoinTrend(context: Context)
    : SQLiteOpenHelper(context, DatabaseName, null, DatabaseVersion) {

    override fun onCreate(database: SQLiteDatabase) {
        val createTable = (
                "CREATE TABLE IF NOT EXISTS $DatabaseTable"
                        + "("
                        + "$ColumnId INTEGER PRIMARY KEY autoincrement,"
                        + "$ColumnCoinTypeId INTEGER,"
                        + "$ColumnTime BIGINT,"
                        + "$ColumnOpenValue DOUBLE,"
                        + "$ColumnCloseValue DOUBLE,"
                        + "$ColumnLowValue DOUBLE,"
                        + "$ColumnHighValue DOUBLE,"
                        + "$ColumnCurrency INT"
                        + ")")
        database.execSQL(createTable)
        DbCoinTrendActionPublishSubject.instance.publishSubject.onNext(DbAction.Null)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database.execSQL("DROP TABLE IF EXISTS $DatabaseTable")
        onCreate(database)
    }

    override fun onDowngrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(database, oldVersion, newVersion)
    }

    fun add(coinCurrency: CoinTrend): Long {
        val values = ContentValues().apply {
            put(ColumnCoinTypeId, coinCurrency.coinType.id)
            put(ColumnTime, coinCurrency.time)
            put(ColumnOpenValue, coinCurrency.openValue)
            put(ColumnCloseValue, coinCurrency.closeValue)
            put(ColumnLowValue, coinCurrency.lowValue)
            put(ColumnHighValue, coinCurrency.highValue)
            put(ColumnCurrency, coinCurrency.currency.id)
        }

        val database = this.writableDatabase
        val returnValue = database.insert(DatabaseTable, null, values)

        DbCoinTrendActionPublishSubject.instance.publishSubject.onNext(DbAction.Add)
        return returnValue
    }

    fun update(coinCurrency: CoinTrend): Int {
        val values = ContentValues().apply {
            put(ColumnCoinTypeId, coinCurrency.coinType.id)
            put(ColumnTime, coinCurrency.time)
            put(ColumnOpenValue, coinCurrency.openValue)
            put(ColumnCloseValue, coinCurrency.closeValue)
            put(ColumnLowValue, coinCurrency.lowValue)
            put(ColumnHighValue, coinCurrency.highValue)
            put(ColumnCurrency, coinCurrency.currency.id)
        }

        val selection = "$ColumnId LIKE ?"
        val selectionArgs = arrayOf(coinCurrency.id.toString())

        val database = this.writableDatabase
        val returnValue = database.update(DatabaseTable, values, selection, selectionArgs)

        DbCoinTrendActionPublishSubject.instance.publishSubject.onNext(DbAction.Update)
        return returnValue
    }

    fun delete(coinCurrency: CoinTrend): Int {
        val database = this.writableDatabase

        val selection = "$ColumnId LIKE ?"
        val selectionArgs = arrayOf(coinCurrency.id.toString())
        val returnValue = database.delete(DatabaseTable, selection, selectionArgs)

        DbCoinTrendActionPublishSubject.instance.publishSubject.onNext(DbAction.Delete)
        return returnValue
    }

    fun get(): MutableList<CoinTrend> {
        val database = this.readableDatabase

        val projection = arrayOf(
                ColumnId,
                ColumnCoinTypeId,
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
                val coinTypeId = getInt(getColumnIndexOrThrow(ColumnCoinTypeId))
                val time = getLong(getColumnIndexOrThrow(ColumnTime))
                val openValue = getDouble(getColumnIndexOrThrow(ColumnOpenValue))
                val closeValue = getDouble(getColumnIndexOrThrow(ColumnCloseValue))
                val lowValue = getDouble(getColumnIndexOrThrow(ColumnLowValue))
                val highValue = getDouble(getColumnIndexOrThrow(ColumnHighValue))
                val currencyId = getInt(getColumnIndexOrThrow(ColumnCurrency))

                val coinType = CoinType.values()[coinTypeId]
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

        DbCoinTrendActionPublishSubject.instance.publishSubject.onNext(DbAction.Get)
        return list
    }

    fun findByCoinType(coinType: CoinType): MutableList<CoinTrend> {
        val database = this.readableDatabase

        val projection = arrayOf(
                ColumnId,
                ColumnCoinTypeId,
                ColumnTime,
                ColumnOpenValue,
                ColumnCloseValue,
                ColumnLowValue,
                ColumnHighValue,
                ColumnCurrency)

        val selection = "$ColumnCoinTypeId = ?"
        val selectionArgs = arrayOf(coinType.id.toString())

        val sortOrder = "$ColumnId ASC"

        val cursor = database.query(
                DatabaseTable, projection, selection, selectionArgs,
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

        DbCoinTrendActionPublishSubject.instance.publishSubject.onNext(DbAction.Get)
        return list
    }

    companion object {
        private const val DatabaseVersion = 1
        private const val DatabaseName = "guepardoapps-mycoins-coin-trend.db"
        private const val DatabaseTable = "coinCurrencyTable"

        private const val ColumnId = "_id"
        private const val ColumnCoinTypeId = "coinTypeId"
        private const val ColumnTime = "time"
        private const val ColumnOpenValue = "openValue"
        private const val ColumnCloseValue = "closeValue"
        private const val ColumnLowValue = "lowValue"
        private const val ColumnHighValue = "highValue"
        private const val ColumnCurrency = "currency"
    }
}