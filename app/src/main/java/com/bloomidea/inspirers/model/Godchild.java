package com.bloomidea.inspirers.model;

import java.util.GregorianCalendar;

/**
 * Created by michellobato on 10/04/17.
 */

public class Godchild extends Request {
    private int userBuzz;
    private String revisionId;
    private GregorianCalendar dateLastBuzz;

    public Godchild(String nid, String requestType, String statusRequest, User profile, GregorianCalendar created, int userBuzz, String revisionId, GregorianCalendar dateLastBuzz) {
        super(nid, requestType, statusRequest, profile, created);
        this.userBuzz = userBuzz;
        this.revisionId = revisionId;
        this.dateLastBuzz = dateLastBuzz;
    }

    public int getUserBuzz() {
        return userBuzz;
    }

    public String getRevisionId() {
        return revisionId;
    }

    public void setUserBuzz(int userBuzz, GregorianCalendar dateLastBuzz) {
        this.userBuzz = userBuzz;
        this.dateLastBuzz = dateLastBuzz;
    }

    public GregorianCalendar getDateLastBuzz() {
        return dateLastBuzz;
    }
}
