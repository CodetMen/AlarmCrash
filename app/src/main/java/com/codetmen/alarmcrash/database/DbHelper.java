package com.codetmen.alarmcrash.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static String DATABASE_ALARM = "dbAlarm";
    private static final int DATABASE_VERSION = 1;

    // initial create table
    public static final String CREATE_TABLE_ALARM = String.format("CREATE TABLE %s"
            + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
            " %s TEXT NOT NULL," +
            " %s TEXT NOT NULL,"+
            " %s TEXT NOT NULL,"+
            " %s TEXT NOT NULL)",
            DbContract.TABLE_ALARM,
            DbContract.AlarmColumns._ID,
            DbContract.AlarmColumns.TIME,
            DbContract.AlarmColumns.DATE,
            DbContract.AlarmColumns.TITLE,
            DbContract.AlarmColumns.MESSAGE);

    public DbHelper(Context context) {
        super(context, DATABASE_ALARM, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ALARM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TABLE_ALARM);
    }
}
