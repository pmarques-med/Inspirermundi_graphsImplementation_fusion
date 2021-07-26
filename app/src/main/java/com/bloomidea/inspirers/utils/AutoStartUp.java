package com.bloomidea.inspirers.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bloomidea.inspirers.application.AppController;

/**
 * Created by michellobato on 21/04/17.
 */

public class AutoStartUp extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(AppController.getmInstance().getActiveUser()!=null) {
            Utils.createAllUserAlarms(context);
        }
    }
}
