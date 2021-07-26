package com.bloomidea.inspirers;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.BorderProgressBar;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.events.LogoutEvent;
import com.bloomidea.inspirers.events.MedicineCreatedEvent;
import com.bloomidea.inspirers.events.MedicinesChangedEvent;
import com.bloomidea.inspirers.events.NumGodchildsUpdated;
import com.bloomidea.inspirers.events.ReviewCreatedEvent;
import com.bloomidea.inspirers.events.UserBadgeWon;
import com.bloomidea.inspirers.events.UserInfoUpdated;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.model.Level;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.model.UserBadge;
import com.bloomidea.inspirers.model.UserMedicine;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.MedicineTypeAux;
import com.bloomidea.inspirers.utils.Utils;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.github.ornolfr.ratingview.RatingView;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

import rx.functions.Action1;

import static com.bloomidea.inspirers.R.id.level_progress_bar;
import static com.bloomidea.inspirers.R.id.level_textview;
import static com.bloomidea.inspirers.R.id.total_points;
import static com.bloomidea.inspirers.application.AppController.getmInstance;

public class UserProfileActivity extends MyActiveActivity {
    public static final String EXTRA_USER_PROFILE = "EXTRA_USER_PROFILE";
    private String reportEmail = "inspirermundi@gmail.com";

    private boolean isActiveUser;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        reportEmail = getString(R.string.report_email);

        Object aux = getIntent().getSerializableExtra(EXTRA_USER_PROFILE);

        if(aux==null){
            setActiveUserToLoad();
        }else{
            isActiveUser = false;
            user = (User) aux;

            if(user.getUid().equals(getmInstance().getActiveUser().getUid())){
                setActiveUserToLoad();
            }
        }

        configTopMenu();

        loadUserInfo();

