package com.bloomidea.inspirers.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by michellobato on 26/04/17.
 */

public class QuestionMultipleChoice extends Question implements Serializable {
    private ArrayList<Answer> listAnswers;

    public QuestionMultipleChoice(Long id, String nid, String question, String type, ArrayList<Answer> listAnswers) {
        super(id, nid, question, type);
        this.listAnswers = listAnswers;
    }

    public QuestionMultipleChoice(QuestionMultipleChoice q){
        super(q);

        this.listAnswers = new ArrayList<>();
        for(Answer a : q.getListAnswers()){
            listAnswers.add(new Answer(a));
        }
    }

    public ArrayList<Answer> getListAnswers() {
        return listAnswers;
    }

    @Override
    public void updateFrom(Question q) {
        if(q instanceof QuestionMultipleChoice){
            QuestionMultipleChoice aux = (QuestionMultipleChoice) q;

            for(Answer a : listAnswers){
                for(Answer aAux : aux.getListAnswers()){
                    if(aAux.getNid().equals(a.getNid())){
                        a.setSelected(aAux.isSelected());
                        break;
                    }
                }
            }
        }
    }
}
