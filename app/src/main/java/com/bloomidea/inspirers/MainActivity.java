package com.bloomidea.inspirers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.adapter.MissOptionAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.BorderProgressBar;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.customViews.ProgressBar;
import com.bloomidea.inspirers.events.LogoutEvent;
import com.bloomidea.inspirers.events.MedicineCreatedEvent;
import com.bloomidea.inspirers.events.MedicinesChangedEvent;
import com.bloomidea.inspirers.events.PollAnswered;
import com.bloomidea.inspirers.events.UserInfoUpdated;
import com.bloomidea.inspirers.events.UserNewPoints;
import com.bloomidea.inspirers.events.UserStatsModifiedEvent;
import com.bloomidea.inspirers.listener.OkErrorListener;
import com.bloomidea.inspirers.listener.TreeMapListener;
import com.bloomidea.inspirers.model.Level;
import com.bloomidea.inspirers.model.MissOption;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.model.TimelineItem;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.model.UserNormalMedicine;
import com.bloomidea.inspirers.utils.MedicineTypeAux;
import com.bloomidea.inspirers.utils.Utils;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.medida.inhalerdetection.InhalerDetectionActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeMap;

import rx.functions.Action1;

public class MainActivity extends MyActiveActivity implements TimelineFragment.OnTimelineFragmentInteractionListener {
    private static final int INHALER_DETECTION_REQUEST = 101;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 666;
    private static final int LOCATION_SETTINGS_REQUEST = 777;

    public static final String EXTRA_TAB_OPEN = "EXTRA_TAB_OPEN";
    public static final int TAB_PEOPLE = 1;

    private View selectedImageView;

    private Fragment actual;

    private ImageView profile_image;
    private ProgressBar profile_image_progress;
    private DonutProgress donut_progress;
    private TextView total_points;
    private TextView level_textview;
    private BorderProgressBar level_progress_bar;

    private AlertDialog dialog;

    private ImageView take_medicine_imageView;
    private TimelineItem medicineCanTake;
    private DonutProgress donut_progress_medicine_timer;
    private CountDownTimer countDownMedicineTimer;

    private FusedLocationProviderClient mFusedLocationClient;

    private MissOptionAdapter optionAdapter;
    private ArrayList<MissOption> missOptionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        //Log.d("uniqueID",androidId);

