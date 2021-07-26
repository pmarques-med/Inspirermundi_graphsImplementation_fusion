package com.bloomidea.inspirers.events;

/**
 * Created by michellobato on 21/04/17.
 */

public class GodfatherAskedEvent {
    private String nid;
    private String status;
    private String requestType;

    public GodfatherAskedEvent(String nid, String status, String requestType) {
        this.nid = nid;
        this.status = status;
        this.requestType = requestType;
    }

    public String getNid() {
        return nid;
    }

    public String getStatus() {
        return status;
    }

    public String getRequestType() {
        return requestType;
    }
}
