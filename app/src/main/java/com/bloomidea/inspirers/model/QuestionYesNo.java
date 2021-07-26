package com.bloomidea.inspirers.model;

import java.io.Serializable;

/**
 * Created by michellobato on 26/04/17.
 */

public class QuestionYesNo extends Question implements Serializable {
    private Boolean yes;

    public QuestionYesNo(Long id, String nid, String question, String type, Boolean yes) {
        super(id, nid, question, type);
        this.yes = yes;
    }

    public QuestionYesNo(QuestionYesNo q){
        super(q);
        this.yes = q.isYes();
    }

    public Boolean isYes() {
        return yes;
    }

    public void setYes(Boolean yes) {
        this.yes = yes;
    }

    @Override
    public void updateFrom(Question q) {
        if(q instanceof QuestionYesNo){
            QuestionYesNo aux = (QuestionYesNo) q;

            yes = aux.isYes();
        }
    }
}
