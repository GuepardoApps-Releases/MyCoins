package guepardoapps.mycoins.database.coin

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import guepardoapps.mycoins.enums.CoinType
import guepardoapps.mycoins.enums.DbAction
import guepardoapps.mycoins.models.Coin
import guepardoapps.mycoins.publishsubject.DbCoinActionPublishSubject

// Helpful
// https://developer.android.com/training/data-storage/sqlite
// https://www.techotopia.com/index.php/A_Kotlin_Android_SQLite_Database_Tutorial

internal class DbCoin(context: Context)
    : SQLiteOpenHelper(context, DatabaseName, null, DatabaseVersion) {

    override fun onCreate(database: SQLiteDatabase) {
        val createTable = (
                "CREATE TABLE IF NOT EXISTS $DatabaseTable"
                        + "("
                        + "$ColumnId INTEGER PRIMARY KEY autoincrement,"
                        + "$ColumnCoinTypeId INTEGER,"
                        + "$ColumnAmount DOUBLE"
                        + ")")
        database.execSQL(createTable)
        DbCoinActionPublishSubject.instance.publishSubject.onNext(DbAction.Null)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database.execSQL("DROP TABLE IF EXISTS $DatabaseTable")
        onCreate(database)
    }

    override fun onDowngrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(database, oldVersion, newVersion)
    }

    fun add(coin: Coin): Long {
        val values = ContentValues().apply {
            put(ColumnCoinTypeId, coin.coinType.id)
            put(ColumnAmount, coin.amount)
        }

        val database = this.writableDatabase
        val returnValue = database.insert(DatabaseTable, null, values)

        DbCoinActionPublishSubject.instance.publishSubject.onNext(DbAction.Add)
        return returnValue
    }

    fun update(coin: Coin): Int {
        val values = ContentValues().apply {
            put(ColumnCoinTypeId, coin.coinType.id)
            put(ColumnAmount, coin.amount)
        }

        val selection = "$ColumnId LIKE ?"
        val selectionArgs = arrayOf(coin.id.toString())

        val database = this.writableDatabase
        val returnValue = database.update(DatabaseTable, values, selection, selectionArgs)

        DbCoinActionPublishSubject.instance.publishSubject.onNext(DbAction.Update)
        return returnValue
    }

    fun delete(coin: Coin): Int {
        val database = this.writableDatabase

        val selection = "$ColumnId LIKE ?"
        val selectionArgs = arrayOf(coin.id.toString())
        val returnValue = database.delete(DatabaseTable, selection, selectionArgs)

        DbCoinActionPublishSubject.instance.publishSubject.onNext(DbAction.Delete)
        return returnValue
    }

    fun get(): MutableList<Coin> {
        val database = this.readableDatabase

        val projection = arrayOf(ColumnId, ColumnCoinTypeId, ColumnAmount)

        val sortOrder = "$ColumnId ASC"

        val cursor = database.query(
                DatabaseTable, projection, null, null,
                null, null, sortOrder)

        val list = mutableListOf<Coin>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(ColumnId))
                val coinTypeId = getInt(getColumnIndexOrThrow(ColumnCoinTypeId))
                val amount = getDouble(getColumnIndexOrThrow(ColumnAmount))
                val coinType = CoinType.values()[coinTypeId]

                list.add(Coin(id, coinType, amount))
            }
        }

        DbCoinActionPublishSubject.instance.publishSubject.onNext(DbAction.Get)
        return list
    }

    fun findById(id: Int): MutableList<Coin> {
        val database = this.readableDatabase

        val projection = arrayOf(ColumnId, ColumnCoinTypeId, ColumnAmount)

        val selection = "$ColumnId = ?"
        val selectionArgs = arrayOf(id.toString())

        val sortOrder = "$ColumnId ASC"

        val cursor = database.query(
                DatabaseTable, projection, selection, selectionArgs,
                null, null, sortOrder)

        val list = mutableListOf<Coin>()
        with(cursor) {
            while (moveToNext()) {
                val coinTypeId = getInt(getColumnIndexOrThrow(ColumnCoinTypeId))
                val amount = getDouble(getColumnIndexOrThrow(ColumnAmount))
                val coinType = CoinType.values()[coinTypeId]

                list.add(Coin(id, coinType, amount))
            }
        }

        DbCoinActionPublishSubject.instance.publishSubject.onNext(DbAction.Get)
        return list
    }

    companion object {
        private const val DatabaseVersion = 1
        private const val DatabaseName = "guepardoapps-mycoins-coin.db"
        private const val DatabaseTable = "coinTable"

        private const val ColumnId = "_id"
        private const val ColumnCoinTypeId = "coinTypeId"
        private const val ColumnAmount = "amount"
    }
}