        AppController.getmInstance().getPolls(new TreeMapListener() {
            @Override
            public void onResponse(TreeMap response) {
                if(AppController.getmInstance().getActiveUser()!=null) {
                    AppController.getmInstance().checkUserPolls();

                    AppController.getmInstance().updateUserStats(false);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AppController.getmInstance().updateUserStats(false);
            }
        });

        //AppController.getmInstance().forceSyncManual();

        if(AppController.getmInstance().getActiveUser()!=null && AppController.getmInstance().getActiveUser().isPushOn() && AppController.getmInstance().getActiveUser().getNotificationsToken().isEmpty()) {
            Utils.getRegId(this);
        }

        configureEventListener();

        take_medicine_imageView = (ImageView) findViewById(R.id.take_medicine_imageView);
        donut_progress_medicine_timer = (DonutProgress) findViewById(R.id.donut_progress_medicine_timer);


        findViewById(R.id.main_menu_timeline_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(TimelineFragment.newInstance());
                changeMenuVisualSelection(findViewById(R.id.timeline_imageView));
            }
        });

        findViewById(R.id.main_menu_people_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isOnline(MainActivity.this,true,getSupportFragmentManager())) {
                    setFragment(PeopleFragment.newInstance());
                    changeMenuVisualSelection(findViewById(R.id.people_imageView));
                }
            }
        });

        findViewById(R.id.main_menu_stats_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(StatisticsFragment.newInstance());
                changeMenuVisualSelection(findViewById(R.id.stats_imageView));
            }
        });

        findViewById(R.id.main_menu_leader_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isOnline(MainActivity.this,true,getSupportFragmentManager())) {
                    setFragment(LeaderboardFragment.newInstance());
                    changeMenuVisualSelection(findViewById(R.id.leader_imageView));
                }
            }
        });

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHideAddMedicine(true);
            }
        });

        findViewById(R.id.close_add_medicine_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHideAddMedicine(false);
            }
        });

        findViewById(R.id.add_medicine_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHideAddMedicine(false);
            }
        });

        findViewById(R.id.add_overlay_bottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHideAddMedicine(false);
            }
        });


        findViewById(R.id.add_medicine_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(View.VISIBLE == view.getVisibility()){
                    Intent i = new Intent(MainActivity.this, AddMedicineActivity.class);

                    Utils.openIntent(MainActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                }
            }
        });

        findViewById(R.id.add_sos_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(View.VISIBLE == view.getVisibility()){

                    Intent i = new Intent(MainActivity.this, AddSOSMedicineActivity.class);

                    Utils.openIntent(MainActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                }
            }
        });

        findViewById(R.id.profile_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, UserProfileActivity.class);

                Utils.openIntent(MainActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        findViewById(R.id.settings_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);

                Utils.openIntent(MainActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        findViewById(R.id.help_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AboutActivity.class);

                Utils.openIntent(MainActivity.this, i, -1, -1);
            }
        });

        configureHeaderView();

        int tabToOpen = getIntent().getIntExtra(EXTRA_TAB_OPEN,-1);

        if(tabToOpen==-1) {
            goToTimeline();
        }else if(tabToOpen == TAB_PEOPLE){
            goToPeople();
        }

        setUpActiveMedicine(true);


        if(AppController.getmInstance().getActiveUser().isFirstLogin()){
            Intent i = new Intent(this, StudyIDActivity.class);
            //i.putExtra(EditProfileActivity.EXTRA_HIDE_BACK_BTN, true);

            Utils.openIntent(this, i, R.anim.slide_in_left, R.anim.slide_out_left);
        }

        //MARK6: ADICIONAR TIMELINE ITENS QUANDO NÂO TEM MAIS NOS MEDICAMNETOS CONTINUOS


        User aux = AppController.getmInstance().getActiveUser();
        ArrayList<UserNormalMedicine> auxListMeds;
        auxListMeds = AppController.getmInstance().getMedicineDataSource().getUserNormalMedicines(aux.getId());
        for (UserNormalMedicine auxMed : auxListMeds){
            if (auxMed.getDuration() == 0){
                TimelineItem auxTime = AppController.getmInstance().getTimelineDataSource().getLastTimelineForMedicine(auxMed.getId());

                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
                GregorianCalendar today = new GregorianCalendar();
                today.add(Calendar.HOUR_OF_DAY, -1);

                String teste1 = fmt.format(today.getTime());
                String teste2 = fmt.format(auxTime.getDate().getTime());

                if (fmt.format(today.getTime()).equals(fmt.format(auxTime.getDate().getTime())) || today.getTimeInMillis() > auxTime.getDate().getTimeInMillis()){
                    AppController.getmInstance().getTimelineDataSource().createTimelineUserMedicine(aux.getId(), auxMed, today, true);
                }
            }
        }

        //MARK7: VER SE NÃO TOMOU MEDS DA ULTIMA SEMANA E MOSTRAR OVERLAY

        GregorianCalendar todayAux = new GregorianCalendar();
        todayAux.add(Calendar.DAY_OF_MONTH, -7);
        String lastWeek = Utils.getWeekNumber(todayAux);
        if (!AppController.getmInstance().getTimelineDataSource().hasTakenMedicineOnWeek(lastWeek) && AppController.getmInstance().getTimelineDataSource().hasMissMedicineOnWeek(lastWeek)){
            final View overlayDontTake = findViewById(R.id.overlay_donttake_med);
            overlayDontTake.setVisibility(View.VISIBLE);

            RecyclerView recyclerView;
            LinearLayoutManager linearLayoutManager;
            linearLayoutManager = new LinearLayoutManager(MainActivity.this);

            missOptionsList = MissOption.getAllMissOptions();
            final ArrayList<MissOption> finalMissOptionsList = missOptionsList;
            optionAdapter = new MissOptionAdapter(MainActivity.this, missOptionsList, new MissOptionAdapter.MissOptionAdapterListener() {
                @Override
                public void onOptionClick(MissOption option) {
                    for (MissOption auxOption : finalMissOptionsList){
                        auxOption.setSelected(false);
                        if (auxOption.equals(option)){
                            auxOption.setSelected(true);
                        }
                    }
                    optionAdapter.notifyDataSetChanged();
                }
            });

            recyclerView = findViewById(R.id.recyclerViewOptions);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(optionAdapter);

            overlayDontTake.findViewById(R.id.overlay_btn_miss_send).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String textMissOption = "";
                    for (MissOption option : missOptionsList){
                        if (option.isSelected()){
                            textMissOption = option.getName();
                        }
                    }

                    if (!textMissOption.equals("")){
                        overlayDontTake.setVisibility(View.GONE);
                    }else{
                        Toast.makeText(MainActivity.this, R.string.select_option, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            overlayDontTake.findViewById(R.id.overlay_btn_miss_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    overlayDontTake.setVisibility(View.GONE);
                }
            });
        }
        //testeAlarme();
    }

    /*private void testeAlarme() {
        GregorianCalendar now = new GregorianCalendar();
        now.add(Calendar.SECOND,15);

        Intent intentAlarm = new Intent(this, AlarmReciever.class);
        intentAlarm.putExtra(AlarmReciever.ALARM_TYPE, InternalBroadcastReceiver.TYPE_MEDICINE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

        // create the object
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.set(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), pendingIntent);

        Log.d("Alarme", "Alarm Scheduled for " + (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(now.getTimeInMillis()))) + "(" + (1) + ")");
    }*/

    private void configureEventListener() {
        AppController.getmInstance().getMyBus().toObserverable()
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {

                        if(event instanceof UserNewPoints || event instanceof UserStatsModifiedEvent) {
                            loadUserPoints();
                        }else if(event instanceof MedicineCreatedEvent){
                            setUpActiveMedicine(true);

                            //showHideAddMedicine(false);
                        }else if(event instanceof UserInfoUpdated){
                            configureHeaderView();
                        }else if(event instanceof MedicinesChangedEvent){
                            setUpActiveMedicine(true);
                        }else if(event instanceof LogoutEvent){
                            finish();
                        }else if(event instanceof PollAnswered){
                            setUpActiveMedicine(true);
                        }
                    /*
                    * else if(event instanceof UserWonBadge){
                        UserWonBadge auxEvent = (UserWonBadge) event;

                        Utils.showWinBadge(MainActivity.this, getSupportFragmentManager(), auxEvent.getBadgeWon().getBadge(),null);
                    }
                    * */
                    }
                });
    }

    private void showHideAddMedicine(boolean show) {
        if(show){
            //findViewById(R.id.white_view).setVisibility(View.GONE);
            //findViewById(R.id.textView3).setVisibility(View.GONE);
            //findViewById(R.id.textView4).setVisibility(View.GONE);
            //findViewById(R.id.imageView10).setVisibility(View.GONE);
            //findViewById(R.id.imageView11).setVisibility(View.GONE);
            //findViewById(R.id.imageView12).setVisibility(View.GONE);

            findViewById(R.id.add_medicine_view).setVisibility(View.VISIBLE);
            findViewById(R.id.add_overlay).setVisibility(View.VISIBLE);
            findViewById(R.id.add_overlay2).setVisibility(View.VISIBLE);
            findViewById(R.id.add_overlay_bottom).setVisibility(View.VISIBLE);
            findViewById(R.id.close_add_medicine_btn).setVisibility(View.VISIBLE);
            findViewById(R.id.take_medicine_box).setBackgroundResource(R.drawable.item_medicine_normal_btn_black_transparent);

            TranslateAnimation animation = new TranslateAnimation(Utils.dp2px(MainActivity.this,56),0,0,0);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setDuration(200);
            animation.setFillAfter(true);

            findViewById(R.id.add_medicine_btn).startAnimation(animation);

            TranslateAnimation animation2 = new TranslateAnimation(0,0,Utils.dp2px(MainActivity.this,56)*-1,0);
            animation2.setInterpolator(new AccelerateInterpolator());
            animation2.setDuration(200);
            animation2.setFillAfter(true);
            animation2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
                    fadeIn.setInterpolator(new AccelerateInterpolator());
                    fadeIn.setDuration(150);
                    fadeIn.setFillAfter(true);

                    findViewById(R.id.textView3).startAnimation(fadeIn);
                    findViewById(R.id.textView4).startAnimation(fadeIn);
                    findViewById(R.id.white_view).startAnimation(fadeIn);
                    findViewById(R.id.imageView10).startAnimation(fadeIn);
                    findViewById(R.id.imageView11).startAnimation(fadeIn);
                    findViewById(R.id.imageView12).startAnimation(fadeIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            findViewById(R.id.add_sos_btn).startAnimation(animation2);
        }else{
            AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setDuration(150);
            fadeOut.setFillAfter(true);

            AlphaAnimation fadeOut2= new AlphaAnimation(1, 0);
            fadeOut2.setInterpolator(new AccelerateInterpolator());
            fadeOut2.setDuration(150);
            fadeOut2.setFillAfter(true);
            fadeOut2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    TranslateAnimation animation2 = new TranslateAnimation(0,Utils.dp2px(MainActivity.this,56),0,0);
                    animation2.setInterpolator(new AccelerateInterpolator());
                    animation2.setDuration(200);
                    animation2.setFillAfter(true);

                    findViewById(R.id.add_medicine_btn).startAnimation(animation2);

                    TranslateAnimation animation3 = new TranslateAnimation(0,0,0,Utils.dp2px(MainActivity.this,56)*-1);
                    animation3.setInterpolator(new AccelerateInterpolator());
                    animation3.setDuration(200);
                    animation3.setFillAfter(true);
                    animation3.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            findViewById(R.id.add_medicine_view).setVisibility(View.GONE);
                            findViewById(R.id.add_overlay).setVisibility(View.GONE);
                            findViewById(R.id.add_overlay2).setVisibility(View.GONE);
                            findViewById(R.id.add_overlay_bottom).setVisibility(View.GONE);
                            findViewById(R.id.take_medicine_box).setBackgroundResource(R.drawable.item_medicine_normal_btn_back);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    findViewById(R.id.add_sos_btn).startAnimation(animation3);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            findViewById(R.id.textView3).startAnimation(fadeOut);
            findViewById(R.id.textView4).startAnimation(fadeOut);
            findViewById(R.id.white_view).startAnimation(fadeOut);
            findViewById(R.id.imageView10).startAnimation(fadeOut);
            findViewById(R.id.imageView11).startAnimation(fadeOut);
            findViewById(R.id.imageView12).startAnimation(fadeOut2);
            findViewById(R.id.close_add_medicine_btn).setVisibility(View.INVISIBLE);
        }
    }

    private void configureHeaderView() {
        User aux = AppController.getmInstance().getActiveUser();

        if(aux.getPicture()==null) {
            String imgUrl = aux.getPictureUrl();

            if(imgUrl!=null && !imgUrl.isEmpty()){
                Picasso.get()
                        .load(imgUrl)
                        .resizeDimen(R.dimen.avatar_w_h, R.dimen.avatar_w_h)
                        .centerCrop()
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                Bitmap aux = bitmap.copy(bitmap.getConfig(), true);

                                AppController.getmInstance().updateUserPicture(aux);
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }


                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            }
        }


        profile_image = (ImageView) findViewById(R.id.profile_image);
        profile_image_progress = (ProgressBar) findViewById(R.id.profile_image_progress);
        donut_progress = (DonutProgress) findViewById(R.id.donut_progress);
        total_points = (TextView) findViewById(R.id.total_points);
        level_textview = (TextView) findViewById(R.id.level_textview);
        level_progress_bar = (BorderProgressBar) findViewById(R.id.level_progress_bar);

        Utils.loadImageView(this, profile_image, profile_image_progress, aux.getPictureUrl(), aux.getPicture(), R.drawable.default_avatar, null);

        loadUserPoints();
    }

    private void loadUserPoints() {
        User aux = AppController.getmInstance().getActiveUser();

        donut_progress.setFinishedStrokeColor(Color.parseColor(aux.getStatsEverColor()));
        donut_progress.setProgress(aux.getStatsEver().floatValue());

        Level nextLevel = AppController.getmInstance().getNextLevel(aux.getLevel());
        Level actualLevel = AppController.getmInstance().getLevel(aux.getLevel());

        int nextLevelPoints = nextLevel.getPoints();

        total_points.setText(""+aux.getUserPoints()+"/"+nextLevelPoints);
        level_textview.setText(getResources().getString(R.string.level_text, aux.getLevel()));


        int maxP = nextLevelPoints-actualLevel.getPoints();
        int actCal =maxP-(nextLevelPoints-aux.getUserPoints());

        level_progress_bar.setCurProgress(actCal);

        level_progress_bar.setMaxProgress(maxP);
    }

    private void setFragment(final Fragment newFragment){
        actual = newFragment;

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        String tagAux = ""+(new GregorianCalendar()).getTimeInMillis();
        transaction.replace(R.id.fragment_container, newFragment,tagAux);

        transaction.commit();
    }

    private void changeMenuVisualSelection(View newImageView) {
        if(selectedImageView!=null && selectedImageView.getId()!=newImageView.getId()){
            selectedImageView.setSelected(false);
        }

        newImageView.setSelected(true);

        selectedImageView = newImageView;
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()<=1){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            MainActivity.this.finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            dialog.cancel();
                            break;
                    }
                }
            };

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setMessage(R.string.exit_question).setPositiveButton(R.string.yes, dialogClickListener)
                    .setNegativeButton(R.string.no, dialogClickListener).show();
        }else {
            super.onBackPressed();
        }
    }

    public void setUpActiveMedicine(boolean reloadMedicine){
        if(medicineCanTake == null || reloadMedicine) {
            medicineCanTake = AppController.getmInstance().getTimelineDataSource().getActiveTimelineItem(AppController.getmInstance().getActiveUser().getId());
        }

        donut_progress_medicine_timer.setVisibility(View.GONE);
        if(countDownMedicineTimer!=null){
            countDownMedicineTimer.cancel();
        }

        findViewById(R.id.take_medicine_box).setBackgroundResource(R.drawable.item_medicine_normal_btn_back);
        if(AppController.getmInstance().getMedicineDataSource().getUserNormalMedicines(AppController.getmInstance().getActiveUser().getId()).isEmpty() && AppController.getmInstance().getMedicineDataSource().getUserNormalMedicinesDisable(AppController.getmInstance().getActiveUser().getId()).isEmpty()){
            take_medicine_imageView.setImageResource(R.drawable.asset43);
            Utils.changeBtnBackgroundMedicine(take_medicine_imageView,R.drawable.no_med_to_take_white);
            findViewById(R.id.take_medicine_box).setBackgroundResource(R.drawable.item_medicine_normal_btn_black_transparent);
            //findViewById(R.id.take_medicine_box).setVisibility(View.INVISIBLE);
        }else if(medicineCanTake != null && medicineCanTake.getMedicine() != null) {
            findViewById(R.id.take_medicine_box).setVisibility(View.VISIBLE);
            boolean animate = false;

            take_medicine_imageView.setImageResource(MedicineTypeAux.getMedicineTypeIcon(medicineCanTake.getMedicine().getMedicineType().getCode()));

            GregorianCalendar auxTimeStartCounter = new GregorianCalendar();
            auxTimeStartCounter.setTimeInMillis(medicineCanTake.getStartTime().getTimeInMillis());
            auxTimeStartCounter.add(Calendar.HOUR_OF_DAY,-2);

            GregorianCalendar auxTimeValidTake = new GregorianCalendar();
            auxTimeValidTake.setTimeInMillis(medicineCanTake.getStartTime().getTimeInMillis());
            auxTimeValidTake.add(Calendar.HOUR_OF_DAY,-1);

            GregorianCalendar now = new GregorianCalendar();

            if(now.compareTo(auxTimeStartCounter) >= 0 && now.compareTo(medicineCanTake.getEndTime()) <= 0) {
                if(now.compareTo(auxTimeStartCounter) >= 0 && now.compareTo(auxTimeValidTake) < 0){
                    //isNormalWaiting
                    Log.d("MAIN","normal waiting");
                    Utils.changeBtnBackgroundMedicine(take_medicine_imageView,R.drawable.item_medicine_normal_btn_back_grey);

                    donut_progress_medicine_timer.setVisibility(View.VISIBLE);
                    donut_progress_medicine_timer.setMax(60);
                    Log.d("LOG_INIT_PROG",""+((now.getTimeInMillis() - auxTimeStartCounter.getTimeInMillis())/60000));
                    donut_progress_medicine_timer.setProgress((now.getTimeInMillis() - auxTimeStartCounter.getTimeInMillis())/60000);
                    countDownMedicineTimer = new CountDownTimer(auxTimeValidTake.getTimeInMillis() - now.getTimeInMillis(),60000) {
                        @Override
                        public void onTick(long l) {

                            donut_progress_medicine_timer.setProgress(60-(l/60000));
                            Log.d("LOG_NEW_PROG",""+(60-(l/60000)));
                        }

                        @Override
                        public void onFinish() {
                            setUpActiveMedicine(false);
                        }
                    };

                    countDownMedicineTimer.start();

                }else{
                    //normal
                    Log.d("MAIN","normal");
                    Utils.changeBtnBackgroundMedicine(take_medicine_imageView,R.drawable.item_medicine_normal_btn_back_green_to_take);
                    animate = true;

                    Log.d("END_TIME",medicineCanTake.getEndTime().getTimeInMillis()+"");
                    Log.d("NOW_TIME",now.getTimeInMillis()+"");

                    countDownMedicineTimer = new CountDownTimer(medicineCanTake.getEndTime().getTimeInMillis() - now.getTimeInMillis(),60000) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            Log.d("TIMER","TEMPO ACABOU");

                            setUpActiveMedicine(true);
                        }
                    };

                    countDownMedicineTimer.start();
                }
            }else{
                //future
                Log.d("MAIN","future");
                Utils.changeBtnBackgroundMedicine(take_medicine_imageView,R.drawable.item_medicine_future_btn_back);

                GregorianCalendar startTimeMinusOneHour = new GregorianCalendar();
                startTimeMinusOneHour.setTimeInMillis(medicineCanTake.getStartTime().getTimeInMillis());
                startTimeMinusOneHour.add(Calendar.HOUR_OF_DAY,-1);

                Log.d("END_TIME_F",startTimeMinusOneHour.getTimeInMillis()+"");
                Log.d("NOW_TIME_F",now.getTimeInMillis()+"");

                countDownMedicineTimer = new CountDownTimer(startTimeMinusOneHour.getTimeInMillis() - now.getTimeInMillis(), 60000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        Log.d("TIMER", "TEMPO ACABOU");
                        setUpActiveMedicine(false);

                        if(actual!=null &&  actual instanceof TimelineFragment){
                            ((TimelineFragment) actual).refreshTimeline();
                        }
                    }
                };

                countDownMedicineTimer.start();
            }

            if(animate) {
                Utils.animateBtnToTakeMedicine(this, take_medicine_imageView);
                take_medicine_imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showOverlayTakeMed(false);
                    }
                });
            }else{
                take_medicine_imageView.clearAnimation();
                take_medicine_imageView.setOnClickListener(null);
            }
        }else if(medicineCanTake != null && medicineCanTake.getPoll() != null) {
            findViewById(R.id.take_medicine_box).setVisibility(View.VISIBLE);

            Log.d("MAIN","Poll");

            take_medicine_imageView.setImageResource(medicineCanTake.getPoll().getPoolType().equals(Poll.POLL_TYPE_CARAT)?R.drawable.carat_timline_icon:R.drawable.poll_timeline_icon);

            boolean activeToTake = medicineCanTake.getStartTime().before(new GregorianCalendar());

            if(activeToTake) {
                if (medicineCanTake.getPoll().getPoolType().equals(Poll.POLL_TYPE_CARAT)) {
                    Utils.changeBtnBackgroundMedicine(take_medicine_imageView, R.drawable.item_poll_carat_btn_back);
                } else {
                    Utils.changeBtnBackgroundMedicine(take_medicine_imageView, R.drawable.item_poll_other_btn_back);
                }
            }else{
                Utils.changeBtnBackgroundMedicine(take_medicine_imageView, R.drawable.item_medicine_future_btn_back);
            }

            if(activeToTake) {
                Utils.animateBtnToTakeMedicine(this, take_medicine_imageView);
                take_medicine_imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (medicineCanTake.getPoll().getPoolType().equals(Poll.POLL_TYPE_CARAT)) {
                            Intent i = new Intent(MainActivity.this, CARATQuizActivity.class);
                            i.putExtra(CARATQuizActivity.EXTRA_POLL, medicineCanTake.getPoll());
                            i.putExtra(CARATQuizActivity.EXTRA_POLL_TIMELINE_ID, medicineCanTake.getId());

                            Utils.openIntent(MainActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                        } else {
                            Intent i = new Intent(MainActivity.this, SymptomsPollActivity.class);
                            i.putExtra(SymptomsPollActivity.EXTRA_POLL, medicineCanTake.getPoll());
                            i.putExtra(SymptomsPollActivity.EXTRA_POLL_TIMELINE_ID, medicineCanTake.getId());

                            Utils.openIntent(MainActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                        }
                    }
                });
            }else{
                take_medicine_imageView.clearAnimation();
                take_medicine_imageView.setOnClickListener(null);
            }
        } else{
            Log.d("MAIN","no med");
            take_medicine_imageView.setImageResource(R.drawable.no_meds);
            Utils.changeBtnBackgroundMedicine(take_medicine_imageView,R.drawable.no_med_to_take);
            findViewById(R.id.take_medicine_box).setVisibility(View.INVISIBLE);
        }
    }

    private void showOverlayTakeMed(final boolean isTimeLine){
        final View overlayTake = findViewById(R.id.overlay_take_med);
        overlayTake.setVisibility(View.VISIBLE);
        overlayTake.findViewById(R.id.view_take_med).setVisibility(View.VISIBLE);
        overlayTake.findViewById(R.id.view_reshcedule_med).setVisibility(View.GONE);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        final TimelineItem auxMed;
        if (isTimeLine){
            auxMed = ((TimelineFragment) actual).medicineToTakeAux;
            overlayTake.findViewById(R.id.layout_information).setVisibility(View.VISIBLE);
            overlayTake.findViewById(R.id.imageview_tutorial).setVisibility(View.GONE);
            overlayTake.findViewById(R.id.info_tutorial_text).setVisibility(View.GONE);
            overlayTake.findViewById(R.id.overlay_menu_option).setVisibility(View.VISIBLE);

            String auxDate = AppController.getmInstance().getString(R.string.overlay_take_date, timeFormat.format(auxMed.getStartTime().getTime()));
            ((TextView) overlayTake.findViewById(R.id.overlay_date_text)).setText(auxDate);
            ((TextView) overlayTake.findViewById(R.id.overlay_doses_text)).setText(getResources().getQuantityString(MedicineTypeAux.getTextResourceForTotal(auxMed.getMedicine().getMedicineType().getCode()), auxMed.getDosage(), auxMed.getDosage()));
            //((TextView) overlayTake.findViewById(R.id.overlay_last_text)).setText();

            final View showMenu = overlayTake.findViewById(R.id.overlay_menu_option);
            showMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(MainActivity.this, showMenu);
                    //inflating menu from xml resource

                    popup.inflate(R.menu.options_take);


                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu1:
                                    //handle menu1 click

                                    if(auxMed.getMedicine() != null){
                                        if(auxMed.getMedicine() instanceof UserNormalMedicine){
                                            Intent i = new Intent(MainActivity.this, AddMedicineActivity.class);
                                            i.putExtra(AddMedicineActivity.EXTRA_MEDICINE_EDIT,auxMed.getMedicine());

                                            Utils.openIntent(MainActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                                        }
                                    }

                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });
        }else{
            auxMed = medicineCanTake;
            overlayTake.findViewById(R.id.layout_information).setVisibility(View.GONE);
            overlayTake.findViewById(R.id.imageview_tutorial).setVisibility(View.VISIBLE);
            overlayTake.findViewById(R.id.info_tutorial_text).setVisibility(View.VISIBLE);
            overlayTake.findViewById(R.id.overlay_menu_option).setVisibility(View.GONE);
        }

        ((ImageView) overlayTake.findViewById(R.id.overlay_medicine_type_icon)).setImageResource(MedicineTypeAux.getMedicineTypeIcon(auxMed.getMedicine().getMedicineType().getCode()));
        ((TextView) overlayTake.findViewById(R.id.overlay_medicine_name)).setText(auxMed.getMedicine().getMedicineName());

        String auxDate = timeFormat.format(auxMed.getStartTime().getTime());
        ((TextView) overlayTake.findViewById(R.id.overlay_time_take_med)).setText(auxDate);
        //((TextView) overlayTake.findViewById(R.id.overlay_med_type)).setText(MedicineTypeAux.getTextResource(auxMed.getMedicine().getMedicineType().getCode()));
        ((TextView) overlayTake.findViewById(R.id.overlay_med_type)).setText(getResources().getQuantityString(MedicineTypeAux.getTextResourceForTotal(auxMed.getMedicine().getMedicineType().getCode()), auxMed.getDosage(), auxMed.getDosage()));

        overlayTake.findViewById(R.id.btn_take_med).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimeLine){
                    if(MedicineTypeAux.needsOpenRecognitionToTake(auxMed.getMedicine().getMedicineName(),auxMed.getMedicine().getMedicineType()) &&
                            auxMed.getRecognitionFailedTimes() < 2){
                        Intent intent = InhalerDetectionActivity.PrepareIntentInput(getBaseContext(),Long.parseLong(AppController.getmInstance().getActiveUser().getUid()), MedicineTypeAux.getRecognitionCodeToTake(auxMed.getMedicine().getMedicineName()),  AppController.INHALER_DETECTION_TOTAL, AppController.INHALER_DETECTION_MAX_TIME_SECONDS);
                        startActivityForResult(intent, INHALER_DETECTION_REQUEST);
                    }else {
                        ((TimelineFragment) actual).takeMedicineAux();
                    }
                }else{
                    if(medicineCanTake!=null){
                        if(MedicineTypeAux.needsOpenRecognitionToTake(medicineCanTake.getMedicine().getMedicineName(),medicineCanTake.getMedicine().getMedicineType()) && medicineCanTake.getRecognitionFailedTimes() < 2){
                            Intent intent = InhalerDetectionActivity.PrepareIntentInput(getBaseContext(),Long.parseLong(AppController.getmInstance().getActiveUser().getUid()), MedicineTypeAux.getRecognitionCodeToTake(medicineCanTake.getMedicine().getMedicineName()), AppController.INHALER_DETECTION_TOTAL, AppController.INHALER_DETECTION_MAX_TIME_SECONDS);
                            startActivityForResult(intent, INHALER_DETECTION_REQUEST);
                        }else {
                            takeMedicine();
                        }
                    }
                }
                overlayTake.setVisibility(View.GONE);
            }
        });

        overlayTake.findViewById(R.id.overlay_btn_reschedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayTake.findViewById(R.id.view_take_med).setVisibility(View.GONE);
                overlayTake.findViewById(R.id.view_reshcedule_med).setVisibility(View.VISIBLE);
                overlayTake.findViewById(R.id.overlay_menu_option).setVisibility(View.GONE);
                overlayTake.findViewById(R.id.imageview_tutorial).setVisibility(View.GONE);
                overlayTake.findViewById(R.id.info_tutorial_text).setVisibility(View.GONE);
            }
        });

        overlayTake.findViewById(R.id.overlay_btn_donttake).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayTake.setVisibility(View.GONE);

                final View overlayDontTake = findViewById(R.id.overlay_donttake_med);
                overlayDontTake.setVisibility(View.VISIBLE);

                RecyclerView recyclerView;
                LinearLayoutManager linearLayoutManager;
                linearLayoutManager = new LinearLayoutManager(MainActivity.this);

                missOptionsList = MissOption.getAllMissOptions();
                final ArrayList<MissOption> finalMissOptionsList = missOptionsList;
                optionAdapter = new MissOptionAdapter(MainActivity.this, missOptionsList, new MissOptionAdapter.MissOptionAdapterListener() {
                    @Override
                    public void onOptionClick(MissOption option) {
                        for (MissOption auxOption : finalMissOptionsList){
                            auxOption.setSelected(false);
                            if (auxOption.equals(option)){
                                auxOption.setSelected(true);
                            }
                        }
                        optionAdapter.notifyDataSetChanged();
                    }
                });

                recyclerView = findViewById(R.id.recyclerViewOptions);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(optionAdapter);

                overlayDontTake.findViewById(R.id.overlay_btn_miss_send).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String textMissOption = "";
                        for (MissOption option : missOptionsList){
                            if (option.isSelected()){
                                textMissOption = option.getName();
                            }
                        }

                        if (!textMissOption.equals("")){
                            if (AppController.getmInstance().getTimelineDataSource().updateTimelineStateNotes(auxMed.getId(), TimelineItem.STATE_MISSED, textMissOption)){
                                if(actual!=null &&  actual instanceof TimelineFragment){
                                    ((TimelineFragment) actual).reloadTimeline();
                                }
                                setUpActiveMedicine(true);
                                overlayDontTake.setVisibility(View.GONE);
                            }
                        }else{
                            Toast.makeText(MainActivity.this, R.string.select_option, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                overlayDontTake.findViewById(R.id.overlay_btn_miss_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        overlayDontTake.setVisibility(View.GONE);
                        overlayTake.setVisibility(View.VISIBLE);
                    }
                });

            }
        });

        overlayTake.findViewById(R.id.btn_overlay_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayTake.setVisibility(View.GONE);
            }
        });

        overlayTake.findViewById(R.id.ovarlay_drak_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayTake.setVisibility(View.GONE);
            }
        });

        overlayTake.findViewById(R.id.add_5min).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rescheduleTime(5, isTimeLine, auxMed, overlayTake);
            }
        });
        overlayTake.findViewById(R.id.add_10min).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rescheduleTime(10, isTimeLine, auxMed, overlayTake);
            }
        });
        overlayTake.findViewById(R.id.add_15min).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rescheduleTime(15, isTimeLine, auxMed, overlayTake);
            }
        });
        overlayTake.findViewById(R.id.add_30min).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rescheduleTime(30, isTimeLine, auxMed, overlayTake);
            }
        });
        overlayTake.findViewById(R.id.add_60min).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rescheduleTime(60, isTimeLine, auxMed, overlayTake);
            }
        });
        overlayTake.findViewById(R.id.add_120min).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rescheduleTime(120, isTimeLine, auxMed, overlayTake);
            }
        });

    }

    private void rescheduleTime(int minutes, boolean isTimeLine, TimelineItem auxMed, View overlayTake){
        GregorianCalendar auxStart = new GregorianCalendar();
        auxStart.setTimeInMillis(auxMed.getStartTime().getTimeInMillis());
        auxStart.add(Calendar.MINUTE,minutes);
        auxMed.setStartTime(auxStart);

        GregorianCalendar auxEnd = new GregorianCalendar();
        auxEnd.setTimeInMillis(auxMed.getEndTime().getTimeInMillis());
        auxEnd.add(Calendar.MINUTE,minutes);
        auxMed.setEndTime(auxEnd);

        if (AppController.getmInstance().getTimelineDataSource().changeTimelineItenDate(auxStart, auxEnd, auxMed.getId())){
            if(actual!=null &&  actual instanceof TimelineFragment){
                ((TimelineFragment) actual).reloadTimeline();
            }
        }

        overlayTake.setVisibility(View.GONE);
    }

    private void takeMedicine() {
        if(medicineCanTake!=null) {
            AppController.getmInstance().takeMedicine(medicineCanTake, true, -1, getSupportFragmentManager(), new OkErrorListener() {
                @Override
                public void ok() {
                    AppController.getmInstance().addTimelineItemNeedLocation(medicineCanTake);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getLastLocation();
                        }
                    });

                    //loadUserPoints();
                    setUpActiveMedicine(true);
                }

                @Override
                public void error() {
                    Toast.makeText(MainActivity.this, R.string.problem_takin_med, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void goToTimeline() {
        findViewById(R.id.main_menu_timeline_box).performClick();
    }

    public void goToPeople() {
        if(actual!= null && actual instanceof PeopleFragment){
            ((PeopleFragment) actual).loadInfo();
        }else {
            findViewById(R.id.main_menu_people_box).performClick();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == INHALER_DETECTION_REQUEST) {
            switch(resultCode) {
                case RESULT_OK:
                    int detectionCount = data.getIntExtra(InhalerDetectionActivity.EXTRA_RESULT_DETECTION_COUNT_INT, 0);
                    //vai buscar o "dosageCount" a data:
                    int dosageCount = data.getIntExtra(InhalerDetectionActivity.EXTRA_RESULT_DOSAGE_COUNT_INT, 0);


                    if (AppController.getmInstance().getInhalerDataSource().changeDosage(dosageCount, medicineCanTake.getMedicine().getInhalers().get(0).getId())){

                    }

                    //if(detectionCount >= AppController.INHALER_DETECTION_TOTAL){
                    takeMedicine();
                    //}else{
                    //    if(medicineCanTake!=null){
                    //        int failedTimes = medicineCanTake.getRecognitionFailedTimes()+1;
                    //        if(AppController.getmInstance().getTimelineDataSource().updateRecognitionFailedTimes(medicineCanTake.getId(),failedTimes)){
                    //            medicineCanTake.setRecognitionFailedTimes(failedTimes);
                    //        }
                    //    }
                    //}
                    break;
                case RESULT_CANCELED:
                    //todo implementar contador do inalador quando for enviado pelo reconhecimento
                    /*if(data != null && data.hasExtra(InhalerDetectionActivity.EXTRA_RESULT_DETECTION_COUNT_INT)) {
                        //do stuff
                    } else {
                        //do stuff
                    }*/
                    break;
                default:
                    break;
            }
        } if(requestCode == LOCATION_SETTINGS_REQUEST){
            getLastLocation();
        } else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void getLastLocation(){
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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
                    AppController.getmInstance().forceSyncManual();
                    //finish();
                }
            });
            dialog.show();
        }

        return gps_enabled || network_enabled;
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
                    AppController.getmInstance().forceSyncManual();
                    //finish();
                }
            }
        }else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    @Override
    public void onLastLocationAsk() {
        getLastLocation();
    }

    @Override
    public void onMedTakePressed() {
        showOverlayTakeMed(true);
    }

    public void notificationMessage(String uid) {
        if(actual!=null){
            if(actual instanceof PeopleFragment){
                ((PeopleFragment) actual).notificationMessage(uid);
            }
        }
    }

    public void resetMessages(String uid) {
        if(actual!=null){
            if(actual instanceof PeopleFragment){
                ((PeopleFragment) actual).resetMessages(uid);
            }
        }
    }

    public boolean isPeopleFragment() {
        return actual != null && actual instanceof PeopleFragment;
    }
}
