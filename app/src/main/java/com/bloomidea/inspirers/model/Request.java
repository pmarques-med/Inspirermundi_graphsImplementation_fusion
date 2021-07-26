package com.bloomidea.inspirers.model;

import java.util.GregorianCalendar;

/**
 * Created by michellobato on 07/04/17.
 */

public class Request {
    public static final String TYPE_GODFATHER ="godfather";
    public static final String TYPE_GODCHILD ="godchild";

    public static final String STATUS_WAITING = "waiting";
    public static final String STATUS_ACCEPT = "accept";
    public static final String STATUS_REJECT = "reject";
    public static final String STATUS_DELETE = "deleted";

    private String nid;
    private String requestType;
    private String statusRequest;
    private User profile;
    private GregorianCalendar created;

    public Request(String nid, String requestType, String statusRequest, User profile, GregorianCalendar created) {
        this.nid = nid;
        this.requestType = requestType;
        this.statusRequest = statusRequest;
        this.profile = profile;
        this.created = created;
    }

    public String getNid() {
        return nid;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getStatusRequest() {
        return statusRequest;
    }

    public User getProfile() {
        return profile;
    }

    public GregorianCalendar getCreated() {
        return created;
    }

    public void accepted() {
        statusRequest = STATUS_ACCEPT;
    }
}
