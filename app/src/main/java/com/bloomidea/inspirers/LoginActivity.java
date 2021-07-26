package com.bloomidea.inspirers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.listener.JSONObjectListener;
import com.bloomidea.inspirers.listener.OkErrorListener;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    public static final int LOGIN_CREATE_ACCOUNT_REQUEST = 1002;

    private CallbackManager callbackManager;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        ((EditText)findViewById(R.id.password_editText)).setTransformationMethod(new PasswordTransformationMethod());

        Utils.clearFacebookSession();

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String token = loginResult.getAccessToken().getToken();
                Log.d("Facebook","ok: "+token);
                final ProgressDialog ringProgressDialog = Utils.createRingProgressDialogNoText(LoginActivity.this);

                APIInspirers.fboauth_login(loginResult.getAccessToken().getToken(), new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.loginUserAux(LoginActivity.this, ringProgressDialog, response, new OkErrorListener() {
                            @Override
                            public void ok() {
                                goToMain();
                            }

                            @Override
                            public void error() {
                                Utils.clearFacebookSession();
                            }
                        });
                        //loginUserAux(ringProgressDialog, response);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ringProgressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("Facebook","user cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("Facebook","error: " + exception.toString());
            }
        });

        findViewById(R.id.btn_login_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isOnline(LoginActivity.this,true, getSupportFragmentManager())) {
                    loginButton.performClick();
                }
            }
        });


        ((TextView) findViewById(R.id.recover_password_btn)).setText(Html.fromHtml("<u>"+getResources().getString(R.string.recover_password)+"</u>"));
        findViewById(R.id.recover_password_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle(R.string.recover_password);
                builder.setMessage(R.string.insert_email);

                LayoutInflater inflater = LoginActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.layout_recover_password,null);

                builder.setView(view);

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = ((EditText) view.findViewById(R.id.email_edittext)).getText().toString();

                        if(email.isEmpty()){
                            Toast.makeText(LoginActivity.this,R.string.error_email_empty,Toast.LENGTH_SHORT).show();
                        }else if(!Utils.isEmailValid(email)){
                            Toast.makeText(LoginActivity.this,R.string.error_email_invalid,Toast.LENGTH_SHORT).show();
                        }else{
                            recoverPassword(email);
                        }

                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(( view.findViewById(R.id.email_edittext)).getWindowToken(), 0);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow((view.findViewById(R.id.email_edittext)).getWindowToken(), 0);
                    }
                });

                builder.show();
            }
        });

        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ((EditText) findViewById(R.id.email_editText)).getText().toString();
                String pass = ((EditText) findViewById(R.id.password_editText)).getText().toString();

                if(checkLoginData(email, pass)) {
                    //Toast.makeText(LoginActivity.this,"Falta implementar",Toast.LENGTH_SHORT).show();

                    final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(LoginActivity.this);

                    APIInspirers.login(email, pass, new JSONObjectListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Utils.loginUserAux(LoginActivity.this, ringProgressDialogNoText, response, new OkErrorListener() {
                                @Override
                                public void ok() {
                                    goToMain();
                                }

                                @Override
                                public void error() {

                                }
                            });
                            //loginUserAux(ringProgressDialogNoText, response);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ringProgressDialogNoText.dismiss();
                            if (error.networkResponse != null && (error.networkResponse.statusCode == 403 || error.networkResponse.statusCode == 401)) {
                                Toast.makeText(LoginActivity.this,R.string.error_password_email,Toast.LENGTH_SHORT).show();
                            } else if(error.networkResponse.statusCode == 406) {
                                byte[] htmlBodyBytes = error.networkResponse.data;
                                Toast.makeText(LoginActivity.this,R.string.blocked_temporararily,Toast.LENGTH_SHORT).show();
                                Log.d("aux",""+new String(htmlBodyBytes));
                            }else{
                                Toast.makeText(LoginActivity.this,R.string.problem_communicating_with_server,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        findViewById(R.id.signup_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);

                Utils.openIntentForResult(LoginActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left, LoginActivity.LOGIN_CREATE_ACCOUNT_REQUEST);
            }
        });
    }
