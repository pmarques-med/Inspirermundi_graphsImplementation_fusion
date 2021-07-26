package com.bloomidea.inspirers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.bloomidea.inspirers.adapter.QuestionsPagerAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.customViews.ViewPagerCustomDuration;
import com.bloomidea.inspirers.customViews.ZoomOutPageTransformer;
import com.bloomidea.inspirers.listener.OkErrorPollListener;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.model.Question;
import com.bloomidea.inspirers.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.viewpagerindicator.CirclePageIndicator;

public class CARATQuizActivity extends MyActiveActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 666;
    private static final int LOCATION_SETTINGS_REQUEST = 777;

    private static final int CARAT_SCORE_REQUEST = 100;

    public static final String EXTRA_POLL = "EXTRA_POLL";
    public static final String EXTRA_POLL_TIMELINE_ID = "EXTRA_POLL_TIMELINE_ID";
    public static final String EXTRA_VIEW_ANSWER = "EXTRA_VIEW_ANSWER";
    public static final String EXTRA_VIEW_ANSWER_MY_POLL_ID = "EXTRA_VIEW_ANSWER_MY_POLL_ID";
    public static final String EXTRA_VIEW_ANSWER_MY_POLL_COMMENT = "EXTRA_VIEW_ANSWER_MY_POLL_COMMENT";
    public static final String EXTRA_CAN_COMMNET = "EXTRA_CAN_COMMNET";
    public static final String EXTRA_FROM_SCORE = "EXTRA_FROM_SCORE";

    private Poll poll = null;
    private boolean viewAnswer = false;
    private long myPollId = -1;
    private String myPollComment = "";
    private long pollTimelineId = -1;
    private boolean canComment = true;
    private boolean fromScore = false;

    private ViewPagerCustomDuration viewPager;
    private TextView saveBtn;
    private TextView caratTextview;

    private boolean saving = false;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caratquiz);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        poll = (Poll) getIntent().getSerializableExtra(EXTRA_POLL);
        viewAnswer = getIntent().getBooleanExtra(EXTRA_VIEW_ANSWER,false);
        canComment = getIntent().getBooleanExtra(EXTRA_CAN_COMMNET,true);
        fromScore = getIntent().getBooleanExtra(EXTRA_FROM_SCORE,false);

        myPollId = getIntent().getLongExtra(EXTRA_VIEW_ANSWER_MY_POLL_ID,-1);
        myPollComment = getIntent().getStringExtra(EXTRA_VIEW_ANSWER_MY_POLL_COMMENT);
        pollTimelineId = getIntent().getLongExtra(EXTRA_POLL_TIMELINE_ID,-1);

        saveBtn = (TextView) findViewById(R.id.save_btn);
        caratTextview = (TextView) findViewById(R.id.carat_textview);

        viewPager = findViewById(R.id.questions_viewPager);

        viewPager.setAdapter(new QuestionsPagerAdapter(getSupportFragmentManager(), this, poll.getListQuestions(), viewAnswer, new QuestionFragment.OnQuestionFragmentInteractionListener() {
            @Override
            public void updateQuestionAnswer(Question q) {
                updateQuestion(q);
            }
        }));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                viewPager.setScrollDurationFactor(1);
            }

            @Override
            public void onPageSelected(int position) {
                boolean lastPos = (position + 1) == poll.getListQuestions().size();
                if(!viewAnswer) {
                    if (lastPos) {
                        saveBtn.setVisibility(View.VISIBLE);
                    } else {
                        saveBtn.setVisibility(View.GONE);
                    }
                }else{
                    if(fromScore){
                        saveBtn.setVisibility(View.GONE);
                    }
                }

                if(lastPos){
                    caratTextview.setText(R.string.carat_text2_last_question);
                }else{
                    caratTextview.setText(R.string.carat_text2);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewAnswer){
                    if(fromScore){
                        onBackPressed();
                    }else {
                        showScore();
                    }
                }else {
                    if (!saving) {
                        saving = true;
                        AppController.getmInstance().answerPool(CARATQuizActivity.this, poll, pollTimelineId, getSupportFragmentManager(), new OkErrorPollListener() {
                            @Override
                            public void ok(long pollId) {
                                Utils.createNavigationAction(getString(R.string.anwser_carat));


                                viewAnswer = true;
                                myPollId = pollId;

                                AppController.getmInstance().addTimelineItemNeedLocation(AppController.getmInstance().getTimelineDataSource().getTimelineItem(pollTimelineId));

                                getLastLocation();

                                //saving = false;

                                //configureBtn();

                                //showScore();
                            }

                            @Override
                            public void error() {
                                //noting to do
                                saving = false;
                            }
                        });
                    }
                }
            }
        });

        viewPager.setPageMargin(((int)this.getResources().getDimension(R.dimen.space_pages)));
        viewPager.setPageTransformer(false, new ZoomOutPageTransformer());


        final CirclePageIndicator titleIndicator = (CirclePageIndicator)findViewById(R.id.titles);

        titleIndicator.setViewPager(viewPager);

        configureBtn();
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


                saving = false;

                configureBtn();

                showScore();
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
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(this);
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
                    saving = false;

                    configureBtn();

                    showScore();
                }
            });
            dialog.show();
        }

        return gps_enabled || network_enabled;
    }


    private void showScore() {
        Intent i = new Intent(CARATQuizActivity.this, CARATScoreActivity.class);
        i.putExtra(CARATScoreActivity.EXTRA_POLL, poll);
        i.putExtra(CARATScoreActivity.EXTRA_MY_POLL_ID, myPollId);
        i.putExtra(CARATScoreActivity.EXTRA_MY_POLL_COMMENT, myPollComment);
        i.putExtra(CARATScoreActivity.EXTRA_CAN_COMMENT, canComment);
        i.putExtra(CARATScoreActivity.EXTRA_FROM_QUESTIONS, true);

        Utils.openIntentForResult(CARATQuizActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left, CARAT_SCORE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CARAT_SCORE_REQUEST) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }else if(requestCode == LOCATION_SETTINGS_REQUEST){
            getLastLocation();
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void configureBtn(){
        if(viewAnswer){
            saveBtn.setText(R.string.continue_text);
            if(fromScore){
                saveBtn.setVisibility(View.GONE);
            }else {
                saveBtn.setVisibility(View.VISIBLE);
            }
        }else{
            saveBtn.setText(R.string.submit);
        }
    }

    private void updateQuestion(Question q){
        for(Question qAux : poll.getListQuestions()){
            if(qAux.getNid().equals(q.getNid())){
                qAux.updateFrom(q);
            }
        }

        final int nextPage = viewPager.getCurrentItem()+1;

        if(nextPage < poll.getListQuestions().size()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    viewPager.setScrollDurationFactor(5);
                    viewPager.setCurrentItem(nextPage,true);
                }

            }, 500);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();

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
                    saving = false;

                    configureBtn();

                    showScore();
                }
            }
        }else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }
}
