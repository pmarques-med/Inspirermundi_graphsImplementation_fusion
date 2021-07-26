package com.bloomidea.inspirers.events;

/**
 * Created by michellobato on 26/01/2018.
 */

public class ResetMessageCounter {
    private String uid;

    public ResetMessageCounter(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
