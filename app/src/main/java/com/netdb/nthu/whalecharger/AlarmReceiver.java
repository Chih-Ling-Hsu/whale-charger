package com.netdb.nthu.whalecharger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

/**
 * Created by user on 2016/7/29.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("AlarmSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        boolean alarmVibrate= prefs.getBoolean("alarmVibrate", true);
        if(alarmVibrate==true) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        }


        WakeLocker.acquire(context);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notify)
                        .setContentTitle("WhaleCharger")
                        .setContentText("Time to keep your accounts!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int mId=0;
        mNotificationManager.notify(mId, mBuilder.build());

        WakeLocker.release();
    }
}