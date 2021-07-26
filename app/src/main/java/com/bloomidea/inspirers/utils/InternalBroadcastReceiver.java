package com.bloomidea.inspirers.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bloomidea.inspirers.listener.InternalBroadcastListener;

/**
 * Created by michellobato on 21/04/17.
 */

public class InternalBroadcastReceiver extends BroadcastReceiver {
    public static final int TYPE_MESSAGE = 1;
    public static final int TYPE_MEDICINE = 2;
    public static final int TYPE_REMOVED_GODFATHER = 3;
    public static final int TYPE_REQUEST_GODCHILD = 4;
    public static final int TYPE_ACCEPTED_GODCHILD = 5;
    public static final int TYPE_BUZZ = 6;

    public static final String INTENT_FILTER_ACTION = "coolFriendFilter";
    public static final String INTENT_EXTRA_MESSAGE = "INTENT_EXTRA_MESSAGE";
    public static final String INTENT_EXTRA_TYPE = "INTENT_EXTRA_TYPE";
    public static final String INTENT_EXTRA_UID = "INTENT_EXTRA_UID";
    public static final String INTENT_EXTRA_MESSAGE_NOTIF = "INTENT_EXTRA_MESSAGE_NOTIF";

    private InternalBroadcastListener listener;

    public InternalBroadcastReceiver(InternalBroadcastListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        int type = intent.getIntExtra(INTENT_EXTRA_TYPE, -1);

        if (type == TYPE_MESSAGE) {
            String msg = intent.getStringExtra(INTENT_EXTRA_MESSAGE);
            String uid = intent.getStringExtra(INTENT_EXTRA_UID);
            String messageNotif = intent.getStringExtra(INTENT_EXTRA_MESSAGE_NOTIF);
            listener.userMessage(uid,msg, messageNotif);
        } else if (type == TYPE_MEDICINE) {
            listener.medicineToTake();
        } else if (type == TYPE_REMOVED_GODFATHER){
            String messageNotif = intent.getStringExtra(INTENT_EXTRA_MESSAGE_NOTIF);
            listener.removedGodfather(messageNotif);
        } else if (type == TYPE_REQUEST_GODCHILD){
            String messageNotif = intent.getStringExtra(INTENT_EXTRA_MESSAGE_NOTIF);
            listener.requestGodchild(messageNotif);
        } else if (type == TYPE_ACCEPTED_GODCHILD){
            String messageNotif = intent.getStringExtra(INTENT_EXTRA_MESSAGE_NOTIF);
            listener.acceptedGodchild(messageNotif);
        }else if (type == TYPE_BUZZ){
            String messageNotif = intent.getStringExtra(INTENT_EXTRA_MESSAGE_NOTIF);
            listener.buzzRecived(messageNotif);
        }
    }
}
