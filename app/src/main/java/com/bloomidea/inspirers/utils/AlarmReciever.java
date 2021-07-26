package com.bloomidea.inspirers.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by michellobato on 21/04/17.
 */

public class AlarmReciever extends BroadcastReceiver {
    public static final String ALARM_TYPE = "ALARM_TYPE";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Aqui","Aqui alarme");

        Intent inService = new Intent(context, TaskService.class);
        inService.putExtra(ALARM_TYPE,intent.getIntExtra(ALARM_TYPE,-1));

        context.startService(inService);
    }

}