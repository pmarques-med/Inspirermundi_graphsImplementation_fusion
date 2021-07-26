package com.bloomidea.inspirers.customViews;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.VolleyError;

import com.bloomidea.inspirers.AddMedicineActivity;
import com.bloomidea.inspirers.AddSOSMedicineActivity;
import com.bloomidea.inspirers.CARATListActivity;
import com.bloomidea.inspirers.CARATQuizActivity;
import com.bloomidea.inspirers.CARATScoreActivity;
import com.bloomidea.inspirers.EditProfileActivity;
import com.bloomidea.inspirers.InviteDoctorActivity;
import com.bloomidea.inspirers.LeaderboardActivity;
import com.bloomidea.inspirers.MainActivity;
import com.bloomidea.inspirers.MessagesActivity;
import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.ReviewsActivity;
import com.bloomidea.inspirers.SearchActivity;
import com.bloomidea.inspirers.SettingsActivity;
import com.bloomidea.inspirers.StatisticsActivity;
import com.bloomidea.inspirers.SymptomsListActivity;
import com.bloomidea.inspirers.SymptomsPollActivity;
import com.bloomidea.inspirers.UserProfileActivity;
import com.bloomidea.inspirers.WarriorsActivity;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.listener.InternalBroadcastListener;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.model.NavAux;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.InspirersJSONParser;
import com.bloomidea.inspirers.utils.InternalBroadcastReceiver;
import com.bloomidea.inspirers.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.GregorianCalendar;

/**
 * Created by michellobato on 21/04/17.
 */

public class MyActiveActivity extends AppCompatActivity {
    public static final String NAVIGATION_TYPE_SCREEN = "Screen";
    public static final String NAVIGATION_TYPE_ACTION = "Action";


