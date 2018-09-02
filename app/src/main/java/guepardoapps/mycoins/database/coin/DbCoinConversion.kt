package guepardoapps.mycoins.database.coin

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import guepardoapps.mycoins.enums.CoinType
import guepardoapps.mycoins.enums.DbAction
import guepardoapps.mycoins.models.CoinConversion
import guepardoapps.mycoins.publishsubject.DbCoinConversionActionPublishSubject

// Helpful
// https://developer.android.com/training/data-storage/sqlite
// https://www.techotopia.com/index.php/A_Kotlin_Android_SQLite_Database_Tutorial

internal class DbCoinConversion(context: Context)
    : SQLiteOpenHelper(context, DatabaseName, null, DatabaseVersion) {

    override fun onCreate(database: SQLiteDatabase) {
        val createTable = (
                "CREATE TABLE IF NOT EXISTS $DatabaseTable"
                        + "("
                        + "$ColumnId INTEGER PRIMARY KEY autoincrement,"
                        + "$ColumnCoinTypeId INTEGER,"
                        + "$ColumnEurValue DOUBLE,"
                        + "$ColumnUsDollarValue DOUBLE"
                        + ")")
        database.execSQL(createTable)
        DbCoinConversionActionPublishSubject.instance.publishSubject.onNext(DbAction.Null)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database.execSQL("DROP TABLE IF EXISTS $DatabaseTable")
        onCreate(database)
    }

    override fun onDowngrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(database, oldVersion, newVersion)
    }

    fun add(coinConversion: CoinConversion): Long {
        val values = ContentValues().apply {
            put(ColumnCoinTypeId, coinConversion.coinType.id)
            put(ColumnEurValue, coinConversion.eurValue)
            put(ColumnUsDollarValue, coinConversion.usDollarValue)
        }

        val database = this.writableDatabase
        val returnValue = database.insert(DatabaseTable, null, values)

        DbCoinConversionActionPublishSubject.instance.publishSubject.onNext(DbAction.Add)
        return returnValue
    }

    fun update(coinConversion: CoinConversion): Int {
        val values = ContentValues().apply {
            put(ColumnCoinTypeId, coinConversion.coinType.id)
            put(ColumnEurValue, coinConversion.eurValue)
            put(ColumnUsDollarValue, coinConversion.usDollarValue)
        }

        val selection = "$ColumnId LIKE ?"
        val selectionArgs = arrayOf(coinConversion.id.toString())

        val database = this.writableDatabase
        val returnValue = database.update(DatabaseTable, values, selection, selectionArgs)

        DbCoinConversionActionPublishSubject.instance.publishSubject.onNext(DbAction.Update)
        return returnValue
    }

    fun delete(coinConversion: CoinConversion): Int {
        val database = this.writableDatabase

        val selection = "$ColumnId LIKE ?"
        val selectionArgs = arrayOf(coinConversion.id.toString())
        val returnValue = database.delete(DatabaseTable, selection, selectionArgs)

        DbCoinConversionActionPublishSubject.instance.publishSubject.onNext(DbAction.Delete)
        return returnValue
    }

    fun get(): MutableList<CoinConversion> {
        val database = this.readableDatabase

        val projection = arrayOf(ColumnId, ColumnCoinTypeId, ColumnEurValue, ColumnUsDollarValue)

        val sortOrder = "$ColumnId ASC"

        val cursor = database.query(
                DatabaseTable, projection, null, null,
                null, null, sortOrder)

        val list = mutableListOf<CoinConversion>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(ColumnId))
                val coinTypeId = getInt(getColumnIndexOrThrow(ColumnCoinTypeId))
                val eurValue = getDouble(getColumnIndexOrThrow(ColumnEurValue))
                val usDollarValue = getDouble(getColumnIndexOrThrow(ColumnUsDollarValue))
                val coinType = CoinType.values()[coinTypeId]

                val coinConversion = CoinConversion()
                coinConversion.id = id
                coinConversion.coinType = coinType
                coinConversion.eurValue = eurValue
                coinConversion.usDollarValue = usDollarValue

                list.add(coinConversion)
            }
        }

        DbCoinConversionActionPublishSubject.instance.publishSubject.onNext(DbAction.Get)
        return list
    }

    fun findByCoinType(coinType: CoinType): MutableList<CoinConversion> {
        val database = this.readableDatabase

        val projection = arrayOf(ColumnId, ColumnCoinTypeId, ColumnEurValue, ColumnUsDollarValue)

        val selection = "$ColumnCoinTypeId = ?"
        val selectionArgs = arrayOf(coinType.id.toString())

        val sortOrder = "$ColumnId ASC"

        val cursor = database.query(
                DatabaseTable, projection, selection, selectionArgs,
                null, null, sortOrder)

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

        DbCoinConversionActionPublishSubject.instance.publishSubject.onNext(DbAction.Get)
        return list
    }

    companion object {
        private const val DatabaseVersion = 1
        private const val DatabaseName = "guepardoapps-mycoins-coin-conversion.db"
        private const val DatabaseTable = "coinTable"

        private const val ColumnId = "_id"
        private const val ColumnCoinTypeId = "coinTypeId"
        private const val ColumnEurValue = "eurValue"
        private const val ColumnUsDollarValue = "usDollarValue"
    }
}