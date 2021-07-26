package com.bloomidea.inspirers;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.adapter.SymptomsPollAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.listener.JSONObjectListener;
import com.bloomidea.inspirers.listener.OkErrorPollListener;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.model.Question;
import com.bloomidea.inspirers.model.QuestionSlider;
import com.bloomidea.inspirers.model.QuestionYesNo;
import com.bloomidea.inspirers.model.TimelineItem;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.sync.InnerSyncPoll;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SymptomsPollActivity extends MyActiveActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 666;
    private static final int LOCATION_SETTINGS_REQUEST = 777;

    public static final String EXTRA_POLL = "EXTRA_POLL";
    public static final String EXTRA_POLL_TIMELINE_ID = "EXTRA_POLL_TIMELINE_ID";
    public static final String EXTRA_VIEW_ANSWER = "EXTRA_VIEW_ANSWER";

    private Poll poll = null;
    private boolean viewAnswer = false;
    private long pollTimelineId = -1;

    private RecyclerView questionsRecyclerView;
    private SymptomsPollAdapter adapter;

    private boolean saving = false;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms_poll);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        poll = (Poll) getIntent().getSerializableExtra(EXTRA_POLL);
        viewAnswer = getIntent().getBooleanExtra(EXTRA_VIEW_ANSWER,false);
        pollTimelineId = getIntent().getLongExtra(EXTRA_POLL_TIMELINE_ID,-1);

        questionsRecyclerView = (RecyclerView) findViewById(R.id.questions_recyclerView);

        adapter = new SymptomsPollAdapter(this, poll, viewAnswer, new SymptomsPollAdapter.SymptomsPollAdapterInteractionListener() {
            @Override
            public void end(final Poll poll) {
                if(!saving) {
                    saving = true;
                    AppController.getmInstance().answerPool(SymptomsPollActivity.this, poll, pollTimelineId, getSupportFragmentManager(), new OkErrorPollListener() {
                        @Override
                        public void ok(long pollId) {
                            if (poll.getPoolType().equals(Poll.POLL_TYPE_WEEKLY)) {
                                Question q = poll.getListQuestions().get(0);
                                if (q instanceof QuestionYesNo) {
                                    if (((QuestionYesNo) q).isYes() != null) {
                                        AppController.getmInstance().updateWeekPollAnswer(((QuestionYesNo) q).isYes().booleanValue());
                                    }
                                }
                            }

                            Utils.createNavigationAction(getString(R.string.answer_sint));

                            AppController.getmInstance().addTimelineItemNeedLocation(AppController.getmInstance().getTimelineDataSource().getTimelineItem(pollTimelineId));

                            getLastLocation();

                            //saving = false;

                            //onBackPressed();
                        }

                        @Override
                        public void error() {
                            //noting to do
                            saving = false;
                        }
                    });
                }
            }

            @Override
            public void showCreateSOSMedicineQuestion() {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                Intent i = new Intent(SymptomsPollActivity.this, AddSOSMedicineActivity.class);

                                Utils.openIntent(SymptomsPollActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                dialog.cancel();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(SymptomsPollActivity.this);
                builder.setMessage(R.string.create_sos_medicine_question).setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();
            }

            @Override
            public void goBack() {
                onBackPressed();
            }
        });

        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        questionsRecyclerView.setAdapter(adapter);
    }

    private void getLastLocation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }else{
            if(checkLocationEnable()) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    AppController.getmInstance().updateAllTimelineItemLocation(location);
                                }
                            }
                        });


                //saving = false;
                onBackPressed();
            }
        }
    }

    private boolean checkLocationEnable() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            androidx.appcompat.app.AlertDialog.Builder dialog = new 	androidx.appcompat.app.AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(myIntent,LOCATION_SETTINGS_REQUEST);
                }
            });
            dialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    onBackPressed();
                }
            });
            dialog.show();
        }

        return gps_enabled || network_enabled;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }else{
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    getLastLocation();
                    /*Utils.showMessageAlertDialog(this, getString(R.string.location_access_title), getString(R.string.location_access_explanation), new AlertDialogListener() {
                        @Override
                        public void okClick() {
                            getLastLocation();
                        }
                    });*/
                }else{
                    //AppController.getmInstance().forceSyncManual();
                    onBackPressed();
                }
            }
        }else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOCATION_SETTINGS_REQUEST){
            getLastLocation();
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
