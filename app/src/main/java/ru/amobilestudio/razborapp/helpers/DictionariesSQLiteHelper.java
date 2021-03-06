package ru.amobilestudio.razborapp.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

import ru.amobilestudio.razborapp.app.R;

/**
 * Created by vetal on 21.05.14.
 */
public class DictionariesSQLiteHelper extends SQLiteOpenHelper {

    private Context _context;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Dictionaries.db";

    public static final String COLUMN_ID_VALUE = "id";
    public static final String COLUMN_NAME_VALUE = "name";

    private static final String INT_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";

    public static final String TABLE_NAME_CATEGORIES = "categories";
    public static final String TABLE_NAME_CAR_MODELS = "car_models";
    public static final String TABLE_NAME_LOCATIONS = "locations";
    public static final String TABLE_NAME_SUPPLIERS = "suppliers";
    public static final String TABLE_NAME_BU_CARS = "bu_cars";

    public static final String[] TABLE_NAMES = new String[]{
            TABLE_NAME_CATEGORIES,
            TABLE_NAME_CAR_MODELS,
            TABLE_NAME_LOCATIONS,
            TABLE_NAME_SUPPLIERS,
            TABLE_NAME_BU_CARS
    };

    public DictionariesSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        _context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        for (String s : TABLE_NAMES){
            StringBuilder sb = new StringBuilder();

            sb.append("CREATE TABLE " + s + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_ID_VALUE + INT_TYPE + ", " +
                    COLUMN_NAME_VALUE + TEXT_TYPE +
            " ); ");
            db.execSQL(sb.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        for (String s : TABLE_NAMES){
            StringBuilder sb = new StringBuilder();
            sb.append(" DROP TABLE IF EXISTS " + s + ";");
            db.execSQL(sb.toString());
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ArrayList<Item> getAll(String tableName, boolean withEmpty){
        ArrayList<Item> items = new ArrayList<Item>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + tableName, null);

        if(c.moveToFirst()){
            if(withEmpty)
                items.add(new Item(0, _context.getString(R.string.empty_item)));
            while (!c.isAfterLast()){
                Item item = new Item(
                        c.getInt(c.getColumnIndex(COLUMN_ID_VALUE)),
                        c.getString(c.getColumnIndex(COLUMN_NAME_VALUE))
                );
                items.add(item);
                c.moveToNext();
            }
        }
        return items;
    }

    public int getPositionById(int id, String tableName){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + tableName, null);

        int position = -1;
        if(c.moveToFirst())
            while(!c.isAfterLast()){
                if(c.getInt(c.getColumnIndex(COLUMN_ID_VALUE)) == id){
                    position = c.getPosition();
                    break;
                }
                c.moveToNext();
            }

        return position;
    }

    public String getValueById(int id, String tableName){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.query(tableName, new String[]{COLUMN_NAME_VALUE}, COLUMN_ID_VALUE + "=?",
                new String[]{id + ""}, null, null, null, null);

        String result = "";

        if(id == 0) return result;

        if(c.moveToFirst())
            result = c.getString(c.getColumnIndex(COLUMN_NAME_VALUE));

        return result;
    }

    public int getIdByValue(String name, String tableName){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.query(tableName, new String[] {COLUMN_ID_VALUE}, COLUMN_NAME_VALUE + "=?",
                new String[] { name}, null, null, null, null);

        int result = 0;
        if(c.moveToFirst())
            result = c.getInt(c.getColumnIndex(COLUMN_ID_VALUE));

        return result;
    }

    public static final class Item{
        private int _id;
        private String _value;

        public Item(int id, String value){
            _id = id;
            _value = value;
        }

        public int getId() {
            return _id;
        }

        public String getValue() {
            return _value;
        }

        @Override
        public String toString() {
            return _value;
        }
    }
}
