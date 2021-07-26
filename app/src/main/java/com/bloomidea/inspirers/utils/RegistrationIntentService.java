package com.bloomidea.inspirers.utils;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.listener.JSONObjectListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONObject;

/**
 * Created by michellobato on 21/04/17.
 */

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(TAG, "GCM Registration Token: " + token);

            sendRegistrationToServer(token);

        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }

    private void sendRegistrationToServer(final String token) {
        APIInspirers.registerDeviceNotifications(getString(R.string.push_lang), token, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                AppController.getmInstance().updateNotificationToken(token);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error","getRegid");
            }
        });

    }

}