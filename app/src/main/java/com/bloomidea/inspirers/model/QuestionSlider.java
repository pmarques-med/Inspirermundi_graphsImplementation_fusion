package com.bloomidea.inspirers.model;

import java.io.Serializable;

/**
 * Created by michellobato on 26/04/17.
 */

public class QuestionSlider extends Question implements Serializable {
    //from 0-100
    private int totalSelected;
    private boolean userSelected;

    public QuestionSlider(Long id, String nid, String question, String type, int totalSelected, boolean userSelected) {
        super(id, nid, question, type);
        this.totalSelected = totalSelected;
        this.userSelected = userSelected;
    }

    public QuestionSlider(QuestionSlider q){
        super(q);
        this.totalSelected = q.getTotalSelected();
        this.userSelected = q.isUserSelected();
    }

    public int getTotalSelected() {
        return totalSelected;
    }

    public void setTotalSelected(int totalSelected, boolean userSelected) {
        this.totalSelected = totalSelected;
        this.userSelected = userSelected;
    }

    public boolean isUserSelected() {
        return userSelected;
    }

    @Override
    public void updateFrom(Question q) {
        if(q instanceof QuestionSlider){
            QuestionSlider aux = (QuestionSlider) q;

            totalSelected = aux.getTotalSelected();
            userSelected = aux.isUserSelected();
        }
    }
}
