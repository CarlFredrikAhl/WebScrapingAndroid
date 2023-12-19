package com.example.webscraping;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmManagerHelper {
    private static final int ALARM_REQUEST_CODE = 1001;
    public static final long ALARM_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES; //AlarmManager.INTERVAL_HALF_HOUR;

    public static void startAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if(alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), ALARM_INTERVAL, pendingIntent);
        }
    }

    public static void stopAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if(alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
