package com.bloomidea.inspirers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.adapter.ListPollAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.listener.TreeMapListener;
import com.bloomidea.inspirers.model.ListPoll;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.InspirersJSONParser;
import com.bloomidea.inspirers.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.TreeMap;

public class CARATListActivity extends MyActiveActivity {
    private RecyclerView questionsRecyclerView;
    private ListPollAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caratlist);

        configTopMenu();

        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(this);

        APIInspirers.requestMyCaratList(AppController.getmInstance().getActiveUser().getUid(), new JSONArrayListener() {
            @Override
            public void onResponse(final JSONArray responseArray) {
                ringProgressDialogNoText.dismiss();
                AppController.getmInstance().getPolls(new TreeMapListener() {
                    @Override
                    public void onResponse(TreeMap response) {
                        ArrayList<ListPoll> list = InspirersJSONParser.parseListPollAnswered(responseArray,(Poll) response.get(Poll.POLL_TYPE_CARAT));

                        questionsRecyclerView = (RecyclerView) findViewById(R.id.carat_list_recyclerView);

                        adapter = new ListPollAdapter(CARATListActivity.this, list, new ListPollAdapter.ListPollListener() {
                            @Override
                            public void openPoll(ListPoll poll) {
                                Intent i = new Intent(CARATListActivity.this, CARATQuizActivity.class);
                                i.putExtra(CARATQuizActivity.EXTRA_POLL, poll.getPoll());
                                i.putExtra(CARATQuizActivity.EXTRA_VIEW_ANSWER, true);
                                i.putExtra(CARATQuizActivity.EXTRA_CAN_COMMNET,false);
                                i.putExtra(CARATQuizActivity.EXTRA_VIEW_ANSWER_MY_POLL_ID, poll.getMyPollId());

                                Utils.openIntent(CARATListActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                            }
                        });

                        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(CARATListActivity.this));

                        questionsRecyclerView.setAdapter(adapter);

                        if(list.isEmpty()){
                            ((TextView) findViewById(R.id.no_info_textView)).setText(R.string.no_pools);
                            findViewById(R.id.no_info_layout).setVisibility(View.VISIBLE);
                            questionsRecyclerView.setVisibility(View.GONE);
                        }else{
                            findViewById(R.id.no_info_layout).setVisibility(View.GONE);
                            questionsRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error",error.toString());
                ringProgressDialogNoText.dismiss();
                Toast.makeText(CARATListActivity.this,R.string.problem_communicating_with_server, Toast.LENGTH_SHORT).show();
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

        ((TextView) findViewById(R.id.title_textView)).setText(R.string.polls);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
