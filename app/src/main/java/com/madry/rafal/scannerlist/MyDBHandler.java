package com.madry.rafal.scannerlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//creates and handles two databases - main one that fills the MainActivity and the second one which is archive
public class MyDBHandler extends SQLiteOpenHelper {
    private static final String TAG = "DBhelper";

    MainActivity link = new MainActivity();
    //all names and versions are initialised here for maintenance purposes
    private static final String DATABASE_NAME = "Items.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_ITEMS = "items";
    public static final String TABLE_ARCHIVE = "archive";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ITEM = "Product";
    public static final String COLUMN_CODE = "barcode";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query1 = "CREATE TABLE " +
                TABLE_ITEMS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM + " TEXT)";

        String query2 = "CREATE TABLE " +
                TABLE_ARCHIVE + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM + " TEXT, " +
                COLUMN_CODE + " TEXT)";
        db.execSQL(query1);
        Log.d(TAG, "utworzylo pierwsza tabele");
        db.execSQL(query2);
        Log.d(TAG, "utworzylo druga tabele");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARCHIVE);
        onCreate(db);
    }


    public void addItem(String barcode, int rows) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ITEM + " FROM " + TABLE_ARCHIVE + " WHERE " + COLUMN_CODE + " = '" + barcode + "'", null);
        String cc = cursor.getColumnName(0);
        Log.d(TAG, "pobralo kursor " + cc);
        cursor.moveToFirst();
        String item = cursor.getString(0);
        Log.d(TAG, "przechwycilo tekst");
        cv.put(COLUMN_ITEM, item);
        db.insert(TABLE_ITEMS, null, cv);
        Log.d(TAG, "wsadzilo do bazy");
        db.close();



    }

    public void addNewItem (String item, String barcode){
        SQLiteDatabase db = getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ITEM, item);
        db.insert(TABLE_ITEMS, null, cv);
        cv.put(COLUMN_CODE, barcode);
        db.insert(TABLE_ARCHIVE, null, cv);
        Log.d(TAG, "dodalo do bazy");
        db.close();

    }

    public void deleteItem(String i) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ITEMS + " WHERE " + COLUMN_ITEM  + " = '" + i + "'");
        db.close();
    }

    public Cursor getCursor(){

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS, null);
        return cursor;
    }
    public Cursor getCursorA(){

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ARCHIVE, null);
        return cursor;
    }
    public int checkRows(String barcode) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT EXISTS (SELECT * FROM " + TABLE_ARCHIVE + " WHERE " + COLUMN_CODE + " = '" + barcode + "')", null);
        cursor.moveToFirst();
        int rows = cursor.getInt(0);

        return rows;
    }
}
