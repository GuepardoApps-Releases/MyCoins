package guepardoapps.mycoins.database.coin

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import guepardoapps.mycoins.enums.DbAction
import guepardoapps.mycoins.extensions.byString
import guepardoapps.mycoins.models.Coin
import guepardoapps.mycoins.models.CoinTypes
import guepardoapps.mycoins.models.DbPublishSubject
import guepardoapps.mycoins.publishsubject.DbCoinActionPublishSubject
import guepardoapps.mycoins.utils.Logger

// Helpful
// https://developer.android.com/training/data-storage/sqlite
// https://www.techotopia.com/index.php/A_Kotlin_Android_SQLite_Database_Tutorial

internal class DbCoin(context: Context) : SQLiteOpenHelper(context, DatabaseName, null, DatabaseVersion) {
    private val tag: String = DbCoin::class.java.simpleName

    override fun onCreate(database: SQLiteDatabase) {
        val createTable = (
                "CREATE TABLE IF NOT EXISTS $DatabaseTable"
                        + "("
                        + "$ColumnId TEXT PRIMARY KEY,"
                        + "$ColumnCoinType TEXT,"
                        + "$ColumnAmount DOUBLE,"
                        + "$ColumnAdditionalInformation TEXT"
                        + ")")
        database.execSQL(createTable)
        DbCoinActionPublishSubject.instance.publishSubject.onNext(DbPublishSubject(DbAction.Null, 1f))
    }

    override fun onDowngrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) = onUpgrade(database, oldVersion, newVersion)

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database.execSQL("DROP TABLE IF EXISTS $DatabaseTable")
        onCreate(database)
    }

    fun add(coin: Coin, currentActionNo: Float = 1f, totalActions: Float = 1f): Long {
        Logger.instance.debug(tag, "add: $coin")

        val values = ContentValues().apply {
            put(ColumnId, coin.id)
            put(ColumnCoinType, coin.coinType.type)
            put(ColumnAmount, coin.amount)
            put(ColumnAdditionalInformation, coin.additionalInformation)
        }

        val returnValue = this.writableDatabase.insert(DatabaseTable, null, values)
        DbCoinActionPublishSubject.instance.publishSubject.onNext(DbPublishSubject(DbAction.Add, currentActionNo / totalActions))
        return returnValue
    }

    fun delete(coin: Coin, currentActionNo: Float = 1f, totalActions: Float = 1f): Int {
        Logger.instance.debug(tag, "delete: $coin")

        val returnValue = this.writableDatabase.delete(DatabaseTable, "$ColumnId LIKE ?", arrayOf(coin.id))
        DbCoinActionPublishSubject.instance.publishSubject.onNext(DbPublishSubject(DbAction.Delete, currentActionNo / totalActions))
        return returnValue
    }

    fun findById(id: String): MutableList<Coin> {
        Logger.instance.debug(tag, "findById: $id")

        val cursor = this.readableDatabase.query(DatabaseTable,
                arrayOf(ColumnId, ColumnCoinType, ColumnAmount, ColumnAdditionalInformation),
                "$ColumnId = '$id'", null, null, null, "$ColumnId ASC")

        val list = mutableListOf<Coin>()
        with(cursor) {
            while (moveToNext()) {
                val amount = getDouble(getColumnIndexOrThrow(ColumnAmount))
                val additionalInformation = getString(getColumnIndexOrThrow(ColumnAdditionalInformation))

                val coinTypeString = getString(getColumnIndexOrThrow(ColumnCoinType))
                val coinType = CoinTypes.values.byString(coinTypeString)
                if (coinType == CoinTypes.Null) coinType.type = coinTypeString

                list.add(Coin(id = id, coinType = coinType, amount = amount, additionalInformation = additionalInformation))
            }
        }

        DbCoinActionPublishSubject.instance.publishSubject.onNext(DbPublishSubject(DbAction.Get, 1f))
        return list
    }

    fun get(): MutableList<Coin> {
        Logger.instance.debug(tag, "get")

        val cursor = this.readableDatabase.query(DatabaseTable,
                arrayOf(ColumnId, ColumnCoinType, ColumnAmount, ColumnAdditionalInformation),
                null, null, null, null, "$ColumnId ASC")

        val list = mutableListOf<Coin>()
        with(cursor) {
            while (moveToNext()) {
                val id = getString(getColumnIndexOrThrow(ColumnId))
                val amount = getDouble(getColumnIndexOrThrow(ColumnAmount))

                val coinTypeString = getString(getColumnIndexOrThrow(ColumnCoinType))
                val coinType = CoinTypes.values.byString(coinTypeString)
                if (coinType == CoinTypes.Null) coinType.type = coinTypeString

                val additionalInformation = getString(getColumnIndexOrThrow(ColumnAdditionalInformation))

                list.add(Coin(id = id, coinType = coinType, amount = amount, additionalInformation = additionalInformation))
            }
        }

        DbCoinActionPublishSubject.instance.publishSubject.onNext(DbPublishSubject(DbAction.Get, 1f))
        return list
    }

    fun update(coin: Coin, currentActionNo: Float = 1f, totalActions: Float = 1f): Int {
        Logger.instance.debug(tag, "update: $coin")

        val values = ContentValues().apply {
            put(ColumnCoinType, coin.coinType.type)
            put(ColumnAmount, coin.amount)
            put(ColumnAdditionalInformation, coin.additionalInformation)
        }

        val returnValue = this.writableDatabase.update(DatabaseTable, values, "$ColumnId LIKE ?", arrayOf(coin.id))
        DbCoinActionPublishSubject.instance.publishSubject.onNext(DbPublishSubject(DbAction.Update, currentActionNo / totalActions))
        return returnValue
    }

    companion object {
        private const val DatabaseVersion = 1
        private const val DatabaseName = "guepardoapps-mycoins-coin-2.db"
        private const val DatabaseTable = "coinTable"

        private const val ColumnId = "id"
        private const val ColumnCoinType = "coinType"
        private const val ColumnAmount = "amount"
        private const val ColumnAdditionalInformation = "additionalInformation"
    }
}