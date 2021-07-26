package com.bloomidea.inspirers.model;

import java.util.GregorianCalendar;

/**
 * Created by michellobato on 27/04/17.
 */

public class ListPoll {
    private Long myPollId;
    private Poll poll;
    private GregorianCalendar asweredDate;
    private String nid;
    private String comment;

    public ListPoll(Long myPollId, Poll poll, GregorianCalendar asweredDate, String nid, String comment) {
        this.myPollId = myPollId;
        this.poll = poll;
        this.asweredDate = asweredDate;
        this.nid = nid;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public Poll getPoll() {
        return poll;
    }

    public GregorianCalendar getAsweredDate() {
        return asweredDate;
    }

    public Long getMyPollId() {
        return myPollId;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public void setMyPollId(Long myPollId) {
        this.myPollId = myPollId;
    }
}
