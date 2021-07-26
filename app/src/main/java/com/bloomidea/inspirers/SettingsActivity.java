package com.bloomidea.inspirers;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.listener.JSONObjectListener;
import com.bloomidea.inspirers.model.NavAux;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.Utils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SettingsActivity extends MyActiveActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_WRITE = 300;

    private String[] optionsCARAT;
    private int selectedTypePos = -1;
    private int selectedCARATOptionTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        optionsCARAT = new String[]{getString(R.string.carat_option_weakly),getString(R.string.carat_option_2_on_2),getString(R.string.carat_option_4_on_4)};
        selectedCARATOptionTime = AppController.getmInstance().getActiveUser().getTimeCARAT();

        selectedTypePos = selectedCARATOptionTime==-7?0:selectedCARATOptionTime==-14?1:2;

        loadTextCARATPOS();

        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ((TextView) findViewById(R.id.title_textView)).setText(R.string.settings);

        ((SwitchCompat) findViewById(R.id.notification_switch)).setChecked(AppController.getmInstance().getActiveUser().isPushOn());
        ((SwitchCompat) findViewById(R.id.notification_switch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppController.getmInstance().updateUserNotifications(SettingsActivity.this, b);
            }
        });

        findViewById(R.id.carat_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb = new AlertDialog.Builder(SettingsActivity.this);

                adb.setTitle(R.string.period_carat);
                adb.setSingleChoiceItems(optionsCARAT, selectedTypePos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setSelectedCARATOption(i);
                        dialogInterface.dismiss();
                    }
                });

                adb.setNegativeButton(getString(R.string.cancel), null);

                adb.show();
            }
        });

        findViewById(R.id.export_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportNavigationData();
            }
        });

        findViewById(R.id.about_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, DisclamerActivity.class);
                i.putExtra(DisclamerActivity.EXTRA_IS_ABOUT,true);
                i.putExtra(DisclamerActivity.EXTRA_FROM_SETTINGS,true);

                Utils.openIntent(SettingsActivity.this, i, -1, -1);
            }
        });

        findViewById(R.id.terms_and_conditions_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, DisclamerActivity.class);
                i.putExtra(DisclamerActivity.EXTRA_IS_ABOUT,false);
                i.putExtra(DisclamerActivity.EXTRA_FROM_SETTINGS,true);

                Utils.openIntent(SettingsActivity.this, i, -1, -1);
            }
        });

        findViewById(R.id.rules_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, AboutActivity.class);

                Utils.openIntent(SettingsActivity.this, i, -1, -1);
            }
        });

        findViewById(R.id.logout_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isOnline(SettingsActivity.this,true,getSupportFragmentManager())) {
                    AppController.getmInstance().forceSyncManual();

                    final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(SettingsActivity.this);

                    Utils.clearSession(SettingsActivity.this, new JSONObjectListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            logOutServer(ringProgressDialogNoText);
                            Log.d("TESTE1","TESTE1");
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            logOutServer(ringProgressDialogNoText);
                            Log.d("TESTE2","TESTE2");
                        }
                    });
                }
            }
        });
    }

    private void logOutServer(final ProgressDialog ringProgressDialogNoText) {
        APIInspirers.logOutFromServer(new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TESTE3","TESTE3");
                ringProgressDialogNoText.dismiss();

                AppController.getmInstance().loggout(SettingsActivity.this);

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                finish();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TESTE4","TESTE4");
                ringProgressDialogNoText.dismiss();
                Toast.makeText(SettingsActivity.this,R.string.problem_communicating_with_server,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void exportNavigationData() {

        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(this);

        ArrayList<NavAux> data = AppController.getmInstance().getNavigationDataSource().getUserNavigationInfo(AppController.getmInstance().getActiveUser().getId());
        String filename = getString(R.string.csv_navigation_data);
        String email = getString(R.string.email_csv_navigation);

        if(data == null || data.isEmpty()){
            ringProgressDialogNoText.dismiss();
            Toast.makeText(this,R.string.noting_to_share,Toast.LENGTH_SHORT).show();
        }else{
            if(checkPermissions()) {
                String csv = convertToCsvString(data);
                Uri csvFile = saveCSVToFile(csv,filename);

                ringProgressDialogNoText.dismiss();

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.export_navigation_data_subject));
                sendIntent.putExtra(Intent.EXTRA_STREAM, csvFile);
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});

                sendIntent.setType("text/html");
                startActivity(sendIntent);
            }else{
                ringProgressDialogNoText.dismiss();
            }
        }
    }

    private boolean checkPermissions(){
        boolean permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if(!permissionCheck) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_WRITE);
        }

        return permissionCheck;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findViewById(R.id.export_box).performClick();
            }
        }else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    private String convertToCsvString(ArrayList<NavAux> data){
        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        User aux = AppController.getmInstance().getActiveUser();

        String result = getString(R.string.csv_nav_file_header1)+"\n";
        result += aux.getUserName() + ",";
        result += android_id + ",";
        result += aux.getUserProvidedId() + "";

        result += "\n";
        result += "\n";

        result += getString(R.string.csv_nav_file_header2)+"\n";

        for(NavAux item : data){
            result += item.getType() + ",";
            result += item.getDescription() + ",";
            result += (item.getStartTime().getTimeInMillis()/1000) + ",";
            if(item.getType().equals(MyActiveActivity.NAVIGATION_TYPE_SCREEN) && item.getEndTime()!=null) {
                result += (item.getEndTime().getTimeInMillis()/1000) + "";
            }else{
                result += "---";
            }

            result += "\n";

        }

        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void setSelectedCARATOption(int selectedPos) {
        selectedTypePos = selectedPos;


        if(selectedPos == 0){
            this.selectedCARATOptionTime = -7;
        }else if(selectedPos == 1){
            this.selectedCARATOptionTime = -14;
        }else {
            this.selectedCARATOptionTime = -28;
        }

        AppController.getmInstance().updateUserCaratTime(selectedCARATOptionTime);

        loadTextCARATPOS();
    }

    private void loadTextCARATPOS() {
        ((TextView) findViewById(R.id.carat_textview)).setText(selectedCARATOptionTime==-7?R.string.carat_option_weakly:selectedCARATOptionTime==-14?R.string.carat_option_2_on_2:R.string.carat_option_4_on_4);
    }

    private Uri saveCSVToFile(String csv, String fileName){
        File file   = null;
        File root   = Environment.getExternalStorageDirectory();
        if (root.canWrite()){
            File dir    =   new File (root.getAbsolutePath() + "/PersonData");
            dir.mkdirs();
            file   =   new File(dir, fileName+".csv");
            FileOutputStream out   =   null;
            try {
                out = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                out.write(csv.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Uri u1  = null;

        if(file!=null) {
            u1 = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);//Uri.fromFile(file);
        }

        return u1;
    }
}
