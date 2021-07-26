package com.bloomidea.inspirers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.adapter.WarriorsAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.customViews.ProgressBar;
import com.bloomidea.inspirers.customViews.SimpleDividerItemDecoration;
import com.bloomidea.inspirers.events.ReviewCreatedEvent;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.listener.JSONObjectListener;
import com.bloomidea.inspirers.listener.OkErrorListener;
import com.bloomidea.inspirers.model.Request;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.swipeLayout.util.Attributes;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.InspirersJSONParser;
import com.bloomidea.inspirers.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import rx.functions.Action1;

public class WarriorsActivity extends MyActiveActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private WarriorsAdapter warriorsAdapter;

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warriors);

        configTopMenu();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) findViewById(R.id.warriors_recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        warriorsAdapter = new WarriorsAdapter(this, new WarriorsAdapter.WarriorsAdapterListener() {
            @Override
            public void askBtnClick(User user,int positionOnListItems) {
                if(Utils.isOnline(WarriorsActivity.this,true,getSupportFragmentManager())) {
                    askForAux(user, positionOnListItems);
                }
            }

            @Override
            public void acceptBtnClick(Request request, int positionOnListItems) {
                if(Utils.isOnline(WarriorsActivity.this,true,getSupportFragmentManager())) {
                    acceptWarrior(request, positionOnListItems);
                }
            }

            @Override
            public void denyBtnClick(Request request, int positionOnListItems) {
                if(Utils.isOnline(WarriorsActivity.this,true,getSupportFragmentManager())) {
                    denyWarrior(request, positionOnListItems);
                }
            }

            @Override
            public void deleteBtnClick(Request request, int positionOnListItems) {
                if(Utils.isOnline(WarriorsActivity.this,true,getSupportFragmentManager())) {
                    deleteWarrior(request, positionOnListItems);
                }
            }
        });

        warriorsAdapter.setMode(Attributes.Mode.Single);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration( this));
        recyclerView.setAdapter(warriorsAdapter);

        initInfo();

        AppController.getmInstance().getMyBus().toObserverable()
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {

                        if(event instanceof ReviewCreatedEvent) {
                            ReviewCreatedEvent eventAux = (ReviewCreatedEvent) event;

                            if(warriorsAdapter != null){
                                warriorsAdapter.updateReviewsInfo(eventAux.getUid(), eventAux.getNewReview());
                            }
                        }
                    }
                });
    }

    private void denyWarrior(Request request, final int positionOnListItems) {
        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(WarriorsActivity.this);
        APIInspirers.declineWarrior(request.getNid(), new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                ringProgressDialogNoText.dismiss();
                warriorsAdapter.warriorDenied(positionOnListItems);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialogNoText.dismiss();
                Toast.makeText(WarriorsActivity.this, R.string.problem_send_to_server, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteWarrior(Request request, final int positionOnListItems) {
        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(WarriorsActivity.this);
        APIInspirers.deleteWarrior(request.getNid(), new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                ringProgressDialogNoText.dismiss();
                warriorsAdapter.warriorDeleted(positionOnListItems);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialogNoText.dismiss();
                Toast.makeText(WarriorsActivity.this, R.string.problem_send_to_server, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void acceptWarrior(Request request, final int positionOnListItems) {
        if (Utils.canOpenGodchild(AppController.getmInstance().getActiveUser().getLevel(), AppController.getmInstance().getActiveUser().getNumGodChilds())) {
            AppController.getmInstance().acceptNewWarrior(request, this, getSupportFragmentManager(), new OkErrorListener() {
                @Override
                public void ok() {
                    User activeUser = AppController.getmInstance().getActiveUser();
                    int myRequestTotal = activeUser.getNumGodChilds();

                    if (!(activeUser.getLevel() >= 10 && (myRequestTotal + activeUser.getNumGodChilds()) == 0) ||
                            !(activeUser.getLevel() >= 20 && (myRequestTotal + activeUser.getNumGodChilds()) <= 1) ||
                            !(activeUser.getLevel() >= 30 && (myRequestTotal + activeUser.getNumGodChilds()) <= 2) ||
                            !(activeUser.getLevel() >= 40 && (myRequestTotal + activeUser.getNumGodChilds()) <= 3) ||
                            !(activeUser.getLevel() >= 50 && (myRequestTotal + activeUser.getNumGodChilds()) <= 4)) {
                        onBackPressed();
                    } else {
                        //request.accepted();
                        warriorsAdapter.warriorAccepted(positionOnListItems);
                    }
                }

                @Override
                public void error() {
                    Toast.makeText(WarriorsActivity.this, R.string.problem_send_to_server, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Utils.showMessageAlertDialog(this,null,Utils.cantAcceptGodchildReason(this, AppController.getmInstance().getActiveUser().getLevel()),null);
        }
    }

    private void askForAux(User user, final int positionOnListItems) {
        User activeUser = AppController.getmInstance().getActiveUser();
        int myRequestTotal = warriorsAdapter.totalByMe();

        if ((activeUser.getLevel() >= 10 && (myRequestTotal + activeUser.getNumGodChilds()) == 0) ||
            (activeUser.getLevel() >= 20 && (myRequestTotal + activeUser.getNumGodChilds()) <= 1) ||
            (activeUser.getLevel() >= 30 && (myRequestTotal + activeUser.getNumGodChilds()) <= 2) ||
            (activeUser.getLevel() >= 40 && (myRequestTotal + activeUser.getNumGodChilds()) <= 3) ||
            (activeUser.getLevel() >= 50 && (myRequestTotal + activeUser.getNumGodChilds()) <= 4)){

            final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(WarriorsActivity.this);

            APIInspirers.askWarrior(user.getUid(), new JSONObjectListener() {
                @Override
                public void onResponse(JSONObject response) {
                    ringProgressDialogNoText.dismiss();

                    Utils.createNavigationAction(getString(R.string.send_request_warrior));

                    warriorsAdapter.warriorAsked(response.optString("nid"),Request.STATUS_WAITING,Request.TYPE_GODFATHER,positionOnListItems);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    ringProgressDialogNoText.dismiss();
                    Toast.makeText(WarriorsActivity.this,R.string.problem_send_to_server,Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            if(myRequestTotal != 0) {
                Utils.showMessageAlertDialog(this, null, getString(R.string.alert_request_full_w), null);
            }else{
                Utils.showMessageAlertDialog(this,null,Utils.cantOpenGodchildReason(this,activeUser.getLevel()),null);
            }
            //Toast.makeText(this,R.string.alert_request_full_w,Toast.LENGTH_LONG).show();
        }
    }


    private void initInfo(){
        final String uid = AppController.getmInstance().getActiveUser().getUid();

        progressBar.setVisibility(View.VISIBLE);

        APIInspirers.warriorsRequestedToMe(uid, new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<Request> results = InspirersJSONParser.parseListRequest(response);

                warriorsAdapter.addAllToMeList(results);

                APIInspirers.warriorsRequestedByMe(uid, new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Request> results = InspirersJSONParser.parseListRequest(response);

                        warriorsAdapter.addAllByMeList(results);

                        APIInspirers.warriorsRequestsSuggested(AppController.getmInstance().getActiveUser().getUid(), new JSONArrayListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                ArrayList<User> results = InspirersJSONParser.parseListUsers(response);

                                warriorsAdapter.addAllSuggestedList(results);

                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(WarriorsActivity.this,R.string.problem_communicating_with_server,Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(WarriorsActivity.this,R.string.problem_communicating_with_server,Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(WarriorsActivity.this,R.string.problem_communicating_with_server,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configTopMenu() {
        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ((TextView) findViewById(R.id.title_textView)).setText(R.string.collect_warriors);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void reloadRequests(){
        warriorsAdapter.clearAll();
        initInfo();
    }
}
