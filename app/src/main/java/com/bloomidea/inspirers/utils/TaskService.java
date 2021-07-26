package com.bloomidea.inspirers.utils;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bloomidea.inspirers.MainActivity;
import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.application.AppController;

/**
 * Created by michellobato on 21/04/17.
 */

public class TaskService extends IntentService {

    public TaskService() {
        super("TaskService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i("TaskService", "Service running "+ AppController.isActivityVisible());

        int alarmType = intent.getIntExtra(AlarmReciever.ALARM_TYPE,-1);

        if (alarmType == InternalBroadcastReceiver.TYPE_MEDICINE) {
            if (AppController.isActivityVisible()) {
                Intent localIntent = new Intent(InternalBroadcastReceiver.INTENT_FILTER_ACTION);

                localIntent.putExtra(InternalBroadcastReceiver.INTENT_EXTRA_TYPE, InternalBroadcastReceiver.TYPE_MEDICINE);

                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            } else {
                showAlarmMedicine(this, intent);
            }
        }
    }

    private void showAlarmMedicine(final Context context, Intent intentAux){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String msg = context.getString(R.string.medicine_to_take);
        String title = context.getString(R.string.app_name);

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder myNotification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(resultPendingIntent);

        int notifyID = 2;

        notificationManager.notify(notifyID, myNotification.build());
    }

}