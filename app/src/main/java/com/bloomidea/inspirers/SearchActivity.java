package com.bloomidea.inspirers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.adapter.SearchAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.LinearLayoutManagerWithSmoothScroller;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.customViews.SimpleDividerItemDecoration;
import com.bloomidea.inspirers.events.GodfatherAskedEvent;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.listener.JSONObjectListener;
import com.bloomidea.inspirers.model.Request;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.InspirersJSONParser;
import com.bloomidea.inspirers.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends MyActiveActivity {
    public static final String EXTRA_SEARCH_TEXT = "EXTRA_SEARCH_TEXT";

    private String textToSearch;

    private RecyclerView searchRecyclerView;
    private LinearLayoutManagerWithSmoothScroller linearLayoutManager;
    private SearchAdapter searchAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        textToSearch = getIntent().getStringExtra(EXTRA_SEARCH_TEXT);

        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.icon_imageView).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.title_textView)).setText(R.string.search);

        searchRecyclerView = (RecyclerView) findViewById(R.id.seacrh_recyclerView);
        linearLayoutManager = new LinearLayoutManagerWithSmoothScroller(this);
        searchAdapter = new SearchAdapter(this, new SearchAdapter.SearchAdapterListener() {
            @Override
            public void askBtnClick(User user, int positionOnListItems) {
                final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(SearchActivity.this);

                APIInspirers.askGodfather(user.getUid(), new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ringProgressDialogNoText.dismiss();

                        Utils.createNavigationAction(getString(R.string.send_request_inspirer));

                        AppController.getmInstance().getMyBus().send(new GodfatherAskedEvent(response.optString("nid"),Request.STATUS_WAITING,Request.TYPE_GODCHILD));

                        onBackPressed();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ringProgressDialogNoText.dismiss();
                        Toast.makeText(SearchActivity.this,R.string.problem_send_to_server,Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        searchRecyclerView.setLayoutManager(linearLayoutManager);
        searchRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        searchRecyclerView.setAdapter(searchAdapter);

        loadInfo();
    }

    private void loadInfo(){
        final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(this);
        APIInspirers.searchGodfathers(textToSearch, AppController.getmInstance().getActiveUser().getUid(), new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {
                ringProgressDialogNoText.dismiss();

                ArrayList<User> suggestions = InspirersJSONParser.parseListUsers(response);

                searchAdapter.addAllSuggestedList(suggestions);

                if (suggestions.isEmpty()){
                    findViewById(R.id.no_info_layout).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.no_info_layout).setVisibility(View.GONE);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialogNoText.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
