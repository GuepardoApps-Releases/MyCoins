package guepardoapps.mycoins.database.coin

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import guepardoapps.mycoins.models.CoinType
import guepardoapps.mycoins.enums.DbAction
import guepardoapps.mycoins.extensions.byString
import guepardoapps.mycoins.models.CoinConversion
import guepardoapps.mycoins.models.CoinTypes
import guepardoapps.mycoins.models.DbPublishSubject
import guepardoapps.mycoins.publishsubject.DbCoinConversionActionPublishSubject
import guepardoapps.mycoins.utils.Logger

// Helpful
// https://developer.android.com/training/data-storage/sqlite
// https://www.techotopia.com/index.php/A_Kotlin_Android_SQLite_Database_Tutorial

internal class DbCoinConversion(context: Context) : SQLiteOpenHelper(context, DatabaseName, null, DatabaseVersion) {
    private val tag: String = DbCoinConversion::class.java.simpleName

    override fun onCreate(database: SQLiteDatabase) {
        val createTable = (
                "CREATE TABLE IF NOT EXISTS $DatabaseTable"
                        + "("
                        + "$ColumnId INTEGER PRIMARY KEY autoincrement,"
                        + "$ColumnCoinType TEXT,"
                        + "$ColumnEurValue DOUBLE,"
                        + "$ColumnUsDollarValue DOUBLE"
                        + ")")
        database.execSQL(createTable)
        DbCoinConversionActionPublishSubject.instance.publishSubject.onNext(DbPublishSubject(DbAction.Null, 1f))
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database.execSQL("DROP TABLE IF EXISTS $DatabaseTable")
        onCreate(database)
    }

    override fun onDowngrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(database, oldVersion, newVersion)
    }

    fun add(coinConversion: CoinConversion, currentActionNo: Float = 1f, totalActions: Float = 1f): Long {
        Logger.instance.debug(tag, "add: $coinConversion")

        val values = ContentValues().apply {
            put(ColumnCoinType, coinConversion.coinType.type)
            put(ColumnEurValue, coinConversion.eurValue)
            put(ColumnUsDollarValue, coinConversion.usDollarValue)
        }

        val database = this.writableDatabase
        val returnValue = database.insert(DatabaseTable, null, values)

        DbCoinConversionActionPublishSubject.instance.publishSubject.onNext(DbPublishSubject(DbAction.Add, currentActionNo / totalActions))
        return returnValue
    }

    fun delete(coinConversion: CoinConversion, currentActionNo: Float = 1f, totalActions: Float = 1f): Int {
        Logger.instance.debug(tag, "delete: $coinConversion")

        val database = this.writableDatabase

        val selection = "$ColumnId LIKE ?"
        val selectionArgs = arrayOf(coinConversion.id.toString())
        val returnValue = database.delete(DatabaseTable, selection, selectionArgs)

        DbCoinConversionActionPublishSubject.instance.publishSubject.onNext(DbPublishSubject(DbAction.Delete, currentActionNo / totalActions))
        return returnValue
    }

    fun get(): MutableList<CoinConversion> {
        Logger.instance.debug(tag, "get")

        val database = this.readableDatabase

        val projection = arrayOf(ColumnId, ColumnCoinType, ColumnEurValue, ColumnUsDollarValue)

        val sortOrder = "$ColumnId ASC"

        val cursor = database.query(
                DatabaseTable, projection, null, null,
                null, null, sortOrder)

        val list = mutableListOf<CoinConversion>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(ColumnId))
                val eurValue = getDouble(getColumnIndexOrThrow(ColumnEurValue))
                val usDollarValue = getDouble(getColumnIndexOrThrow(ColumnUsDollarValue))

                val coinTypeString = getString(getColumnIndexOrThrow(ColumnCoinType))
                val coinType = CoinTypes.values.byString(coinTypeString)
                if (coinType == CoinTypes.Null) coinType.type = coinTypeString

                val coinConversion = CoinConversion()
                coinConversion.id = id
                coinConversion.coinType = coinType
                coinConversion.eurValue = eurValue
                coinConversion.usDollarValue = usDollarValue

                list.add(coinConversion)
            }
        }

        DbCoinConversionActionPublishSubject.instance.publishSubject.onNext(DbPublishSubject(DbAction.Get, 1f))
        return list
    }

    fun findByCoinType(coinType: CoinType): MutableList<CoinConversion> {
        Logger.instance.debug(tag, "findByCoinType: $coinType")

        val database = this.readableDatabase

        val projection = arrayOf(ColumnId, ColumnCoinType, ColumnEurValue, ColumnUsDollarValue)

        val cursor = database.query(
                DatabaseTable, projection,
                "$ColumnCoinType = '${coinType.type}'",
                null, null, null,
                "$ColumnId ASC")

        val list = mutableListOf<CoinConversion>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(ColumnId))
                val eurValue = getDouble(getColumnIndexOrThrow(ColumnEurValue))
                val usDollarValue = getDouble(getColumnIndexOrThrow(ColumnUsDollarValue))

                val coinConversion = CoinConversion()
                coinConversion.id = id
                coinConversion.coinType = coinType
                coinConversion.eurValue = eurValue
                coinConversion.usDollarValue = usDollarValue

                list.add(coinConversion)
            }
        }

        DbCoinConversionActionPublishSubject.instance.publishSubject.onNext(DbPublishSubject(DbAction.Get, 1f))
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
        private const val DatabaseVersion = 2
        private const val DatabaseName = "guepardoapps-mycoins-coin-conversion.db"
        private const val DatabaseTable = "coinTable"

        private const val ColumnId = "_id"
        private const val ColumnCoinType = "coinType"
        private const val ColumnEurValue = "eurValue"
        private const val ColumnUsDollarValue = "usDollarValue"
    }
}