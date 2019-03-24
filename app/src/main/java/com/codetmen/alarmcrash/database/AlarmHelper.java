package com.codetmen.alarmcrash.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.codetmen.alarmcrash.model.Alarm;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;

public class AlarmHelper {

    private static String DATABASE_TABLE = DbContract.TABLE_ALARM;
    private Context context;
    private DbHelper dbHelper;
    private SQLiteDatabase database;

    public AlarmHelper(Context context) {
        this.context = context;
    }

    // open and read database
    public AlarmHelper open() throws SQLException {
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    // close database
    public void close(){
        dbHelper.close();
    }

    // capture all existing alarm
    // automitically parsed into alarm model
    // return query result is an array of alarm models
    public ArrayList<Alarm> query() {
        ArrayList<Alarm> arrayList = new ArrayList<Alarm>();
        Cursor cursor = database.query(DATABASE_TABLE, null, null, null, null, null,null);
        cursor.moveToFirst();
        Alarm alarm;
        if (cursor.getCount() > 0){
            do {
                alarm = new Alarm();
                alarm.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                alarm.setTime(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.AlarmColumns.TIME)));
                alarm.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.AlarmColumns.DATE)));
                alarm.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.AlarmColumns.TITLE)));
                alarm.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.AlarmColumns.MESSAGE)));

                arrayList.add(alarm);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    // insert to database
    public long insert(Alarm alarm){
        ContentValues initialValues = new ContentValues();
        initialValues.put(DbContract.AlarmColumns.TIME, alarm.getTime());
        initialValues.put(DbContract.AlarmColumns.DATE, alarm.getDate());
        initialValues.put(DbContract.AlarmColumns.TITLE, alarm.getTitle());
        initialValues.put(DbContract.AlarmColumns.MESSAGE, alarm.getMessage());
        return database.insert(DATABASE_TABLE, null, initialValues);
    }

    // update database
    public int update(Alarm alarm){
        ContentValues initialValues = new ContentValues();
        initialValues.put(DbContract.AlarmColumns.TIME, alarm.getTime());
        initialValues.put(DbContract.AlarmColumns.DATE, alarm.getDate());
        initialValues.put(DbContract.AlarmColumns.TITLE, alarm.getTitle());
        initialValues.put(DbContract.AlarmColumns.MESSAGE, alarm.getMessage());
        return database.update(DATABASE_TABLE, initialValues, _ID +"= '"+alarm.getId()+"'", null);
    }

    // delete database
    public int delete(int id){
        return database.delete(DbContract.TABLE_ALARM, _ID +" = '"+id+"'", null);
    }
}