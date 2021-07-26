package com.bloomidea.inspirers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.listener.JSONObjectListener;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashscreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1000;//2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppController.getmInstance().getActiveUser() == null) {
                    launchLogin();
                }else{
                    if(Utils.isOnline(SplashscreenActivity.this,false,null)) {
                        APIInspirers.userLoggedServer(new JSONObjectListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int logado = response.getInt("islogged");

                                    if (logado == 0) {
                                        Utils.clearSession(SplashscreenActivity.this, new JSONObjectListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                clearLoggedUserAndGoToLogin();

                                                finish();
                                                //logOutServer(ringProgressDialogNoText);
                                                Log.d("TESTE1", "TESTE1");
                                            }

                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                clearLoggedUserAndGoToLogin();
                                                finish();
                                                //logOutServer(ringProgressDialogNoText);
                                                Log.d("TESTE2", "TESTE2");
                                            }
                                        });
                                    } else {
                                        final User activeUser = AppController.getmInstance().getActiveUser();
                                        APIInspirers.requestActiveDevice(activeUser.getUid(), new JSONArrayListener() {
                                            @Override
                                            public void onResponse(JSONArray response) {
                                                if (response.length() > 0) {
                                                    try {
                                                        String deviceIdServer = response.getJSONObject(0).getString("active device");

                                                        String localDeviceId = activeUser.getDeviceId();

                                                        if (deviceIdServer.equals(localDeviceId)) {
                                                            launchMain();

                                                            finish();
                                                        } else {
                                                            Utils.clearSession(SplashscreenActivity.this, new JSONObjectListener() {
                                                                @Override
                                                                public void onResponse(JSONObject response) {
                                                                    clearLoggedUserAndGoToLogin();

                                                                    finish();
                                                                    //logOutServer(ringProgressDialogNoText);
                                                                    Log.d("TESTE1", "TESTE1");
                                                                }

                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    clearLoggedUserAndGoToLogin();
                                                                    finish();
                                                                    //logOutServer(ringProgressDialogNoText);
                                                                    Log.d("TESTE2", "TESTE2");
                                                                }
                                                            });
                                                        }
                                                    } catch (JSONException e) {
                                                        clearLoggedUserAndGoToLogin();

                                                        finish();
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    clearLoggedUserAndGoToLogin();

                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                clearLoggedUserAndGoToLogin();
                                                finish();
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();

                                    clearLoggedUserAndGoToLogin();
                                    finish();
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                launchLogin();
                            }
                        });
                    }else {
                        launchMain();
                    }
                }
            }
        },SPLASH_TIME_OUT);
    }

    private void clearLoggedUserAndGoToLogin() {
        AppController.getmInstance().loggout(SplashscreenActivity.this);
        launchLogin();
    }

    /*
    private void launchMain(){
        Intent i = new Intent(this, MainActivity.class);

        Utils.openIntent(this, i, -1, -1);

        this.finish();
    }*/

    private void launchMain() {
        Intent i;

        if(!AppController.getmInstance().getActiveUser().isAcceptedTerms()){
            i = new Intent(this, DisclamerActivity.class);
        }else {
            i = new Intent(this, MainActivity.class);
        }
        Utils.openIntent(this, i, -1, -1);

        this.finish();
    }

    private void launchLogin(){
        Intent i = new Intent(this, LoginActivity.class);

        Utils.openIntent(this, i, -1, -1);

        this.finish();
    }
}
