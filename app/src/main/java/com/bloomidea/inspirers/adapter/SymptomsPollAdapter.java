package com.bloomidea.inspirers.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.model.Question;
import com.bloomidea.inspirers.model.QuestionSlider;
import com.bloomidea.inspirers.model.QuestionYesNo;

import java.util.Arrays;

/**
 * Created by michellobato on 27/04/17.
 */

public class SymptomsPollAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TOP = 101;
    private static final int TYPE_SLIDER = 102;
    private static final int TYPE_YES_NO = 103;

    private Activity context;
    private Poll poll;
    private boolean viewAnswers;

    private final String[] QUESTION_VERIFY_SOS_NID = new String[]{"305","6324"};

    private final String[] QUESTION_SHOW_NID = new String[]{"306","6325"};
    private boolean showLastQuestionIf306 = false;

    private SymptomsPollAdapter.SymptomsPollAdapterInteractionListener listenerAux;

    private boolean firstTime = true;

    public SymptomsPollAdapter(Activity context, Poll poll, boolean viewAnswers, SymptomsPollAdapterInteractionListener listenerAux) {
        this.context = context;
        this.poll = poll;
        this.viewAnswers = viewAnswers;
        this.listenerAux = listenerAux;


        if(poll.getPoolType().equals(Poll.POLL_TYPE_DAILY)){
            if(viewAnswers){
                for(Question q : poll.getListQuestions()){
                    if (Arrays.asList(QUESTION_SHOW_NID).contains(q.getNid())) {
                        if(q instanceof QuestionYesNo){
                            QuestionYesNo questionAux = (QuestionYesNo) q;
                            showLastQuestionIf306 = (questionAux.isYes()!=null && questionAux.isYes());
                        }
                    }
                }
            }else{
                showLastQuestionIf306 = false;
            }
        }else{
            showLastQuestionIf306 = true;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position==0?TYPE_TOP:(poll.getListQuestions().get(position-1).getType().equals(Question.QUESTION_TYPE_YES_NO)?TYPE_YES_NO:TYPE_SLIDER);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        RecyclerView.ViewHolder viewHolder;

        if (viewType == TYPE_TOP) {
            view = LayoutInflater.from(context).inflate(R.layout.item_symptoms_top, parent, false);
            viewHolder = new ViewHolderTop(view);
        } else if(viewType == TYPE_SLIDER){
            view = LayoutInflater.from(context).inflate(R.layout.item_symptoms_slider, parent, false);
            viewHolder = new ViewHolderSlider(view);
        }else{
            //TYPE_YES_NO
            view = LayoutInflater.from(context).inflate(R.layout.item_symptoms_yes_no, parent, false);
            viewHolder = new ViewHolderYesNo(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderTop){
            ((ViewHolderTop) holder).back_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listenerAux.goBack();
                }
            });
        }else {
            //ViewHolderQuestion
            Question auxQuestion = poll.getListQuestions().get(position-1);

            ViewHolderQuestion auxHolderQuestion = (ViewHolderQuestion) holder;
            boolean isLast = (position == (getItemCount()-1));

            auxHolderQuestion.setIsLast(isLast, viewAnswers);

            if(isLast && !viewAnswers) {
                auxHolderQuestion.end_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listenerAux.end(poll);
                    }
                });
            }else{
                auxHolderQuestion.end_btn.setOnClickListener(null);
            }

            auxHolderQuestion.question_textView.setText(Html.fromHtml(auxQuestion.getQuestion()));

            if (holder instanceof ViewHolderSlider) {
                ViewHolderSlider auxHolder = (ViewHolderSlider) holder;
                auxHolder.seek_bar.setTag(R.id.tag_position,position-1);
                auxHolder.seek_bar.setTag(R.id.tag_viewholder,auxHolder);

                if(viewAnswers){
                    Drawable thumb = context.getResources().getDrawable( R.drawable.custom_seek_thumb );
                    auxHolder.seek_bar.setThumb(thumb);
                    auxHolder.seek_bar.setProgress(((QuestionSlider) auxQuestion).getTotalSelected());
                    auxHolder.seek_bar.setEnabled(false);
                }else{
                    auxHolder.seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            if(b) {
                                Drawable thumb = context.getResources().getDrawable( R.drawable.custom_seek_thumb );
                                ((ViewHolderSlider) seekBar.getTag(R.id.tag_viewholder)).seek_bar.setThumb(thumb);

                                QuestionSlider q = (QuestionSlider) poll.getListQuestions().get((int) seekBar.getTag(R.id.tag_position));
                                q.setTotalSelected(i,true);
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                }

            } else {
                //ViewHolderYesNo
                ViewHolderYesNo auxHolder = (ViewHolderYesNo) holder;
                auxHolder.view_group.setTag(R.id.tag_position,position-1);
                if(viewAnswers){
                    QuestionYesNo questionAux = (QuestionYesNo) auxQuestion;
                    auxHolder.view_group.check(questionAux.isYes()!=null && questionAux.isYes()?R.id.yes_radiobutton:R.id.no_radiobutton);
                    auxHolder.yes_radiobutton.setEnabled(false);
                    auxHolder.no_radiobutton.setEnabled(false);
                }else {
                    auxHolder.view_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                            QuestionYesNo q = (QuestionYesNo) poll.getListQuestions().get((int) radioGroup.getTag(R.id.tag_position));
                            q.setYes(i == R.id.yes_radiobutton);

                            if(poll.getPoolType().equals(Poll.POLL_TYPE_DAILY)) {
                                if (Arrays.asList(QUESTION_SHOW_NID).contains(q.getNid())) {
                                    if(q.isYes()) {
                                        showLastQuestionIf306 = true;

                                        Question lastQ = poll.getListQuestions().get(poll.getListQuestions().size()-1);
                                        if(lastQ instanceof QuestionSlider) {
                                            ((QuestionSlider) lastQ).setTotalSelected(0, false);
                                        }


                                        notifyDataSetChanged();
                                    }else{
                                        showLastQuestionIf306 = false;
                                        Question lastQ = poll.getListQuestions().get(poll.getListQuestions().size()-1);

                                        if(lastQ instanceof QuestionSlider) {
                                            ((QuestionSlider) lastQ).setTotalSelected(0, true);
                                        }

                                        notifyDataSetChanged();
                                    }
                                }else if(Arrays.asList(QUESTION_VERIFY_SOS_NID).contains(q.getNid())){
                                    if(q.isYes() && !AppController.getmInstance().getTimelineDataSource().existSOSLast24H()){
                                        listenerAux.showCreateSOSMedicineQuestion();
                                    }
                                }
                            }
                        }
                    });

                    if(firstTime && poll.getPoolType().equals(Poll.POLL_TYPE_WEEKLY) && position-1==0){
                        if(AppController.getmInstance().getActiveUser().getWeekPollAnswer()!=null) {
                            firstTime = false;
                            auxHolder.view_group.check(AppController.getmInstance().getActiveUser().getWeekPollAnswer()?R.id.yes_radiobutton:R.id.no_radiobutton);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return poll.getListQuestions().size() + (showLastQuestionIf306?0:-1) + 1;
    }


    public static class ViewHolderTop extends RecyclerView.ViewHolder {
        public View back_btn;

        public ViewHolderTop(View v) {
            super(v);

            back_btn = v.findViewById(R.id.back_btn);
        }
    }

    public static class ViewHolderQuestion extends RecyclerView.ViewHolder {
        public TextView question_textView;
        public View line;
        public View end_btn;

        public ViewHolderQuestion(View v) {
            super(v);

            line = v.findViewById(R.id.line);
            end_btn = v.findViewById(R.id.end_btn);
            question_textView = (TextView) v.findViewById(R.id.question_textView);

        }

        public void setIsLast(boolean isLast, boolean viewAnswers) {
            if(isLast){
                line.setVisibility(View.GONE);
                if(viewAnswers) {
                    end_btn.setVisibility(View.INVISIBLE);
                }else{
                    end_btn.setVisibility(View.VISIBLE);
                }
            }else{
                line.setVisibility(View.VISIBLE);
                end_btn.setVisibility(View.GONE);
            }
        }
    }

    public static class ViewHolderSlider extends ViewHolderQuestion {
        public SeekBar seek_bar;

        public ViewHolderSlider(View v) {
            super(v);

            seek_bar = (SeekBar) v.findViewById(R.id.seek_bar);
        }
    }

    public static class ViewHolderYesNo extends ViewHolderQuestion {
        public RadioGroup view_group;
        public RadioButton yes_radiobutton;
        public RadioButton no_radiobutton;

        public ViewHolderYesNo(View v) {
            super(v);

            view_group = (RadioGroup) v.findViewById(R.id.view_group);
            yes_radiobutton = (RadioButton) v.findViewById(R.id.yes_radiobutton);
            no_radiobutton = (RadioButton) v.findViewById(R.id.no_radiobutton);
        }
    }

    public interface SymptomsPollAdapterInteractionListener {
        void end(Poll poll);
        void showCreateSOSMedicineQuestion();
        void goBack();
    }
}
