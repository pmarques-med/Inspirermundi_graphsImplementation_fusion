package com.bloomidea.inspirers;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

import com.bloomidea.inspirers.model.Answer;
import com.bloomidea.inspirers.model.Question;
import com.bloomidea.inspirers.model.QuestionMultipleChoice;
import com.bloomidea.inspirers.utils.Utils;


public class QuestionFragment extends Fragment {
    private static final String ARG_QUESTION = "ARG_QUESTION";
    private static final String ARG_ALREADDY_ANSWERED = "ARG_ALREADDY_ANSWERED";

    private Question question;
    private boolean alreadyAnswered;

    private View rootView;

    private OnQuestionFragmentInteractionListener mListener;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public static QuestionFragment newInstance(Question question, boolean alreadyAnswered) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();

        args.putSerializable(ARG_QUESTION, question);
        args.putBoolean(ARG_ALREADDY_ANSWERED, alreadyAnswered);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(ARG_QUESTION);
            alreadyAnswered = getArguments().getBoolean(ARG_ALREADDY_ANSWERED, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_question, container, false);

        QuestionMultipleChoice auxQuestion = (QuestionMultipleChoice) question;

        ((TextView) rootView.findViewById(R.id.question_title)).setText(auxQuestion.getQuestion());

        RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.question_hypothesis);
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int idAux = 0;
        for(Answer a : auxQuestion.getListAnswers()){
            RadioButton radioButton = (RadioButton) vi.inflate(R.layout.layout_radiobutton, null);

            radioButton.setId(idAux);
            idAux++;

            radioButton.setChecked(a.isSelected());

            radioButton.setText(a.getText());

            radioGroup.addView(radioButton);

            View viewSeparator = new View(getContext());
            viewSeparator.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin)));
            radioGroup.addView(viewSeparator);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                updateQuestionInfo();

                mListener.updateQuestionAnswer(question);
            }
        });

        Utils.setRadioGroupEnable(radioGroup,!alreadyAnswered);

        return rootView;
    }

    public void setmListener(OnQuestionFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnQuestionFragmentInteractionListener {
        void updateQuestionAnswer(Question q);
    }

    public void updateQuestionInfo(){
        RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.question_hypothesis);

        updateQuestionMultipleChoice(radioGroup.getCheckedRadioButtonId());
    }

    public void updateQuestionMultipleChoice(int checkedRadioButtonId) {
        QuestionMultipleChoice auxQuestion = (QuestionMultipleChoice) question;

        for (Answer a : auxQuestion.getListAnswers()){
            a.setSelected(false);
        }

        if(checkedRadioButtonId!=-1){
            auxQuestion.getListAnswers().get(checkedRadioButtonId).setSelected(true);
        }
    }
}