    private NavAux auxNav = null;
    private InternalBroadcastReceiver mMessageReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessageReceiver = new InternalBroadcastReceiver(new InternalBroadcastListener() {
            @Override
            public void userMessage(String uid, String msg,String messageNotif) {
                showMessageRecived(uid,msg,messageNotif);
            }

            @Override
            public void medicineToTake() {
                showMedicineTaken();
            }

            @Override
            public void removedGodfather(String messageNotif) {
                showRemovedGodFather(messageNotif, true);
            }

            @Override
            public void requestGodchild(String messageNotif) {
                showRequesteGodchild(messageNotif);
            }

            @Override
            public void acceptedGodchild(String messageNotif) {
                showAcceptedGodchild(messageNotif);
            }

            @Override
            public void buzzRecived(String messageNotif) {
                showBuzzRecived(messageNotif);
            }
        });
    }

    private void showMessageRecived(final String uid, String msg,String messageNotif) {
        boolean openDialog = true;

        if(this instanceof MessagesActivity){
            MessagesActivity act = (MessagesActivity) this;

            if(act.isSameUser(uid)){
                act.addMessageRecived(msg);
                openDialog = false;
            }
        }

        if(openDialog){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MyActiveActivity.this);
            builder1.setMessage(messageNotif);
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    R.string.open,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(MyActiveActivity.this);
                            APIInspirers.getUserInfo(uid,null,null, new JSONArrayListener() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    ringProgressDialogNoText.dismiss();
                                    User userProfile = null;
                                    try {
                                        userProfile = InspirersJSONParser.parseUser2(response.getJSONObject(0),false,null,null,false);

                                        Intent messages = new Intent(MyActiveActivity.this, MessagesActivity.class);
                                        messages.putExtra(MessagesActivity.EXTRA_USER, userProfile);

                                        Utils.openIntent(MyActiveActivity.this, messages, R.anim.slide_in_left, R.anim.slide_out_left);

                                        if(MyActiveActivity.this instanceof MainActivity){
                                            ((MainActivity) MyActiveActivity.this).resetMessages(uid);
                                        }else if(MyActiveActivity.this instanceof UserProfileActivity){
                                            ((UserProfileActivity) MyActiveActivity.this).resetMessages(uid);
                                        }else if(MyActiveActivity.this instanceof LeaderboardActivity){
                                            ((LeaderboardActivity) MyActiveActivity.this).resetMessages(uid);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    ringProgressDialogNoText.dismiss();
                                }
                            });

                        }
                    });

            builder1.setNeutralButton(
                    R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                            if(MyActiveActivity.this instanceof MainActivity){
                                ((MainActivity) MyActiveActivity.this).notificationMessage(uid);
                            }else if(MyActiveActivity.this instanceof UserProfileActivity){
                                ((UserProfileActivity) MyActiveActivity.this).notificationMessage(uid);
                            }else if(MyActiveActivity.this instanceof LeaderboardActivity){
                                ((LeaderboardActivity) MyActiveActivity.this).notificationMessage(uid);
                            }
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    private void showMedicineTaken() {
        // MARK5: Show new alert medicine


        AlertDialog.Builder builder1 = new AlertDialog.Builder(MyActiveActivity.this);
        builder1.setMessage(R.string.message_medicine_to_take);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                R.string.open,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        if(MyActiveActivity.this instanceof MainActivity){
                            ((MainActivity) MyActiveActivity.this).goToTimeline();
                        }else {
                            Intent a = new Intent(MyActiveActivity.this, MainActivity.class);
                            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(a);
                        }
                    }
                });

        builder1.setNeutralButton(
                R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void showRequesteGodchild(String messageNotif) {
        if(AppController.getmInstance().getActiveUser()!=null) {
            //if (Utils.canOpenGodchild(AppController.getmInstance().getActiveUser().getLevel(), AppController.getmInstance().getActiveUser().getNumGodChilds())) {

                if (MyActiveActivity.this instanceof WarriorsActivity) {
                    ((WarriorsActivity) MyActiveActivity.this).reloadRequests();
                }

                AlertDialog.Builder builder1 = new AlertDialog.Builder(MyActiveActivity.this);
                builder1.setMessage(messageNotif);
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        R.string.open,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if (MyActiveActivity.this instanceof WarriorsActivity) {
                                    //((WarriorsActivity) MyActiveActivity.this).reloadRequests();
                                } else {
                                    Intent warriors = new Intent(MyActiveActivity.this, WarriorsActivity.class);

                                    Utils.openIntent(MyActiveActivity.this, warriors, R.anim.slide_in_left, R.anim.slide_out_left);
                                }
                            }
                        });

                builder1.setNeutralButton(
                        R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        //}
    }

    private void showAcceptedGodchild(String messageNotif) {
        //Go to main menu people too
        showRemovedGodFather(messageNotif, true);
    }

    private void showBuzzRecived(String messageNotif) {
        //Go to main menu people too
        showRemovedGodFather(messageNotif, false);
    }

    private void showRemovedGodFather(String messageNotif, final boolean goToPeopleTab) {
        if (MyActiveActivity.this instanceof WarriorsActivity) {
            ((WarriorsActivity) MyActiveActivity.this).reloadRequests();
        }else if(MyActiveActivity.this instanceof MainActivity){
            if(goToPeopleTab && ((MainActivity) this).isPeopleFragment()) {
                ((MainActivity) MyActiveActivity.this).goToPeople();
            }
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(MyActiveActivity.this);
        builder1.setMessage(messageNotif);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                R.string.open,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (MyActiveActivity.this instanceof MainActivity) {
                            if (goToPeopleTab) {
                                if(!((MainActivity) MyActiveActivity.this).isPeopleFragment()) {
                                    ((MainActivity) MyActiveActivity.this).goToPeople();
                                }
                            } else {
                                ((MainActivity) MyActiveActivity.this).goToTimeline();
                            }
                        } else {
                            Intent a = new Intent(MyActiveActivity.this, MainActivity.class);

                            if (goToPeopleTab) {
                                a.putExtra(MainActivity.EXTRA_TAB_OPEN, MainActivity.TAB_PEOPLE);
                            }

                            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(a);
                        }
                    }
                });

        builder1.setNeutralButton(
                R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.activityResumed();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(InternalBroadcastReceiver.INTENT_FILTER_ACTION));

        User active = AppController.getmInstance().getActiveUser();

        if(active!=null) {
            if (this instanceof ReviewsActivity) {
                auxNav = new NavAux(null, active.getId(), getString(R.string.avaliation_view), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof CARATQuizActivity){
                auxNav = new NavAux(null, active.getId(), getString(R.string.carat_form_view), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof EditProfileActivity){
                auxNav = new NavAux(null, active.getId(), getString(R.string.edit_perfil_view), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof AddMedicineActivity){
                auxNav = new NavAux(null, active.getId(), getString(R.string.create_med), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof InviteDoctorActivity){
                auxNav = new NavAux(null, active.getId(), getString(R.string.invite_doctor_view), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof LeaderboardActivity){
                auxNav = new NavAux(null, active.getId(), getString(R.string.leaderboard_view), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof MessagesActivity){
                auxNav = new NavAux(null, active.getId(), getString(R.string.mensage_view), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof UserProfileActivity){
                auxNav = new NavAux(null, active.getId(), getString(R.string.perfil_view), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof SearchActivity){
                auxNav = new NavAux(null, active.getId(), getString(R.string.search_view), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof AddSOSMedicineActivity){
                auxNav = new NavAux(null, active.getId(), getString(R.string.create_sos), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof SettingsActivity){
                auxNav = new NavAux(null, active.getId(), getString(R.string.settings_view), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof SymptomsPollActivity){
                auxNav = new NavAux(null, active.getId(), getString(R.string.sint_form_view), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof StatisticsActivity){
                auxNav = new NavAux(null, active.getId(), getString(R.string.user_statics_view), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof CARATScoreActivity){
                auxNav = new NavAux(null, active.getId(), getString(R.string.submit_varat_view), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof CARATListActivity){
                auxNav = new NavAux(null, active.getId(), getString(R.string.list_carat), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof SymptomsListActivity){
                auxNav = new NavAux(null, active.getId(), getString(R.string.list_sint), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }else if(this instanceof WarriorsActivity){
                auxNav = new NavAux(null, active.getId(), getString(R.string.request_godchild), new GregorianCalendar(), null, NAVIGATION_TYPE_SCREEN);
            }

            if (auxNav != null) {
                AppController.getmInstance().getNavigationDataSource().createNavigation(auxNav);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        AppController.activityPaused();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        //AppController.activityPaused();

        if(auxNav!=null) {
            auxNav.setEndTime(new GregorianCalendar());
            AppController.getmInstance().getNavigationDataSource().updateNavigationEndTime(auxNav);

            auxNav = null;
        }
    }
}
