package com.example.persiandatepicker.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * this class represents a database with SQLite to store noted dates
 * there is a table that has 3 columns to store date , text and having alarm
 * there are some methods to run queries to insert , delete or run some queries in database
 *
 * @author :zahra fatehi
 * @version :0.0
 */

public class MyDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME =  "My_DB";
    private static final String TABLE_NAME = "dates_note";

    public MyDBHelper(Context context) {
        super(context, DB_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE "+ TABLE_NAME +
                " (date INTEGER PRIMARY KEY, description TEXT NOT NULL, alarm INTEGER DEFAULT 0)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(Long date, String description) {
        SQLiteDatabase db =  this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date" , date);
        contentValues.put("description" , description);
        try {
            Long r = db.insert(TABLE_NAME, null, contentValues);
            return r != -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
     return false;
    }

    /**
     *
     * @param date is the date we wanna be removed in db
     * @return true if deleted successfully
     */
    public boolean deleteData(Long date ){
        SQLiteDatabase db = this.getWritableDatabase();
        int r = db.delete(TABLE_NAME , "date=? " , new String[] {String.valueOf(date)});
        return r != 0;
    }

    /**
     *
     * @param date is the date we wanna be update its description in db
     * @param description is the new description
     * @return true if updated successfully
     */
    public boolean updateData(Long date, String description) {
        SQLiteDatabase db =  this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date" , date);
        contentValues.put("description" , description);
        int r = db.update(TABLE_NAME ,contentValues ,"date=?" ,  new String[] {String.valueOf(date)});
        return r >= 1;
    }

    /**
     *
     * @return all data in db
     */
    public Cursor showAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(" SELECT * FROM "+TABLE_NAME ,null );
    }

    /**
     *
     * @param date is the chosen date
     * @param description is chosen dare description
     * @param hasAlarm it shows there is alarm for this day or not
     * @return true if alarm changed otherwise returns false
     */
    public boolean updateAlarm(Long date,String description , int hasAlarm) {
        SQLiteDatabase db =  this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date" , date);
        contentValues.put("description" , description);
        contentValues.put("alarm" , hasAlarm);
        int r = db.update(TABLE_NAME ,contentValues ,"date=?" ,  new String[] {String.valueOf(date)});
        return r >= 1;
    }

    /**
     *
     * @param chosenDate is the date you chose
     * @return 1 if you had alarm for this day othewise it returns 0
     */
    public int getAlarm(Long chosenDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT alarm FROM "+TABLE_NAME +" WHERE date=? " ,new String[]{String.valueOf(chosenDate)}, null);
            cursor.moveToFirst();
            try {
                return cursor.getInt(cursor.getColumnIndex("alarm"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
    }

    /**
     *
     * @param chosenDate is the date you chose
     * @return the chosen date description
     */
    public String getDescription(Long chosenDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT description FROM "+TABLE_NAME +" WHERE date=? " ,new String[]{String.valueOf(chosenDate)}, null);
        cursor.moveToFirst();
        try {
            return cursor.getString(cursor.getColumnIndex("description"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
