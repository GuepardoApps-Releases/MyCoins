package guepardoapps.mycoins.common.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import guepardoapps.mycoins.R;
import guepardoapps.mycoins.basic.dto.CoinDto;
import guepardoapps.mycoins.basic.utils.Logger;

public class DatabaseCoinList {
    private static final String TAG = DatabaseCoinList.class.getSimpleName();
    private Logger _logger;

    private static final String KEY_ROW_ID = "_id";
    private static final String KEY_TYPE = "_type";
    private static final String KEY_AMOUNT = "_amount";
    private static final String KEY_EURO_CONVERSION = "_euroConversion";
    private static final String KEY_USD_CONVERSION = "_usdConversion";

    private static final String DATABASE_NAME = "DatabaseCoinListDb";
    private static final String DATABASE_TABLE = "DatabaseCoinListTable";
    private static final int DATABASE_VERSION = 1;

    private DatabaseHelper _databaseHelper;
    private final Context _context;
    private SQLiteDatabase _database;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        private DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(" CREATE TABLE " + DATABASE_TABLE + " ( "
                    + KEY_ROW_ID + " TEXT NOT NULL, "
                    + KEY_TYPE + " TEXT NOT NULL, "
                    + KEY_AMOUNT + " TEXT NOT NULL, "
                    + KEY_EURO_CONVERSION + " TEXT NOT NULL, "
                    + KEY_USD_CONVERSION + " TEXT NOT NULL); ");
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            database.execSQL(" DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(database);
        }
    }

    public DatabaseCoinList(@NonNull Context context) {
        _logger = new Logger(TAG);
        _context = context;
    }

    public DatabaseCoinList Open() throws SQLException {
        _databaseHelper = new DatabaseHelper(_context);
        _database = _databaseHelper.getWritableDatabase();
        return this;
    }

    public void Close() {
        _databaseHelper.close();
    }

    public long CreateEntry(@NonNull CoinDto newEntry) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_ROW_ID, newEntry.GetId());
        contentValues.put(KEY_TYPE, newEntry.GetType());
        contentValues.put(KEY_AMOUNT, String.valueOf(newEntry.GetAmount()));
        contentValues.put(KEY_EURO_CONVERSION, String.valueOf(newEntry.GetEuroConversion()));
        contentValues.put(KEY_USD_CONVERSION, String.valueOf(newEntry.GetUsDollarConversion()));

        return _database.insert(DATABASE_TABLE, null, contentValues);
    }

    public long UpdateEntry(@NonNull CoinDto updateEntry) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_TYPE, updateEntry.GetType());
        contentValues.put(KEY_AMOUNT, String.valueOf(updateEntry.GetAmount()));
        contentValues.put(KEY_EURO_CONVERSION, String.valueOf(updateEntry.GetEuroConversion()));
        contentValues.put(KEY_USD_CONVERSION, String.valueOf(updateEntry.GetUsDollarConversion()));

        return _database.update(DATABASE_TABLE, contentValues, KEY_ROW_ID + "=" + updateEntry.GetId(), null);
    }

    public ArrayList<CoinDto> GetCoinList() {
        String[] columns = new String[]{KEY_ROW_ID, KEY_TYPE, KEY_AMOUNT, KEY_EURO_CONVERSION, KEY_USD_CONVERSION};

        Cursor cursor = _database.query(DATABASE_TABLE, columns, null, null, null, null, null);
        ArrayList<CoinDto> result = new ArrayList<>();

        int idIndex = cursor.getColumnIndex(KEY_ROW_ID);
        int typeIndex = cursor.getColumnIndex(KEY_TYPE);
        int amountIndex = cursor.getColumnIndex(KEY_AMOUNT);
        int euroConversionIndex = cursor.getColumnIndex(KEY_EURO_CONVERSION);
        int usdConversionIndex = cursor.getColumnIndex(KEY_USD_CONVERSION);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String idString = cursor.getString(idIndex);
            String type = cursor.getString(typeIndex);
            String amountString = cursor.getString(amountIndex);
            String euroConversionString = cursor.getString(euroConversionIndex);
            String usdConversionString = cursor.getString(usdConversionIndex);

            int id = -1;
            double amount = 0;
            double euroConversion = 0;
            double usdConversion = 0;

            try {
                id = Integer.parseInt(idString);
                amount = Double.parseDouble(amountString);
                euroConversion = Double.parseDouble(euroConversionString);
                usdConversion = Double.parseDouble(usdConversionString);
            } catch (Exception ex) {
                _logger.Error(ex.toString());
            }

            int icon;
            switch (type) {
                case "BCH":
                    icon = R.drawable.bch;
                    break;
                case "BTC":
                    icon = R.drawable.btc;
                    break;
                case "DASH":
                    icon = R.drawable.dash;
                    break;
                case "ETC":
                    icon = R.drawable.etc;
                    break;
                case "ETH":
                    icon = R.drawable.eth;
                    break;
                case "LTC":
                    icon = R.drawable.ltc;
                    break;
                case "XMR":
                    icon = R.drawable.xmr;
                    break;
                case "ZEC":
                    icon = R.drawable.zec;
                    break;
                default:
                    icon = R.drawable.btc;
            }

            CoinDto entry = new CoinDto(id, type, amount, euroConversion, CoinDto.Aggregation.NULL, usdConversion, CoinDto.Aggregation.NULL, icon);
            result.add(entry);
        }

        cursor.close();

        return result;
    }

    public void Delete(@NonNull CoinDto deleteEntry) throws SQLException {
        _database.delete(DATABASE_TABLE, KEY_ROW_ID + "=" + deleteEntry.GetId(), null);
    }
}
