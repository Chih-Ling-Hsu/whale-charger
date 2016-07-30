package com.netdb.nthu.whalecharger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * Created by user on 2016/7/30.
 */
public class AlarmInitReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("AlarmSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        boolean alarmMode= prefs.getBoolean("alarmMode", true);
        int alarmHour= prefs.getInt("alarmHour", 22);
        int alarmMin= prefs.getInt("alarmMin", 0);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent anIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, anIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(alarmMode==true) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
            calendar.set(Calendar.MINUTE, alarmMin);
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
        }
    }
}
