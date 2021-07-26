package com.bloomidea.inspirers.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.MainActivity;
import com.bloomidea.inspirers.MessagesActivity;
import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.WarriorsActivity;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.events.UserInfoUpdated;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.model.User;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by michellobato on 21/04/17.
 */

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";
    private static final String NOTIFICATION_TAG = "INSPIRERS";
    public static final int NOTIFICATION_ID = 101;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String messageNotif = data.getString("message");
        String extras = data.getString("extra");
        try {


            if(AppController.getmInstance().getActiveUser()!=null){
                APIInspirers.getUserInfo(AppController.getmInstance().getActiveUser().getUid(), null, null, new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            User auxUser = InspirersJSONParser.parseUser2(response.getJSONObject(0),false,null,null,false);

                            if(AppController.getmInstance().getUserDataSource().updateUserNumGodchilds(AppController.getmInstance().getActiveUser().getId(), auxUser.getNumGodChilds())){
                                AppController.getmInstance().getActiveUser().setNumGodChilds(auxUser.getNumGodChilds());

                                AppController.getmInstance().getMyBus().send(new UserInfoUpdated());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }

            JSONObject extrasObj = new JSONObject(extras);

            Log.d(TAG, ""+NOTIFICATION_TAG + "" +"-Message: " + messageNotif);
            Log.d(TAG, ""+NOTIFICATION_TAG + "" +"-EXTRAS: " + extrasObj);

            if(extrasObj.getString("type").equals("message")){
                String uid = extrasObj.getString("uid");
                String messageText = extrasObj.getString("message");

                if (AppController.isActivityVisible()) {
                    Intent localIntent = new Intent(InternalBroadcastReceiver.INTENT_FILTER_ACTION);

                    localIntent.putExtra(InternalBroadcastReceiver.INTENT_EXTRA_TYPE, InternalBroadcastReceiver.TYPE_MESSAGE);
                    localIntent.putExtra(InternalBroadcastReceiver.INTENT_EXTRA_UID, uid);
                    localIntent.putExtra(InternalBroadcastReceiver.INTENT_EXTRA_MESSAGE, messageText);
                    localIntent.putExtra(InternalBroadcastReceiver.INTENT_EXTRA_MESSAGE_NOTIF, messageNotif);


                    // Broadcasts the Intent to receivers in this app.
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                } else {
                    showMessageNotification(this, uid, messageNotif);
                }
            } else if(extrasObj.getString("type").equals("requestgodchild") || extrasObj.getString("type").equals("alreadygodfather")){
                if (AppController.isActivityVisible()) {
                    Intent localIntent = new Intent(InternalBroadcastReceiver.INTENT_FILTER_ACTION);

                    localIntent.putExtra(InternalBroadcastReceiver.INTENT_EXTRA_TYPE, InternalBroadcastReceiver.TYPE_REQUEST_GODCHILD);
                    localIntent.putExtra(InternalBroadcastReceiver.INTENT_EXTRA_MESSAGE_NOTIF, messageNotif);

                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                } else {
                    showRequestGodchildNotification(messageNotif);
                }
            } else if(extrasObj.getString("type").equals("acceptedgodchild") || extrasObj.getString("type").equals("requestgodfather") || extrasObj.getString("type").equals("delgodchild")){
                if (AppController.isActivityVisible()) {
                    Intent localIntent = new Intent(InternalBroadcastReceiver.INTENT_FILTER_ACTION);

                    localIntent.putExtra(InternalBroadcastReceiver.INTENT_EXTRA_TYPE, InternalBroadcastReceiver.TYPE_ACCEPTED_GODCHILD);
                    localIntent.putExtra(InternalBroadcastReceiver.INTENT_EXTRA_MESSAGE_NOTIF, messageNotif);

                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                } else {
                    showAcceptedGodchildNotification(messageNotif);
                }
            } else if(extrasObj.getString("type").equals("removedgodfather") || extrasObj.getString("type").equals("delgodfather") || extrasObj.getString("type").equals("maxgodchild")){
                if (AppController.isActivityVisible()) {
                    Intent localIntent = new Intent(InternalBroadcastReceiver.INTENT_FILTER_ACTION);

                    localIntent.putExtra(InternalBroadcastReceiver.INTENT_EXTRA_TYPE, InternalBroadcastReceiver.TYPE_REMOVED_GODFATHER);
                    localIntent.putExtra(InternalBroadcastReceiver.INTENT_EXTRA_MESSAGE_NOTIF, messageNotif);

                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                } else {
                    showRemovedGodftherNotification(messageNotif, true);
                }
            }else if(extrasObj.getString("type").equals("buzz")){
                if (AppController.isActivityVisible()) {
                    Intent localIntent = new Intent(InternalBroadcastReceiver.INTENT_FILTER_ACTION);

                    localIntent.putExtra(InternalBroadcastReceiver.INTENT_EXTRA_TYPE, InternalBroadcastReceiver.TYPE_BUZZ);
                    localIntent.putExtra(InternalBroadcastReceiver.INTENT_EXTRA_MESSAGE_NOTIF, messageNotif);

                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                } else {
                    showBuzzRecivedNotification(messageNotif);
                }
            }
        } catch (Throwable t) {

        }
    }

    private void showMessageNotification(MyGcmListenerService myGcmListenerService, String uid, final String message) {
        APIInspirers.getUserInfo(uid,null,null, new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {
                User userProfile = null;
                try {
                    userProfile = InspirersJSONParser.parseUser2(response.getJSONObject(0),false,null,null,false);

                    createMessageNotification(userProfile, message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void createMessageNotification(User userProfile, String message){
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String msg = message;
        String title = this.getString(R.string.app_name);

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        Intent messages = new Intent(this, MessagesActivity.class);
        messages.putExtra(MessagesActivity.EXTRA_USER, userProfile);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addNextIntent(resultIntent);
        stackBuilder.addNextIntent(messages);


        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder myNotification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(resultPendingIntent);

        notificationManager.notify(NOTIFICATION_ID, myNotification.build());
    }

    private void showRemovedGodftherNotification(String message, boolean goToPeopleTab){
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String msg = message;
        String title = this.getString(R.string.app_name);

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        if(goToPeopleTab) {
            resultIntent.putExtra(MainActivity.EXTRA_TAB_OPEN, MainActivity.TAB_PEOPLE);
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

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

        notificationManager.notify(NOTIFICATION_ID, myNotification.build());
    }

    private void showAcceptedGodchildNotification(String message){
        showRemovedGodftherNotification(message, true);
    }

    private void showBuzzRecivedNotification(String message){
        showRemovedGodftherNotification(message, false);
    }

    private void showRequestGodchildNotification(String messageNotif){
        if(AppController.getmInstance().getActiveUser()!=null) {
            //if (Utils.canOpenGodchild(AppController.getmInstance().getActiveUser().getLevel(), AppController.getmInstance().getActiveUser().getNumGodChilds())) {

                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                String msg = messageNotif;
                String title = this.getString(R.string.app_name);

                Intent resultIntent = new Intent(this, MainActivity.class);
                resultIntent.setAction(Intent.ACTION_MAIN);
                resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

                Intent warriors = new Intent(this, WarriorsActivity.class);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

                stackBuilder.addNextIntent(resultIntent);
                stackBuilder.addNextIntent(warriors);


                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder myNotification =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.notification_icon)
                                .setContentTitle(title)
                                .setContentText(msg)
                                .setAutoCancel(true)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setContentIntent(resultPendingIntent);

                notificationManager.notify(NOTIFICATION_ID, myNotification.build());
            //}
        }
    }
}
