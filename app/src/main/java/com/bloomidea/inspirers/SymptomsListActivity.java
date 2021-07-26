package com.bloomidea.inspirers;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.adapter.ListPollAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.model.ListPoll;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.utils.ListPollsLoader;
import com.bloomidea.inspirers.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;

public class SymptomsListActivity extends MyActiveActivity implements LoaderManager.LoaderCallbacks<List<ListPoll>>{
    private static final int TOTAL_TO_LOAD = 50;

    private RecyclerView questionsRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ListPollAdapter adapter;

    private ArrayList<ListPoll> listDaily = new ArrayList<>();
    private ArrayList<ListPoll> listWeekly = new ArrayList<>();
    private ArrayList<ListPoll> listCARAT = new ArrayList<>();


    private boolean hasMoreDaily = true;
    private boolean loadingDaily = false;
    private int pageDaily = 0;


    private boolean hasMoreWeekly = true;
    private boolean loadingWeekly = false;
    private int pageWeekly = 0;

    private boolean hasMoreCARAT = true;
    private boolean loadingCARAT = false;
    private int pageCARAT = 0;

    private ProgressBar loadingProgress;

    private boolean optionDaily = false;
    private boolean optionWeekly = false;
    private boolean optionCarat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms_list);

        loadingProgress = (ProgressBar) findViewById(R.id.loadingProgress);

        configTopMenu();

        questionsRecyclerView = (RecyclerView) findViewById(R.id.carat_list_recyclerView);

        adapter = new ListPollAdapter(this, new ArrayList<ListPoll>(), new ListPollAdapter.ListPollListener() {
            @Override
            public void openPoll(ListPoll poll) {
                if(poll.getPoll().getPoolType().equals(Poll.POLL_TYPE_CARAT)) {
                    /*Intent i = new Intent(SymptomsListActivity.this, CARATQuizActivity.class);
                    i.putExtra(CARATQuizActivity.EXTRA_POLL, poll.getPoll());
                    i.putExtra(CARATQuizActivity.EXTRA_VIEW_ANSWER, true);
                    i.putExtra(CARATQuizActivity.EXTRA_CAN_COMMNET, false);
                    i.putExtra(CARATQuizActivity.EXTRA_VIEW_ANSWER_MY_POLL_ID, poll.getMyPollId());
                    i.putExtra(CARATQuizActivity.EXTRA_VIEW_ANSWER_MY_POLL_COMMENT, poll.getComment());

                    Utils.openIntent(SymptomsListActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);*/
                    Intent i = new Intent(SymptomsListActivity.this, CARATScoreActivity.class);
                    i.putExtra(CARATScoreActivity.EXTRA_POLL, poll.getPoll());
                    i.putExtra(CARATScoreActivity.EXTRA_FROM_QUESTIONS, false);
                    i.putExtra(CARATScoreActivity.EXTRA_CAN_COMMENT, false);
                    i.putExtra(CARATScoreActivity.EXTRA_MY_POLL_ID, poll.getMyPollId());
                    i.putExtra(CARATScoreActivity.EXTRA_MY_POLL_COMMENT, poll.getComment());

                    Utils.openIntent(SymptomsListActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                }else{
                    Intent i = new Intent(SymptomsListActivity.this, SymptomsPollActivity.class);
                    i.putExtra(SymptomsPollActivity.EXTRA_POLL, poll.getPoll());
                    i.putExtra(SymptomsPollActivity.EXTRA_VIEW_ANSWER, true);

                    Utils.openIntent(SymptomsListActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                }
            }
        });

        linearLayoutManager = new LinearLayoutManager(this);
        questionsRecyclerView.setLayoutManager(linearLayoutManager);

        questionsRecyclerView.setAdapter(adapter);

        questionsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (linearLayoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
                    Log.d("LoadMore", "Scroll");
                    if(optionCarat){
                        loadInfoCARAT(true);
                    }else if(optionDaily){
                        loadInfoDaily(true);
                    }else if(optionWeekly){
                        loadInfoWeekly(true);
                    }
                }
            }
        });

        ((SegmentedGroup)findViewById(R.id.segmented)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i == R.id.button_weekly){
                    loadInfoWeeklyAux();
                }else if(i ==  R.id.button_carat) {
                    loadInfoCARATAux();
                }else{
                    loadInfoDailyAux();
                }
            }
        });

        findViewById(R.id.button_daily).performClick();
    }

    /*
    private void loadInfoDaily() {
        if(listDaily==null || listDaily.isEmpty()){
            final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(this);

            APIInspirers.requestMySintList(AppController.getmInstance().getActiveUser().getUid(), Poll.POLL_TYPE_DAILY, new JSONArrayListener() {
                @Override
                public void onResponse(final JSONArray responseArray) {
                    ringProgressDialogNoText.dismiss();
                    //Log.d("t",responseArray.toString());

                    AppController.getmInstance().getPolls(new TreeMapListener() {
                        @Override
                        public void onResponse(TreeMap response) {
                            listDaily = InspirersJSONParser.parseListPollAnswered(responseArray,(Poll) response.get(Poll.POLL_TYPE_DAILY));

                            adapter.setListPolls(listDaily);

                            verifyInfo(listDaily.isEmpty());
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    ringProgressDialogNoText.dismiss();
                    Toast.makeText(SymptomsListActivity.this,R.string.problem_communicating_with_server, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            adapter.setListPolls(listDaily);
            verifyInfo(listDaily.isEmpty());
        }
    }*/

    private void verifyInfo() {
        if(adapter.getItemCount() == 0){
            ((TextView) findViewById(R.id.no_info_textView)).setText(R.string.no_pools);
            findViewById(R.id.no_info_layout).setVisibility(View.VISIBLE);
            questionsRecyclerView.setVisibility(View.GONE);
        }else{
            findViewById(R.id.no_info_layout).setVisibility(View.GONE);
            questionsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    /*
    private void loadInfoWeekly() {
        if(listWeekly==null || listWeekly.isEmpty()){
            final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(this);

            APIInspirers.requestMySintList(AppController.getmInstance().getActiveUser().getUid(), Poll.POLL_TYPE_WEEKLY, new JSONArrayListener() {
                @Override
                public void onResponse(final JSONArray responseArray) {
                    ringProgressDialogNoText.dismiss();
                    //Log.d("t",responseArray.toString());

                    AppController.getmInstance().getPolls(new TreeMapListener() {
                        @Override
                        public void onResponse(TreeMap response) {
                            listWeekly = InspirersJSONParser.parseListPollAnswered(responseArray,(Poll) response.get(Poll.POLL_TYPE_WEEKLY));
                            adapter.setListPolls(listWeekly);

                            verifyInfo();
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    ringProgressDialogNoText.dismiss();
                    Toast.makeText(SymptomsListActivity.this,R.string.problem_communicating_with_server, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            adapter.setListPolls(listWeekly);
            verifyInfo(listWeekly.isEmpty());
        }
    }*/

    /*
    private void loadInfoCARAT() {
        if(listCARAT==null || listCARAT.isEmpty()){
            final ProgressDialog ringProgressDialogNoText = Utils.createRingProgressDialogNoText(this);

            APIInspirers.requestMyCaratList(AppController.getmInstance().getActiveUser().getUid(), new JSONArrayListener() {
                @Override
                public void onResponse(final JSONArray responseArray) {
                    ringProgressDialogNoText.dismiss();
                    AppController.getmInstance().getPolls(new TreeMapListener() {
                        @Override
                        public void onResponse(TreeMap response) {
                            listCARAT = InspirersJSONParser.parseListPollAnswered(responseArray,(Poll) response.get(Poll.POLL_TYPE_CARAT));
                            adapter.setListPolls(listCARAT);

                            verifyInfo(listCARAT.isEmpty());
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });

                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    ringProgressDialogNoText.dismiss();
                    Toast.makeText(SymptomsListActivity.this,R.string.problem_communicating_with_server, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            adapter.setListPolls(listCARAT);
            verifyInfo();
        }
    }
    */
    private void configTopMenu() {
        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ((TextView) findViewById(R.id.title_textView)).setText(R.string.your_pools_general);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public Loader<List<ListPoll>> onCreateLoader(int id, Bundle args) {
        Poll auxPoll;
        int pageAux;
        if(optionWeekly){
            auxPoll = AppController.getmInstance().getPollModel(Poll.POLL_TYPE_WEEKLY);
            pageAux = pageWeekly;
        }else if(optionDaily){
            auxPoll = AppController.getmInstance().getPollModel(Poll.POLL_TYPE_DAILY);
            pageAux = pageDaily;
        }else{
            //optionCARAT
            auxPoll = AppController.getmInstance().getPollModel(Poll.POLL_TYPE_CARAT);
            pageAux = pageCARAT;
        }

        return new ListPollsLoader(this,TOTAL_TO_LOAD,pageAux,auxPoll);
    }

    @Override
    public void onLoadFinished(Loader<List<ListPoll>> loader, List<ListPoll> data) {
        ArrayList<ListPoll> list = (ArrayList<ListPoll>) data;

        if(optionDaily){
            hasMoreDaily = list.size() == TOTAL_TO_LOAD;
            listDaily.addAll(list);
            loadingDaily = false;
        }else if(optionWeekly){
            hasMoreWeekly = list.size() == TOTAL_TO_LOAD;
            listWeekly.addAll(list);
            loadingWeekly = false;
        }else{
            //optionCARAT
            hasMoreCARAT = list.size() == TOTAL_TO_LOAD;
            listCARAT.addAll(list);
            loadingCARAT = false;
        }

        Log.d("HAS_MORE",hasMoreDaily+"-"+hasMoreWeekly+"-"+hasMoreCARAT);

        adapter.notifyDataSetChanged();
        loadingProgress.setVisibility(View.GONE);
        verifyInfo();
    }

    @Override
    public void onLoaderReset(Loader<List<ListPoll>> loader) {

    }

    private void loadInfoDailyAux(){
        optionDaily = true;
        optionWeekly = false;
        optionCarat = false;

        adapter.setListPolls(listDaily);

        if(listDaily == null || listDaily.isEmpty()){
            loadInfoDaily(false);
        }else{
            verifyInfo();
        }
    }

    public void loadInfoDaily(boolean loadMore) {
        if(!loadingDaily && hasMoreDaily) {
            loadingDaily=true;

            loadingProgress.setVisibility(View.VISIBLE);

            if(loadMore){
                pageDaily+=1;
            }

            getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
        }else{
            verifyInfo();
        }
    }

    private void loadInfoWeeklyAux(){
        optionDaily = false;
        optionWeekly = true;
        optionCarat = false;

        adapter.setListPolls(listWeekly);

        if(listWeekly == null || listWeekly.isEmpty()){
            loadInfoWeekly(false);
        }else{
            verifyInfo();
        }
    }

    public void loadInfoWeekly(boolean loadMore) {
        if(!loadingWeekly && hasMoreWeekly) {
            loadingWeekly=true;

            loadingProgress.setVisibility(View.VISIBLE);

            if(loadMore){
                pageWeekly+=1;
            }

            getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
        }else{
            verifyInfo();
        }
    }

    private void loadInfoCARATAux(){
        optionDaily = false;
        optionWeekly = false;
        optionCarat = true;

        adapter.setListPolls(listCARAT);

        if(listCARAT == null || listCARAT.isEmpty()){
            loadInfoCARAT(false);
        }else{
            verifyInfo();
        }
    }

    public void loadInfoCARAT(boolean loadMore) {
        if(!loadingCARAT && hasMoreCARAT) {
            loadingCARAT=true;

            loadingProgress.setVisibility(View.VISIBLE);

            if(loadMore){
                pageCARAT+=1;
            }

            getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
        }else{
            verifyInfo();
        }
    }
}
