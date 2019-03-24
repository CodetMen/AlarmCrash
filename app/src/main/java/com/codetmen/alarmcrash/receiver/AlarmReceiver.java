package com.codetmen.alarmcrash.receiver;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.codetmen.alarmcrash.ActionAlarmActivity;
import com.codetmen.alarmcrash.R;
import com.codetmen.alarmcrash.database.AlarmHelper;
import com.codetmen.alarmcrash.model.Alarm;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    public final static String EXTRA_MESSAGE = "ExtraMessage";
    public final static String EXTRA_TITLE = "ExtraTitle";
    public final static String EXTRA_DATE = "ExtraDate";
    public final static String EXTRA_TIME = "ExtraTime";

    private final String NOTIF_CHANNEL_ID = "Channel_ID_101";

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // get data from intent
        String message = intent.getStringExtra(AlarmReceiver.EXTRA_MESSAGE);
        String title = intent.getStringExtra(AlarmReceiver.EXTRA_TITLE);
        String date = intent.getStringExtra(AlarmReceiver.EXTRA_DATE);
        String time = intent.getStringExtra(AlarmReceiver.EXTRA_TIME);

        shwoAlarmNotification(context, title);
        showActionAlarm(context, title, message, date, time);
    }

    // Direct to another activity which display popup dialog and play music
    // ToDo: still missing data like message, sound, read message and repeat
    private void showActionAlarm(Context context, String title, String message, String date, String time) {
        Intent intentAlarm = new Intent(context, ActionAlarmActivity.class);
        intentAlarm.putExtra(ActionAlarmActivity.EXTRA_TITLE, title);
        intentAlarm.putExtra(ActionAlarmActivity.EXTRA_MESSAGE, message);
        intentAlarm.putExtra(ActionAlarmActivity.EXTRA_DATE, date);
        intentAlarm.putExtra(ActionAlarmActivity.EXTRA_TIME, time);
        intentAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentAlarm);
    }

    // ToDO: change sound alarm by sending uri from AlarmAdapter class
     private void shwoAlarmNotification(Context context, String title){
        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builderNotif = new NotificationCompat.Builder(context, NOTIF_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(title)
                .setColor(context.getResources().getColor(R.color.color2))
                .setVibrate(new long[]{1000, 1000, 1000,1000, 1000});

        notificationManagerCompat.notify(101, builderNotif.build());
    }

    // method set the trigger alarm
    public void setAlarmSchedule(Context context, String date, String time, String title , String message, int reqCodeAlarm){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.EXTRA_MESSAGE, message);
        intent.putExtra(AlarmReceiver.EXTRA_TITLE, title);
        intent.putExtra(AlarmReceiver.EXTRA_DATE, date);
        intent.putExtra(AlarmReceiver.EXTRA_TIME, time);

        String dateArray[] = date.split("/");
        String timeArray[] = time.split(":");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[2]));
        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1])-1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[0]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
        calendar.set(Calendar.SECOND, 0);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reqCodeAlarm, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 3600, pendingIntent);
    }


}