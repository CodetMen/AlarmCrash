package com.codetmen.alarmcrash.database;

import android.provider.BaseColumns;

public class DbContract {

    public static String TABLE_ALARM = "table_alarm";

    public static final class AlarmColumns implements BaseColumns {
        public static String TIME = "time";
        public static String DATE = "date";
        public static String TITLE = "title";
        public static String MESSAGE = "message";
    }
}
