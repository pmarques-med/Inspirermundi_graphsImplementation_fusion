package com.bloomidea.inspirers;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.model.Answer;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.model.Question;
import com.bloomidea.inspirers.model.QuestionMultipleChoice;
import com.bloomidea.inspirers.utils.Utils;

public class CARATScoreActivity extends MyActiveActivity {
    public static final String EXTRA_POLL = "EXTRA_POLL";
    public static final String EXTRA_MY_POLL_ID = "EXTRA_POLL_ID";
    public static final String EXTRA_MY_POLL_COMMENT = "EXTRA_MY_POLL_COMMENT";
    public static final String EXTRA_CAN_COMMENT = "EXTRA_CAN_COMMENT";
    public static final String EXTRA_FROM_QUESTIONS = "EXTRA_FROM_QUESTIONS";


    private Poll poll;
    private long myPollID;
    private String myPollComment;
    private boolean fromQuestions = false;

    private boolean canComment = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caratscore);

        poll = (Poll) getIntent().getSerializableExtra(EXTRA_POLL);
        myPollID = getIntent().getLongExtra(EXTRA_MY_POLL_ID,-1);
        myPollComment = getIntent().getStringExtra(EXTRA_MY_POLL_COMMENT);
        canComment = getIntent().getBooleanExtra(EXTRA_CAN_COMMENT,true);
        fromQuestions = getIntent().getBooleanExtra(EXTRA_FROM_QUESTIONS,false);

        if(myPollComment==null){
            myPollComment = "";
        }

        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.view_answer_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fromQuestions) {
                    onBackPressed();
                }else{
                    Intent i = new Intent(CARATScoreActivity.this, CARATQuizActivity.class);
                    i.putExtra(CARATQuizActivity.EXTRA_POLL, poll);
                    i.putExtra(CARATQuizActivity.EXTRA_VIEW_ANSWER, true);
                    i.putExtra(CARATQuizActivity.EXTRA_CAN_COMMNET,false);
                    i.putExtra(CARATQuizActivity.EXTRA_VIEW_ANSWER_MY_POLL_ID, myPollID);
                    i.putExtra(CARATQuizActivity.EXTRA_FROM_SCORE, true);

                    Utils.openIntent(CARATScoreActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                }
            }
        });

        if(!canComment){
            findViewById(R.id.end_btn).setVisibility(View.GONE);
            ((EditText) findViewById(R.id.poll_note_editText)).setEnabled(false);
        }else {
            findViewById(R.id.end_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String comment = ((EditText) findViewById(R.id.poll_note_editText)).getText().toString();

                    if (comment != null && !comment.isEmpty() && !myPollComment.equals(comment)) {
                        if (AppController.getmInstance().getPollDataSource().updateMyPollComment(myPollID, comment)) {
                            AppController.getmInstance().forceSyncManual();
                        }

                    }

                    setResult(RESULT_OK);
                    finish();
                }
            });
        }

        ((TextView) findViewById(R.id.view_answer_btn)).setText(Html.fromHtml("<u>"+getString(R.string.view_answer)+"</u>"));

        int totalScore = 0;
        int scoreUpper = 0;
        int scoreLower = 0;

        int count = 1;

        QuestionMultipleChoice auxQ;
        for(Question q : poll.getListQuestions()){
            auxQ = (QuestionMultipleChoice) q;

            for(Answer a : auxQ.getListAnswers()){
                if(a.isSelected()){
                    if(a.getNid().equals("14")){
                        totalScore += 3;
                        if (count >= 5){
                            scoreLower += 3;
                        }else{
                            scoreUpper += 3;
                        }
                    }else if(a.getNid().equals("15")){
                        totalScore += 2;
                        if(count >= 5){
                            scoreLower += 2;
                        }else{
                            scoreUpper += 2;
                        }
                    }else if(a.getNid().equals("16")){
                        totalScore += 1;
                        if(count >= 5){
                            scoreLower += 1;
                        }else{
                            scoreUpper += 1;
                        }
                    }else if(a.getNid().equals("19")){
                        totalScore += 2;
                        scoreLower += 2;
                    }else if(a.getNid().equals("18")){
                        totalScore += 3;
                        scoreLower += 3;
                    }
                }
            }

            count++;
        }

        ((TextView) findViewById(R.id.total_score_textView)).setText(""+totalScore);
        ((TextView) findViewById(R.id.total_score_textView)).setTextColor(Color.parseColor((totalScore > 24 ?"#00cccc":"#ff3333")));

        ((TextView) findViewById(R.id.upper_score_textView)).setText(""+scoreUpper);
        ((TextView) findViewById(R.id.upper_score_textView)).setTextColor(Color.parseColor((scoreUpper > 8 ? "#00cccc":"#ff3333")));

        ((TextView) findViewById(R.id.lower_score_textView)).setText(""+scoreLower);
        ((TextView) findViewById(R.id.lower_score_textView)).setTextColor(Color.parseColor((scoreLower >= 16 ?"#00cccc":"#ff3333")));

        if(myPollComment!=null) {
            ((EditText) findViewById(R.id.poll_note_editText)).setText(myPollComment);
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
}
