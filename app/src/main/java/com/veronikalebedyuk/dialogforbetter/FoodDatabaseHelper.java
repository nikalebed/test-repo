package com.veronikalebedyuk.dialogforbetter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class FoodDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "FoodDatabase";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "name";
    public static final String COL_3 = "value";

    public FoodDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + DATABASE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT, " +
                COL_3 + " REAL)";
        db.execSQL(createTable);

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    public void addData(String name, double value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,name);
        contentValues.put(COL_3, value);
        long result = db.insert(DATABASE_NAME,null,contentValues);

    }
    public Integer deleteData(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DATABASE_NAME, "name=?",new String[]{name});


    }
    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DATABASE_NAME;
        Cursor data = db.rawQuery(query,null);
        return data;
    }
}