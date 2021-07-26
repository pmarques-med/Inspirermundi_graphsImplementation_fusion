package com.bloomidea.inspirers.adapter;

import android.content.Context;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.bloomidea.inspirers.QuestionFragment;
import com.bloomidea.inspirers.model.Question;

import java.util.ArrayList;

/**
 * Created by michellobato on 26/04/17.
 */

public class QuestionsPagerAdapter extends FragmentPagerAdapter {
    private Context context;

    private ArrayList<Question> listQuestions;
    private boolean alreadyAnswered;

    private QuestionFragment.OnQuestionFragmentInteractionListener listenerAux;

    public QuestionsPagerAdapter(FragmentManager fm, Context context, ArrayList<Question> listQuestions, boolean alreadyAnswered, QuestionFragment.OnQuestionFragmentInteractionListener listenerAux) {
        super(fm);
        this.context = context;

        this.listQuestions = listQuestions;

        this.listenerAux = listenerAux;

        this.alreadyAnswered = alreadyAnswered;
    }

    @Override
    public int getCount() {
        return listQuestions.size();
    }

    @Override
    public Fragment getItem(int position) {
        Question aux = listQuestions.get(position);

        QuestionFragment auxFragment = QuestionFragment.newInstance(aux,alreadyAnswered);
        auxFragment.setmListener(listenerAux);

        return auxFragment;
    }
}