/*
    private void loginUserAux(final ProgressDialog ringProgressDialog, JSONObject response) {
        try {
            String uid = response.getJSONObject("user").getString("uid");
            final String sessionName = response.getString("session_name");
            final String sessionId = response.getString("sessid");

            User auxUser = AppController.getmInstance().getUserDataSource().getUserByUid(uid);
            final boolean exists = auxUser!=null;

            if(!exists){
                //auxUser = InspirersJSONParser.parseUser(response);
                APIInspirers.getUserInfo(uid,sessionName,sessionId, new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        User auxUser2 = null;
                        try {
                            auxUser2 = InspirersJSONParser.parseUser2(response.getJSONObject(0),true,sessionName,sessionId,true);
                            loginUser2(ringProgressDialog, auxUser2,false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ringProgressDialog.dismiss();
                    }
                });
            }else{
                auxUser.setSessionId(sessionId);
                auxUser.setSessionName(sessionName);
                loginUser2(ringProgressDialog, auxUser,exists);
            }



        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();

            ringProgressDialog.dismiss();
        }
    }

    private void loginUser2(final ProgressDialog ringProgressDialog, final User auxUser, final boolean exists) {
        AppController.getmInstance().localLoginUser(auxUser, exists);

        Utils.createNavigationAction(getString(R.string.login_action));

        AppController.getmInstance().getUserToken(new StringListener() {
            @Override
            public void onResponse(String response) {
                goToMain();
                ringProgressDialog.dismiss();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                goToMain();
                ringProgressDialog.dismiss();
            }
        });
    }*/

    private void recoverPassword(String email) {
        //Toast.makeText(LoginActivity.this,"Falta implementar",Toast.LENGTH_SHORT).show();
        final ProgressDialog ringProgress = Utils.createRingProgressDialogNoText(this);

        APIInspirers.recoverPassword(email, new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {
                ringProgress.dismiss();
                boolean notFound = false;
                boolean notSend = false;
                try {
                    notFound = response.getString(0).contains("not found");
                    notSend = response.getString(0).contains("false");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (notFound) {
                    Toast.makeText(LoginActivity.this, R.string.password_recover_error_dont_exist, Toast.LENGTH_LONG).show();
                } else if(notSend){
                    Toast.makeText(LoginActivity.this, R.string.password_recover_error_send_email, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.password_recover_ok, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                String auxError = getString(R.string.problem_communicating_with_server);
                if(error != null && error.networkResponse != null && error.networkResponse.data != null){
                    String aux = new String(error.networkResponse.data);

                    if(aux.contains("is not recognized")){
                        auxError = getString(R.string.password_recover_error_dont_exist);
                    }
                }

                ringProgress.dismiss();

                Toast.makeText(LoginActivity.this, auxError, Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
    private void loginUser(final ProgressDialog ringProgressDialog, final User auxUser, final boolean exists) {
        AppController.getmInstance().localLoginUser(auxUser, exists);

        AppController.getmInstance().getUserToken(new StringListener() {
            @Override
            public void onResponse(String response) {

                if(exists){
                    goToMain();
                    ringProgressDialog.dismiss();
                }else{
                    APIInspirers.getUserAchievment(AppController.getmInstance().getActiveUser().getUid(), new JSONArrayListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            ArrayList<UserBadge> userBadges = InspirersJSONParser.parseListUserBadges(response);

                            AppController.getmInstance().setUserBadgesNoSync(userBadges);

                            goToMain();
                            ringProgressDialog.dismiss();
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            goToMain();
                            ringProgressDialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                goToMain();

                ringProgressDialog.dismiss();
            }
        });

        AppController.getmInstance().getCountryList(new HashMapListener() {
            @Override
            public void onResponse(HashMap response) {
                String auxStringIso = AppController.getmInstance().getActiveUser().getIsoCountry();

                if(auxStringIso != null && !auxStringIso.isEmpty() ) {
                    String auxStringCountryName = AppController.getmInstance().getActiveUser().getCountryName();

                    if (auxStringCountryName == null || auxStringCountryName.isEmpty()) {
                        HashMap<String, Country> aux = (HashMap) response;

                        AppController.getmInstance().updateUserCountryInfo(aux.get(auxStringIso));
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }*/

    private void goToMain() {
        Intent i;

        if(!AppController.getmInstance().getActiveUser().isAcceptedTerms() || (AppController.getmInstance().getActiveUser().isAcceptedTerms() && !AppController.getmInstance().getActiveUser().isTermsOff())){
            i = new Intent(this, DisclamerActivity.class);
        }else {
            i = new Intent(this, MainActivity.class);
        }
        Utils.openIntent(this, i, -1, -1);

        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_CREATE_ACCOUNT_REQUEST && resultCode == RESULT_OK) {
            finish();
        }
    }

    private boolean checkLoginData(String email, String password) {
        boolean ok = false;

        if(email == null || email.isEmpty()){
            Toast.makeText(this,R.string.empty_email,Toast.LENGTH_SHORT).show();
        }else if(!Utils.isEmailValid(email)){
            Toast.makeText(this,R.string.invalid_email,Toast.LENGTH_SHORT).show();
        }else if(password == null || password.isEmpty()){
            Toast.makeText(this,R.string.empty_password,Toast.LENGTH_SHORT).show();
        }else{
            ok = true;
        }


        return ok;
    }
}
