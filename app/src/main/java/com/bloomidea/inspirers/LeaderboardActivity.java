package com.bloomidea.inspirers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.adapter.LeaderboardAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.LinearLayoutManagerWithSmoothScroller;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.customViews.SimpleDividerItemDecoration;
import com.bloomidea.inspirers.events.ResetMessageCounter;
import com.bloomidea.inspirers.events.ReviewCreatedEvent;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.InspirersJSONParser;
import com.bloomidea.inspirers.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import rx.functions.Action1;

public class LeaderboardActivity extends MyActiveActivity {

    private int myRank = 0;

    private RecyclerView leaderboardRecyclerView;
    private LinearLayoutManagerWithSmoothScroller leaderboardLayoutManager;
    private LeaderboardAdapter leaderboardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ((ImageView) findViewById(R.id.icon_imageView)).setImageResource(R.drawable.leader_icon2);
        ((TextView) findViewById(R.id.title_textView)).setText(R.string.leaderboard);


        loadInfo();

        configureEventListener();
    }

    private void configureEventListener() {
        AppController.getmInstance().getMyBus().toObserverable()
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {

                        if(event instanceof ReviewCreatedEvent) {
                            ReviewCreatedEvent eventAux = (ReviewCreatedEvent) event;

                            if(leaderboardAdapter != null){
                                leaderboardAdapter.updateReviewsInfo(eventAux.getUid(), eventAux.getNewReview());
                            }
                        }if(event instanceof ResetMessageCounter){
                            ResetMessageCounter eventAux = (ResetMessageCounter) event;

                            if(leaderboardAdapter != null){
                                leaderboardAdapter.resetCounter(eventAux.getUid());
                            }
                        }
                    }
                });
    }

    private void loadInfo() {
        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(this);

        APIInspirers.getUserRank(AppController.getmInstance().getActiveUser().getUid(), new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    myRank = response.getJSONObject(0).getInt("rank");
                    APIInspirers.getLeaderboardList(AppController.getmInstance().getActiveUser().getUid(), new JSONArrayListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d("res1",response.toString());
                            ArrayList<User> leaderBoard = InspirersJSONParser.parseListUsers(response);

                            leaderboardRecyclerView = (RecyclerView) findViewById(R.id.leader_recyclerview);

                            leaderboardLayoutManager = new LinearLayoutManagerWithSmoothScroller(LeaderboardActivity.this);
                            leaderboardAdapter = new LeaderboardAdapter(LeaderboardActivity.this,leaderBoard,myRank);

                            leaderboardRecyclerView.setLayoutManager(leaderboardLayoutManager);

                            leaderboardRecyclerView.setAdapter(leaderboardAdapter);

                            leaderboardRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(LeaderboardActivity.this));

                            ringProgressDialogNoText.dismiss();
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dealWithError(ringProgressDialogNoText);
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                    dealWithError(ringProgressDialogNoText);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                dealWithError(ringProgressDialogNoText);
            }
        });
    }

    private void dealWithError(ProgressDialog ringProgressDialogNoText){
        ringProgressDialogNoText.dismiss();
        Toast.makeText(LeaderboardActivity.this,R.string.error_getting_info,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void notificationMessage(String uid) {
        if(leaderboardAdapter!=null){
            leaderboardAdapter.addUnreadMessage(uid,1);
        }
    }

    public void resetMessages(String uid) {
        if(leaderboardAdapter!=null){
            leaderboardAdapter.resetCounter(uid);
        }
    }
}
