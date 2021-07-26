package com.bloomidea.inspirers.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by michellobato on 16/02/17.
 */

public class Poll implements Serializable{
    public static final String POLL_TYPE_CARAT = "carat_form";
    public static final String POLL_TYPE_DAILY = "daily";
    public static final String POLL_TYPE_WEEKLY = "weekly";

    private Long id;
    private String title;
    private String poolType;
    private GregorianCalendar updatedDate;
    private String nid;

    private ArrayList<Question> listQuestions;

    public Poll(Long id, String title, String poolType, GregorianCalendar updatedDate, ArrayList<Question> listQuestions, String nid) {
        this.id = id;
        this.title = title;
        this.poolType = poolType;
        this.updatedDate = updatedDate;
        this.listQuestions = listQuestions;
        this.nid = nid;
    }

    public Poll(Poll poll) {
        this.id = poll.getId();
        this.title = poll.getTitle();
        this.poolType = poll.getPoolType();
        this.updatedDate = poll.getUpdatedDate();
        this.nid = poll.getNid();

        this.listQuestions = new ArrayList<>();
        for(Question q : poll.getListQuestions()) {
            if(q instanceof QuestionMultipleChoice){
                listQuestions.add(new QuestionMultipleChoice((QuestionMultipleChoice)q));
            }else if(q instanceof QuestionSlider){
                listQuestions.add(new QuestionSlider((QuestionSlider)q));
            }else{
                //QuestionYesNo
                listQuestions.add(new QuestionYesNo((QuestionYesNo)q));
            }
        }
    }

    public String getNid() {
        return nid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getPoolType() {
        return poolType;
    }

    public GregorianCalendar getUpdatedDate() {
        return updatedDate;
    }

    public ArrayList<Question> getListQuestions() {
        return listQuestions;
    }
}
