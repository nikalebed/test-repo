package com.veronikalebedyuk.dialogforbetter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "StatisticsDatabase";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "date";
    public static final String COL_3 = "value";
    public static final String COL_4 = "criteria";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + DATABASE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " INTEGER, " +
                COL_3 + " REAL, " +
                COL_4 + " TEXT)";
        db.execSQL(createTable);

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    public void addData(double value, String s){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, System.currentTimeMillis());
        contentValues.put(COL_3, value);
        contentValues.put(COL_4,s);
        long result = db.insert(DATABASE_NAME,null,contentValues);
    }
    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DATABASE_NAME;
        Cursor data = db.rawQuery(query,null);
        return data;
    }

}