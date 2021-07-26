package com.bloomidea.inspirers.model;

import java.io.Serializable;

/**
 * Created by michellobato on 26/04/17.
 */

public abstract class Question implements Serializable{
    public static final String QUESTION_TYPE_SLIDER = "Slider";
    public static final String QUESTION_TYPE_MULTIPLE_CHOICE = "Multiple";
    public static final String QUESTION_TYPE_YES_NO = "True/false";

    private Long id;
    private String nid;
    private String question;
    private String type;

    public abstract void updateFrom(Question q);

    public Question(Long id, String nid, String question, String type) {
        this.id = id;
        this.nid = nid;
        this.question = question;
        this.type = type;
    }

    public Question(Question q) {
        this.id = q.getId();
        this.nid = q.getNid();
        this.question = q.getQuestion();
        this.type = q.getType();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