        configureEventListener();
    }

    private void configureEventListener() {
        AppController.getmInstance().getMyBus().toObserverable()
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {

                        if(event instanceof ReviewCreatedEvent) {
                            ReviewCreatedEvent eventAux = (ReviewCreatedEvent) event;

                            if(user.getUid().equals(eventAux.getUid())){
                                if(!isActiveUser) {
                                    user.updateReviewsInfo(eventAux.getNewReview());
                                }

                                loadReviewInfo();
                            }
                        }else {
                            if (isActiveUser) {
                                if (event instanceof UserBadgeWon) {
                                    configureBadgesWon();
                                } else if (event instanceof NumGodchildsUpdated) {
                                    loadTotalWarriors();
                                } else if(event instanceof UserInfoUpdated || event instanceof MedicinesChangedEvent || event instanceof MedicineCreatedEvent){
                                    setActiveUserToLoad();
                                    loadUserInfo();
                                }else if(event instanceof LogoutEvent){
                                    finish();
                                }
                            }
                        }
                    }
                });
    }

    private void loadUserInfo(){
        Utils.loadImageView(this, (ImageView) findViewById(R.id.profile_image), (ProgressBar) findViewById(R.id.profile_image_progress), user.getPictureUrl(), user.getPicture(), R.drawable.default_avatar, null);

        ((DonutProgress) findViewById(R.id.donut_progress)).setFinishedStrokeColor(Color.parseColor(user.getStatsEverColor()));
        ((DonutProgress) findViewById(R.id.donut_progress)).setProgress(user.getStatsEver().floatValue());

        ((TextView) findViewById(R.id.user_name_textView)).setText(Html.fromHtml(user.getUserName()));

        loadReviewInfo();

        Utils.loadImageView(this, (ImageView) findViewById(R.id.country_flag_imageView), null, user.getCountryFlagUrl(), user.getCountryFlag(), R.drawable.default_country, null);

        if(user.getCountryName()!=null && !user.getCountryName().isEmpty()) {
            ((TextView) findViewById(R.id.country_name_textView)).setText(user.getCountryName());
        }

        //points
        Level nextLevel = AppController.getmInstance().getNextLevel(user.getLevel());
        Level actualLevel = AppController.getmInstance().getLevel(user.getLevel());

        int nextLevelPoints = nextLevel.getPoints();

        int maxP = nextLevelPoints-actualLevel.getPoints();
        int actCal =maxP-(nextLevelPoints-user.getUserPoints());


        ((TextView) findViewById(total_points)).setText(""+user.getUserPoints());


        ((BorderProgressBar) findViewById(level_progress_bar)).setCurProgress(actCal);
        ((BorderProgressBar) findViewById(level_progress_bar)).setMaxProgress(maxP);

        ((TextView) findViewById(level_textview)).setText(this.getResources().getString(R.string.level_text, user.getLevel()));
        //end points

        ((TextView) findViewById(R.id.progress_percent_textView)).setTextColor(Color.parseColor(user.getStatsEverColor()));
        ((TextView) findViewById(R.id.progress_percent_textView)).setText(user.getStatsEver().intValue()+"%");
        ((TextView) findViewById(R.id.average_ever_percent_textView)).setText(isActiveUser?R.string.my_average_ever_percent:R.string.average_ever_percent);

        if(isActiveUser){
            findViewById(R.id.eval_info_points).setVisibility(View.GONE);
            findViewById(R.id.message_menu).setVisibility(View.VISIBLE);
            findViewById(R.id.add_warrior_line).setVisibility(View.VISIBLE);
            findViewById(R.id.message_menu).setVisibility(View.GONE);
            findViewById(R.id.network_menu).setVisibility(View.VISIBLE);
            //findViewById(R.id.add_warrior_menu).setVisibility(View.VISIBLE);

            findViewById(R.id.network_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Utils.isOnline(UserProfileActivity.this,true, UserProfileActivity.this.getSupportFragmentManager())) {
                        Intent i = new Intent(UserProfileActivity.this, PeopleActivity.class);

                        Utils.openIntent(UserProfileActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                    }
                }
            });

            findViewById(R.id.add_warrior_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Utils.isOnline(UserProfileActivity.this,true,getSupportFragmentManager())) {
                        /*if ((user.getLevel() >= 10 && user.getNumGodChilds() == 0) ||
                                (user.getLevel() >= 20 && user.getNumGodChilds() <= 1) ||
                                (user.getLevel() >= 30 && user.getNumGodChilds() <= 2) ||
                                (user.getLevel() >= 40 && user.getNumGodChilds() <= 3) ||
                                (user.getLevel() >= 50 && user.getNumGodChilds() <= 4)) {*/
                        if(Utils.canOpenGodchild(user.getLevel(),user.getNumGodChilds())){

                            Intent i = new Intent(UserProfileActivity.this, WarriorsActivity.class);

                            Utils.openIntent(UserProfileActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);

                        } else {
                            /*int level = 0;
                            if(user.getLevel() < 10){
                                level = 10;
                            }else if(user.getLevel() < 20){
                                level = 20;
                            }else if(user.getLevel() < 30){
                                level = 30;
                            }else if(user.getLevel() < 40){
                                level = 40;
                            }else if(user.getLevel() < 50){
                                level = 50;
                            }

                            if(level == 0){
                                Toast.makeText(UserProfileActivity.this, getResources().getString(R.string.order_warrior_no_more), Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(UserProfileActivity.this, getResources().getString(R.string.order_warrior_error, "" + level), Toast.LENGTH_SHORT).show();
                            }*/
                            Toast.makeText(UserProfileActivity.this, Utils.cantOpenGodchildReason(UserProfileActivity.this, user.getLevel()), Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });
            findViewById(R.id.active_user_menus).setVisibility(View.VISIBLE);

            findViewById(R.id.my_sintoms_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Utils.isOnline(UserProfileActivity.this,true,UserProfileActivity.this.getSupportFragmentManager())) {
                        Intent i = new Intent(UserProfileActivity.this, SymptomsListActivity.class);

                        Utils.openIntent(UserProfileActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                    }
                }
            });

            /*
            findViewById(R.id.my_pools_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Utils.isOnline(UserProfileActivity.this,true,UserProfileActivity.this.getSupportFragmentManager())) {
                        Intent i = new Intent(UserProfileActivity.this, CARATListActivity.class);

                        Utils.openIntent(UserProfileActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                    }
                }
            });
            */
            findViewById(R.id.my_meds_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(UserProfileActivity.this, MyMedsActivity.class);

                    Utils.openIntent(UserProfileActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                }
            });

            //configureMyMeds();
        }else{
            configureMessagesCounter(user.getUnreadMessages());

            findViewById(R.id.message_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(user.isConnected()){
                        if(Utils.isOnline(UserProfileActivity.this,true,getSupportFragmentManager())) {
                            Intent i = new Intent(UserProfileActivity.this, MessagesActivity.class);
                            i.putExtra(MessagesActivity.EXTRA_USER, user);

                            Utils.openIntent(UserProfileActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);

                            user.setUnreadMessages(0);
                            configureMessagesCounter(0);
                        }
                    }else{
                        Toast.makeText(UserProfileActivity.this, R.string.connection_needed, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


        findViewById(R.id.evaluation_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.isConnected() || isActiveUser){
                    if(Utils.isOnline(UserProfileActivity.this,true,getSupportFragmentManager())) {
                        Intent i = new Intent(UserProfileActivity.this, ReviewsActivity.class);
                        i.putExtra(ReviewsActivity.EXTRA_USER_NAME, user.getUserName());
                        i.putExtra(ReviewsActivity.EXTRA_ACTIVE_USER, isActiveUser);
                        i.putExtra(ReviewsActivity.EXTRA_REVIEWS_NID, user.getNidProfile());
                        i.putExtra(ReviewsActivity.EXTRA_TOTAL_REVIEWS, user.getTotalRating());
                        i.putExtra(ReviewsActivity.EXTRA_USER_UID, user.getUid());

                        Utils.openIntent(UserProfileActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                    }
                }else{
                    Toast.makeText(UserProfileActivity.this, R.string.connection_needed, Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.statistics_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserProfileActivity.this, StatisticsActivity.class);

                if(!isActiveUser) {
                    i.putExtra(StatisticsActivity.EXTRA_USER, user);
                }

                Utils.openIntent(UserProfileActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        loadTotalWarriors();

        if(user.getLanguagesStringWithSeparator("\n").isEmpty()){
            findViewById(R.id.languages_text).setVisibility(View.GONE);
        }else{
            findViewById(R.id.languages_text).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.languages_text)).setText(""+user.getLanguagesStringWithSeparator("\n"));
        }

        if(user.getHobbiesStringWithSeparator("\n").isEmpty()){
            findViewById(R.id.interests_text).setVisibility(View.GONE);
        }else{
            findViewById(R.id.interests_text).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.interests_text)).setText(user.getHobbiesStringWithSeparator("\n"));
        }


        configureBadgesWon();
    }

    private void configureMessagesCounter(int totalUnred) {
        Utils.configureCounter((TextView) findViewById(R.id.messages_notif), totalUnred);
    }

    private void loadTotalWarriors() {
        ((TextView) findViewById(R.id.total_warriors_text)).setText(""+user.getNumGodChilds());
    }

    private void loadReviewInfo() {
        ((RatingView) findViewById(R.id.evaluation_ratingBar)).setRating(user.getMediaAvlTreated5Stars().floatValue());
        ((TextView) findViewById(R.id.evaluation_textView)).setText(getResources().getQuantityString(R.plurals.evaluation_text,user.getTotalRating(),user.getTotalRating()));

        ((TextView) findViewById(R.id.evaluation_text)).setText(getResources().getString(R.string.evaluations_with_total,""+user.getTotalRating()));
    }

    private void configureMyMeds() {
        findViewById(R.id.my_meds_line).setVisibility(View.VISIBLE);
        findViewById(R.id.my_meds_layout).setVisibility(View.VISIBLE);

        ArrayList<UserMedicine> auxList = AppController.getmInstance().getMedicineDataSource().getUserMedicines(user.getId());

        int totalFixed = 3;
        LinearLayout fixedMeds = (LinearLayout) findViewById(R.id.fixed_meds);
        LinearLayout expandable = ((LinearLayout) findViewById(R.id.expand_items_meds));

        fixedMeds.removeAllViews();
        expandable.removeAllViews();

        int i = 1;
        for(UserMedicine userMed : auxList){
            View aux = LayoutInflater.from(this).inflate(R.layout.layout_profile_medicine,null);
            ((ImageView) aux.findViewById(R.id.medicine_imageView)).setImageResource(MedicineTypeAux.getMedicineTypeIcon(userMed.getMedicineType().getCode()));
            ((TextView) aux.findViewById(R.id.medicine_text_textView)).setText(userMed.getMedicineName());

            if(i<=totalFixed){
                fixedMeds.addView(aux);
            }else{
                expandable.addView(aux);
            }
            i++;
        }

        if(auxList.size()>totalFixed){
            findViewById(R.id.expand_meds).setVisibility(View.VISIBLE);
            findViewById(R.id.expand_meds).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ExpandableLayout) findViewById(R.id.expandable_layout_meds)).toggle(true);
                }
            });
            ((ExpandableLayout) findViewById(R.id.expandable_layout_meds)).setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
                @Override
                public void onExpansionUpdate(float expansionFraction) {
                    if(expansionFraction == 0){
                        ((TextView) findViewById(R.id.expand_meds)).setText(Html.fromHtml("<u>"+getString(R.string.view_more)+"</u>"));
                    }else{
                        ((TextView) findViewById(R.id.expand_meds)).setText(Html.fromHtml("<u>"+getString(R.string.view_less)+"</u>"));
                    }
                }
            });
        }else{
            findViewById(R.id.expand_meds).setVisibility(View.GONE);
        }
    }

    private void configureBadgesWon() {
        int totalFixed = 3;
        LinearLayout fixedBadges = (LinearLayout) findViewById(R.id.fixed_badges);
        LinearLayout expandable = ((LinearLayout) findViewById(R.id.expand_items));
        fixedBadges.removeAllViews();
        expandable.removeAllViews();

        ArrayList<UserBadge> auxList = user.getUserBadges();

        int i = 1;
        for(UserBadge b : auxList){
            View aux = LayoutInflater.from(this).inflate(R.layout.layout_profile_badge,null);
            ((ImageView) aux.findViewById(R.id.badge_imageView)).setImageResource(b.getBadge().getImgResourceId());
            ((TextView) aux.findViewById(R.id.badge_text_textView)).setText(b.getBadge().getName());

            if(i<=totalFixed){
                fixedBadges.addView(aux);
            }else{
                expandable.addView(aux);
            }
            i++;
        }

        if(auxList.size()>totalFixed){
            findViewById(R.id.expand).setVisibility(View.VISIBLE);
            findViewById(R.id.expand).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ExpandableLayout) findViewById(R.id.expandable_layout)).toggle(true);
                }
            });
            ((ExpandableLayout) findViewById(R.id.expandable_layout)).setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
                @Override
                public void onExpansionUpdate(float expansionFraction) {
                    if(expansionFraction == 0){
                        ((TextView) findViewById(R.id.expand)).setText(Html.fromHtml("<u>"+getString(R.string.view_more)+"</u>"));
                    }else{
                        ((TextView) findViewById(R.id.expand)).setText(Html.fromHtml("<u>"+getString(R.string.view_less)+"</u>"));
                    }
                }
            });
        }else{
            findViewById(R.id.expand).setVisibility(View.GONE);
        }
    }

    private void configTopMenu() {
        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(isActiveUser){
//            findViewById(R.id.settings_btn_imageView).setVisibility(View.VISIBLE);
//            findViewById(R.id.settings_btn_imageView).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(UserProfileActivity.this, SettingsActivity.class);
//
//                    Utils.openIntent(UserProfileActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
//                }
//            });

            findViewById(R.id.edit_btn_imageView).setVisibility(View.VISIBLE);
            findViewById(R.id.edit_btn_imageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(UserProfileActivity.this, EditProfileActivity.class);

                    Utils.openIntent(UserProfileActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                }
            });
        }else {
            findViewById(R.id.options_btn_imageView).setVisibility(View.VISIBLE);
            findViewById(R.id.options_btn_imageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reportProfile();
                }
            });
        }

        findViewById(R.id.icon_imageView).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.title_textView)).setText(isActiveUser?R.string.my_profile:R.string.user_profile);
    }

    private void reportProfile() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{reportEmail});
                        i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.report_profile_subject,user.getUserName()));
                        //i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                        try {
                            Utils.createNavigationAction(getString(R.string.report_perfil));

                            startActivity(Intent.createChooser(i, getResources().getString(R.string.send_email)));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(UserProfileActivity.this, R.string.no_email_client, Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.report_profile_question).setPositiveButton(R.string.report, dialogClickListener)
                .setNegativeButton(R.string.cancel, dialogClickListener).show();
    }

    private void setActiveUserToLoad() {
        isActiveUser = true;
        user = AppController.getmInstance().getActiveUser();

        reloadUserReviewsInfo();
    }

    private void reloadUserReviewsInfo() {
        APIInspirers.requestAvaliationNidForUser(user.getUid(), new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length()>0) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(0);

                        int total = jsonObject.getInt("total");
                        BigDecimal avr = new BigDecimal(jsonObject.getDouble("media"));

                        user.setReviewInfo(total, avr);
                        loadReviewInfo();

                        AppController.getmInstance().updateUserReviewsInfo(user.getId(), total, avr);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error",error.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void notificationMessage(String uid) {
        if(!isActiveUser && user!= null && user.getUid().equals(uid)){
            user.setUnreadMessages(user.getUnreadMessages()+1);

            configureMessagesCounter(user.getUnreadMessages());
        }
    }

    public void resetMessages(String uid) {
        if(!isActiveUser && user!= null && user.getUid().equals(uid)){
            user.setUnreadMessages(0);

            configureMessagesCounter(0);
        }
    }
}
