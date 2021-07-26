package com.bloomidea.inspirers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.listener.JSONObjectListener;
import com.bloomidea.inspirers.listener.OkErrorListener;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.GregorianCalendar;

public class SignupActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_WRITE = 300;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 400;

    public static final int REQUEST_IMAGE_CAPTURE_AVATAR = 1;
    public static final int RESULT_LOAD_IMAGE_AVATAR = 2;

    private Bitmap avatarImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        configTopMenu();

        ((EditText)findViewById(R.id.password_editText)).setTransformationMethod(new PasswordTransformationMethod());
        ((EditText)findViewById(R.id.conf_password_editText)).setTransformationMethod(new PasswordTransformationMethod());

        findViewById(R.id.picture_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImageFrom();
            }
        });

        findViewById(R.id.signup_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupNewUser();
            }
        });
    }

    private void loadImageFrom(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.choose_img))
                .setItems(R.array.photo_souce, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                dispatchTakePictureIntent();
                                break;
                            case 1:
                                dispatchGalleryIntent();
                                break;
                        }


                    }
                });
        AlertDialog dialog = builder.show();
    }

    private void dispatchTakePictureIntent() {
        if(checkPermissionsCamera()) {
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera_intent, REQUEST_IMAGE_CAPTURE_AVATAR);
        }
    }

    private boolean checkPermissionsCamera(){
        boolean permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==  PackageManager.PERMISSION_GRANTED;

        if(!permissionCheck) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }

        return permissionCheck;
    }

    private void dispatchGalleryIntent() {
        if(checkPermissionsReadWrite()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

            startActivityForResult(intent, RESULT_LOAD_IMAGE_AVATAR);
        }
    }

    private boolean checkPermissionsReadWrite(){
        boolean permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if(!permissionCheck) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_WRITE);
        }

        return permissionCheck;
    }

    private void configTopMenu() {
        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ((TextView) findViewById(R.id.title_textView)).setText(R.string.signup);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchGalleryIntent();
            }
        }else if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            }
        } else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RESULT_LOAD_IMAGE_AVATAR:
                    if(data!=null) {
                        Uri selectedImage = data.getData();
                        try {
                            auxSetAvatarImage(MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_IMAGE_CAPTURE_AVATAR:
                    auxSetAvatarImage((Bitmap) data.getExtras().get("data"));
                    break;
                default:
                    break;
            }
        }
    }

    private void auxSetAvatarImage(Bitmap auxImg){
        avatarImage = getResizedBitmap(auxImg,800);

        ((ImageView) findViewById(R.id.avatar_img)).setImageBitmap(avatarImage);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    private void signupNewUser() {
        final String name = ((EditText) findViewById(R.id.name_editText)).getText().toString();
        final String email = ((EditText) findViewById(R.id.email_editText)).getText().toString();
        final String password = ((EditText) findViewById(R.id.password_editText)).getText().toString();
        String confPassword = ((EditText) findViewById(R.id.conf_password_editText)).getText().toString();

        if(checkRegistrationData(name, email, password, confPassword)) {
            //Toast.makeText(SignupActivity.this,"Falta implementar",Toast.LENGTH_SHORT).show();
            final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(SignupActivity.this);

            final String avatarImgName = "avatar_" + (new GregorianCalendar().getTimeInMillis()) + ".jpg";

            APIInspirers.sendAvatarImgToServer(avatarImgName, avatarImage, new JSONObjectListener() {
                @Override
                public void onResponse(JSONObject response) {
                    String fidAux = null;
                    if (response != null) {
                        try {
                            fidAux = response.getString("fid");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    APIInspirers.signUp(getString(R.string.user_lang), fidAux, name, email, password, new JSONObjectListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            APIInspirers.login(email, password, new JSONObjectListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Utils.loginUserAux(SignupActivity.this, ringProgressDialogNoText, response, new OkErrorListener() {
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
                                        Toast.makeText(SignupActivity.this,R.string.error_password_email,Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SignupActivity.this,R.string.problem_communicating_with_server,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ringProgressDialogNoText.dismiss();
                            String errorMsg = getString(R.string.problem_communicating_with_server);
                            if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 406 && error.networkResponse.data != null) {
                                JSONObject msg = null;
                                try {
                                    msg = new JSONObject(new String(error.networkResponse.data));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if(msg != null){
                                    try {
                                        JSONObject form = msg.getJSONObject("form_errors");
                                        //if(form.has("name")){
                                        //    errorMsg = "Username already taken.";
                                        //}else
                                        if(form.has("mail")){
                                            errorMsg = getString(R.string.email_already_registered);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            Toast.makeText(SignupActivity.this,errorMsg,Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    ringProgressDialogNoText.dismiss();
                    Toast.makeText(SignupActivity.this,R.string.problem_communicating_with_server,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean checkRegistrationData(String name, String email, String password, String confPassword) {
        boolean ok = false;
        if(name.isEmpty()){
            Toast.makeText(this,R.string.empty_name,Toast.LENGTH_SHORT).show();
        }else if(email == null || email.isEmpty()){
            Toast.makeText(this,R.string.empty_email,Toast.LENGTH_SHORT).show();
        }else if(!Utils.isEmailValid(email)){
            Toast.makeText(this,R.string.invalid_email,Toast.LENGTH_SHORT).show();
        }else if(password == null || password.isEmpty()){
            Toast.makeText(this,R.string.empty_password,Toast.LENGTH_SHORT).show();
        }else if(!confPassword.equals(password)){
            Toast.makeText(this,R.string.conf_password_no_match,Toast.LENGTH_SHORT).show();
        }else{
            ok = true;
        }

        return ok;
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
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();

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

    private void goToMain() {
        Intent returnIntent = new Intent();

        setResult(RESULT_OK, returnIntent);

        //Intent i = new Intent(this, MainActivity.class);
        Intent i = new Intent(this, DisclamerActivity.class);

        Utils.openIntent(this, i, -1, -1);

        finish();
    }
}
